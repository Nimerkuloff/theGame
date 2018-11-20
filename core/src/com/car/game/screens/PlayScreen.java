package com.car.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.car.game.tools.MapLoader;

import static com.badlogic.gdx.Gdx.app;
import static com.car.game.Constants.DEFAULT_AXIS_SENS;
import static com.car.game.Constants.DEFAULT_ZOOM;
import static com.car.game.Constants.GRAVITY;
import static com.car.game.Constants.PPM;


public class PlayScreen implements Screen
{
    public static final int DRIVE_DIRECTION_NONE = 0;
    public static final int DRIVE_DIRECTION_FORWARD = 1;
    public static final int DRIVE_DIRECTION_BACKWARD = 2;
    public static final int TURN_DIRECTION_NONE = 0;
    public static final int TURN_DIRECTION_LEFT = 1;

    public static final int TURN_DIRECTION_RIGHT = 2;
    private final SpriteBatch mBatch;
    //SpriteBatch is used for effective drawing of multiple sprites
    private final World mWorld;
    private final Box2DDebugRenderer mB2dr;
    private final OrthographicCamera mCamera;
    //Orthographic - size of the object does not change according to the distance to the camera
    private final Viewport mViewport;
    //Viewport is where camera is placed
    private final Body mPlayer;
    private final MapLoader mMapLoader;
    private Controller mController;
    private int mDriveDirection = DRIVE_DIRECTION_NONE;
    private int mTurnDirection = TURN_DIRECTION_NONE;

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
        try {
            mController = Controllers.getControllers().get(0);
        } catch (IndexOutOfBoundsException ex) {
            app.error("ControllerErr", "Is controller plugged?");
        }
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

        update(delta);
        draw();
    }

    private void processInput()
    {
        Vector2 baseVector = new Vector2();

        if (mTurnDirection == TURN_DIRECTION_RIGHT) {
            mPlayer.setAngularVelocity(-2.0f);
        } else if (mTurnDirection == TURN_DIRECTION_LEFT) {
            mPlayer.setAngularVelocity(2.0f);
        } else if (mTurnDirection == TURN_DIRECTION_NONE && mPlayer.getAngularVelocity() != 0) {
            mPlayer.setAngularVelocity(0.0f);
        }

        if (mDriveDirection == DRIVE_DIRECTION_FORWARD) {
            baseVector.set(0, 120.0f);
        } else if (mDriveDirection == DRIVE_DIRECTION_BACKWARD) {
            baseVector.set(0, -120.0f);
        }

        if (!baseVector.isZero()) {
            mPlayer.applyForceToCenter(mPlayer.getWorldVector(baseVector), true);
        }
    }



    private void handleInput()
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
