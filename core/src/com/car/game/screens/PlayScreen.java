package com.car.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.car.game.entities.Car;
import com.car.game.tools.MapLoader;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.Gdx.app;
import static com.car.game.Constants.DEFAULT_ZOOM;
import static com.car.game.Constants.DRIFT;
import static com.car.game.Constants.DRIVE_DIRECTION_BACKWARD;
import static com.car.game.Constants.DRIVE_DIRECTION_FORWARD;
import static com.car.game.Constants.DRIVE_DIRECTION_NONE;
import static com.car.game.Constants.GRAVITY;
import static com.car.game.Constants.MAX_HIT;
import static com.car.game.Constants.MAX_SPEED;
import static com.car.game.Constants.MIN_HIT;
import static com.car.game.Constants.PPM;
import static com.car.game.Constants.TURN_DIRECTION_LEFT;
import static com.car.game.Constants.TURN_DIRECTION_NONE;
import static com.car.game.Constants.TURN_DIRECTION_RIGHT;


public class PlayScreen implements Screen
{

    private static final float FONT_SCALE = 0.9f;
    private static final float CAMERA_ZOOM = 0.3f;
    private final SpriteBatch mBatch;
    private final Stage mStage;

    //SpriteBatch is used for effective drawing of multiple sprites
    private final World mWorld;
    private final Box2DDebugRenderer mB2dr;
    private final OrthographicCamera mCamera;
    //Orthographic - size of the object does not change according to the distance to the camera
    private final Viewport mViewport;
    //Viewport is where camera is placed
    private final Car mPlayer;
    private final MapLoader mMapLoader;

    private double mScore = 55;
    private double milestone = 50;
    private int tauntIndex = 0;
    private int toInt = 0;


    private String mStringScore;
    private String mTaunt = "Not yet";
    private BitmapFont mFont;
    private BitmapFont mFontTaunt;

    private Controller mXboxController;

    PlayScreen()
    {
        mBatch = new SpriteBatch();
        mWorld = new World(GRAVITY, true);//sleep is used to prevent redundant calculations
        mB2dr = new Box2DDebugRenderer();
        mCamera = new OrthographicCamera();
        mCamera.zoom = DEFAULT_ZOOM;
        mViewport = new FitViewport(640 / PPM, 480 / PPM, mCamera);
        mMapLoader = new MapLoader(mWorld);
        mPlayer = new Car(MAX_SPEED, DRIFT, 50, mMapLoader, Car.DRIVE_2WD, mWorld);


        mXboxController = null;

        mFont = new BitmapFont();
        mFontTaunt = new BitmapFont();

        mFontTaunt.setColor(0.5f, 0.4f, 0, 1);
        mFontTaunt.getData().setScale(FONT_SCALE - 0.5f);
        mFontTaunt.getRegion()
                .getTexture()
                .setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        mFont.setColor(0.5f, 0.4f, 0, 1);
        mFont.getData().setScale(FONT_SCALE);
        try {
            mXboxController = Controllers.getControllers().get(0);
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
        processDamage();
        update(delta);


        mBatch.begin();

        mFont.draw(mBatch, "Score: " + mStringScore, 300 / PPM, 10 / PPM);
        mFontTaunt.draw(mBatch, mTaunt, 300 / PPM, -10);

        mBatch.end();


        draw();
    }

    private void processDamage()
    {
        int numContacts = mWorld.getContactCount();
        if (numContacts > 0) {

            double randomHit = MIN_HIT + (Math.random() * (MAX_HIT - MIN_HIT));
            mScore -= randomHit;
            toInt = (int) mScore;
            mStringScore = String.valueOf(toInt);
            Gdx.app.log("SCOOOORE", mStringScore);
        }

        if (-mScore > milestone) {
            //todo показать использование коллекций
            List<String> taunts = createListOfTaunts();


            mTaunt = taunts.get(tauntIndex);

            if (tauntIndex < taunts.size() - 1) {
                tauntIndex++;
            }
            milestone += 50;

        }


    }
    private List<String> createListOfTaunts()
    {
        List<String> taunts = new ArrayList<String>();
        taunts.add("Driving, heh?");
        taunts.add("Try to stay POSITIVE");
        taunts.add("\"Go  on,  prove me wrong. \nDestroy the fabric of the universe.\n See if I care. \" \n― Terry Pratchett)");
        taunts.add("\"I  often  wonder,  in  a  catfight,\n  when  one  doesn't  want  to  fight,\n if  the other cat calls it a pussy.\"\n― Anthony Liccione");
        taunts.add("\"Is that all you've got? \nA few tricks and quick feet? \n\" \n― T. A. Miles");
        taunts.add("\"One day, in retrospect,\n the years of struggle will strike you as the most beautiful.\"\n ― Sigmund Freud");
        taunts.add("\"What you stay focused on will grow.\" ―\n Roy T. Bennett");
        taunts.add("NO, NO, NO stop!!! Game has no end.");
        taunts.add("No, please!");
        taunts.add("Seriously it's time to stop...");
        taunts.add("No....");
        taunts.add("\"You can get a thousand no's from people,\n and only one \"yes\" from God.\" ―\n Tyler Perry");
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


    //TODO fix controller
//    private void handleInputController()
//    {
//        if (mXboxController.getAxis(3) > DEFAULT_AXIS_SENS) {
//            = DRIVE_DIRECTION_BACKWARD;
//        } else if (mXboxController.getAxis(3) < -DEFAULT_AXIS_SENS) {
//            mDriveDirection = DRIVE_DIRECTION_FORWARD;
//        } else {
//            mDriveDirection = DRIVE_DIRECTION_NONE;
//        }
//        if (mXboxController.getAxis(2) > DEFAULT_AXIS_SENS) {
//            mTurnDirection = TURN_DIRECTION_RIGHT;
//        } else if (mXboxController.getAxis(2) < -DEFAULT_AXIS_SENS) {
//            mTurnDirection = TURN_DIRECTION_LEFT;
//        } else {
//            mTurnDirection = TURN_DIRECTION_NONE;
//        }
//
//    }
    private void handleInput()
    {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            mPlayer.setDriveDirection(DRIVE_DIRECTION_FORWARD);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            mPlayer.setDriveDirection(DRIVE_DIRECTION_BACKWARD);
        } else {
            mPlayer.setDriveDirection(DRIVE_DIRECTION_NONE);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mPlayer.setTurnDirection(TURN_DIRECTION_LEFT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mPlayer.setTurnDirection(TURN_DIRECTION_RIGHT);
        } else {
            mPlayer.setTurnDirection(TURN_DIRECTION_NONE);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            mCamera.zoom -= CAMERA_ZOOM;
        } else if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            mCamera.zoom += CAMERA_ZOOM;
        }
    }


    private void draw()
    {

        mBatch.setProjectionMatrix(mCamera.combined);
        //mCamera.combined -  matrix that describes where things from game world should be
        //                    rendered on the screen
        //setProjectionMatrix instructs the batch to use that combined matrix

        mB2dr.render(mWorld, mCamera.combined);

    }
    private void update(final float delta)
    {
        mPlayer.update(delta);
        mCamera.position.set(mPlayer.getBody().getPosition(), 0);
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
