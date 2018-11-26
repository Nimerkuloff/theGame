package com.car.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.car.game.tools.MapLoader;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.Gdx.app;
import static com.car.game.Constants.DEFAULT_AXIS_SENS;
import static com.car.game.Constants.DEFAULT_ZOOM;
import static com.car.game.Constants.DRIFT;
import static com.car.game.Constants.DRIVE_DIRECTION_BACKWARD;
import static com.car.game.Constants.DRIVE_DIRECTION_FORWARD;
import static com.car.game.Constants.DRIVE_DIRECTION_NONE;
import static com.car.game.Constants.DRIVE_SPEED;
import static com.car.game.Constants.GRAVITY;
import static com.car.game.Constants.MAX_HIT;
import static com.car.game.Constants.MAX_SPEED;
import static com.car.game.Constants.MIN_HIT;
import static com.car.game.Constants.PPM;
import static com.car.game.Constants.TURN_DIRECTION_LEFT;
import static com.car.game.Constants.TURN_DIRECTION_NONE;
import static com.car.game.Constants.TURN_DIRECTION_RIGHT;
import static com.car.game.Constants.TURN_SPEED;


public class PlayScreen implements Screen
{


    private final SpriteBatch mBatch;
    private final Stage mStage;
    //SpriteBatch is used for effective drawing of multiple sprites
    private final World mWorld;
    private final Box2DDebugRenderer mB2dr;
    private final OrthographicCamera mCamera;
    //Orthographic - size of the object does not change according to the distance to the camera
    private final Viewport mViewport;
    //Viewport is where camera is placed
    private final Body mPlayer;
    private final MapLoader mMapLoader;

    public double mScore = 55;
    public double milestone = 50;
    public int tauntIndex = 0;

    private int mDriveDirection = DRIVE_DIRECTION_NONE;
    private int mTurnDirection = TURN_DIRECTION_NONE;
    private String mStringScore;
    private String mTaunt = "Not yet";
    private BitmapFont font;


    private Controller mController;

    public PlayScreen()
    {
        mBatch = new SpriteBatch();
        mWorld = new World(GRAVITY, true);//sleep is used to prevent redundant calculations
        mB2dr = new Box2DDebugRenderer();
        mCamera = new OrthographicCamera();
        mCamera.zoom = DEFAULT_ZOOM;
        mViewport = new FitViewport(640 / PPM, 480 / PPM, mCamera);
        mMapLoader = new MapLoader(mWorld);
        mPlayer = mMapLoader.placePlayer();
        mController = null;

        font = new BitmapFont();

        font.setColor(0.5f, 0.4f, 0, 1);

        try {
            mController = Controllers.getControllers().get(0);
        } catch (IndexOutOfBoundsException ex) {

            app.error("ControllerErr", "Is controller plugged?");

        }

        mStage = new Stage(mViewport, mBatch);
    }

    @Override
    public void show()
    {

    }

    @Override
    public void render(float delta)
    {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        handleInput();
        processInput();
        processDamage();

        update(delta);
        handleDrift();


        mBatch.begin();

        font.setColor(0.5f, 0.4f, 0, 1);

        font.draw(mBatch, "Score: " + mStringScore, 640 / PPM, 480 / PPM);
        font.draw(mBatch, mTaunt, 300 / PPM, -10);

        mBatch.end();


        draw();
    }

    private void processDamage()

    {
        int numContacts = mWorld.getContactCount();

        if (numContacts > 0) {

            double randomHit = MIN_HIT + (Math.random() * (MAX_HIT - MIN_HIT));
            mStringScore = String.valueOf(mScore -= randomHit);
            Gdx.app.log("SCOOOORE", mStringScore);
        }

        if (-mScore > milestone) {
            List<String> taunts = createListOfTaunts();

            mTaunt = taunts.get(tauntIndex);
            tauntIndex++;
            milestone += 50;
        }


    }

    private List<String> createListOfTaunts()
    {
        List<String> taunts = new ArrayList<String>();
        taunts.add("Driving, heh?");
        taunts.add("Try to stay positive");
        taunts.add("\"Go on, prove me wrong. Destroy the fabric of the universe. See if I care. \" ― Terry Pratchett)");
        taunts.add("\" I often wonder, in a catfight, when one doesn't want to fight, if the other cat calls it a pussy.\"― Anthony Liccione");
        taunts.add("\"Is that all you've got? A few tricks and quick feet? That's no way to enforce your bold tongue!\" ― T. A. Miles, Six Celestial Swords");
        taunts.add("\"One day, in retrospect, the years of struggle will strike you as the most beautiful.\" ― Sigmund Freud");
        taunts.add("\"What you stay focused on will grow.\" ― Roy T. Bennett");
        taunts.add("NO, NO, NO stop!!! There aren't any endings.");
        taunts.add("\"You can get a thousand no's from people, and only one \"yes\" from God.\" ― Tyler Perry");

        return taunts;
    }

    private void createCollisionListener()
    {
        mWorld.setContactListener(new ContactListener()
        {

            @Override
            public void beginContact(Contact contact)
            {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                Gdx.app.log("beginContact", "between " + fixtureA.toString() + " and " + fixtureB.toString());
            }

            @Override
            public void endContact(Contact contact)
            {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                Gdx.app.log("endContact", "between " + fixtureA.toString() + " and " + fixtureB.toString());
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold)
            {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse)
            {
            }

        });
    }

    private void handleDrift()
    {
        Vector2 forwardSpeed = getForwardVelocity();
        Vector2 lateralSpeed = getLateralVelocity();
        mPlayer.setLinearVelocity(forwardSpeed.x + lateralSpeed.x + DRIFT, forwardSpeed.y + lateralSpeed.y + DRIFT);
    }

    private void processInput()
    {
        Vector2 baseVector = new Vector2();

        if (mTurnDirection == TURN_DIRECTION_RIGHT) {
            mPlayer.setAngularVelocity(-TURN_SPEED);
        } else if (mTurnDirection == TURN_DIRECTION_LEFT) {
            mPlayer.setAngularVelocity(TURN_SPEED);
        } else if (mTurnDirection == TURN_DIRECTION_NONE && mPlayer.getAngularVelocity() != 0) {
            mPlayer.setAngularVelocity(0.0f);
        }

        if (mDriveDirection == DRIVE_DIRECTION_FORWARD) {
            baseVector.set(0, DRIVE_SPEED);
        } else if (mDriveDirection == DRIVE_DIRECTION_BACKWARD) {
            baseVector.set(0, -DRIVE_SPEED);
        }

        if (!baseVector.isZero() && mPlayer.getLinearVelocity().len() < MAX_SPEED) {
            mPlayer.applyForceToCenter(mPlayer.getWorldVector(baseVector), true);
        }
    }

    private Vector2 getForwardVelocity()
    {
        //пригодится для актуализации вектора скорости на поворотах

        Vector2 currentNormal = mPlayer.getWorldVector(new Vector2(0, 1));
        float dotProduct = currentNormal.dot(mPlayer.getLinearVelocity());
        //dotProduct -- скалярное произведение
        return multiply(dotProduct, currentNormal);
    }

    private Vector2 getLateralVelocity()
    {
        Vector2 currentNormal = mPlayer.getWorldVector(new Vector2(1, 0));
        float dotProduct = currentNormal.dot(mPlayer.getLinearVelocity());
        return multiply(dotProduct, currentNormal);
    }

    private Vector2 multiply(float a, Vector2 v)
    {

        return new Vector2(a * v.x, a * v.y);
    }

    private void handleInputController()
    {
        if (mController.getAxis(3) > DEFAULT_AXIS_SENS) {
            mDriveDirection = DRIVE_DIRECTION_BACKWARD;
        } else if (mController.getAxis(3) < -DEFAULT_AXIS_SENS) {
            mDriveDirection = DRIVE_DIRECTION_FORWARD;
        } else {
            mDriveDirection = DRIVE_DIRECTION_NONE;
        }
        if (mController.getAxis(2) > DEFAULT_AXIS_SENS) {
            mTurnDirection = TURN_DIRECTION_RIGHT;
        } else if (mController.getAxis(2) < -DEFAULT_AXIS_SENS) {
            mTurnDirection = TURN_DIRECTION_LEFT;
        } else {
            mTurnDirection = TURN_DIRECTION_NONE;
        }

    }

    private void handleInput()
    {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            mDriveDirection = DRIVE_DIRECTION_FORWARD;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            mDriveDirection = DRIVE_DIRECTION_FORWARD;
        } else {
            mDriveDirection = DRIVE_DIRECTION_NONE;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mTurnDirection = TURN_DIRECTION_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mTurnDirection = TURN_DIRECTION_RIGHT;
        } else {
            mTurnDirection = TURN_DIRECTION_NONE;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void draw()
    {
        mBatch.begin();
        font.draw(mBatch, "Score: " + mStringScore, 3 / PPM, 100 / PPM);
        mBatch.end();

        mBatch.setProjectionMatrix(mCamera.combined);
        //mCamera.combined -  matrix that describes where things from game world should be
        //                    rendered on the screen
        //setProjectionMatrix instructs the batch to use that combined matrix

        mB2dr.render(mWorld, mCamera.combined);

    }

    private void update(final float delta)
    {
        mCamera.position.set(mPlayer.getPosition(), 0);
        mCamera.update();

        mWorld.step(delta, 6, 2);//because it works better with 6,2
    }

    @Override
    public void resize(int width, int height)
    {

        mViewport.update(width, height);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {
        mBatch.dispose();
        mWorld.dispose();
        mB2dr.dispose();
        mMapLoader.dispose();
    }
}
