package com.car.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.car.game.tools.MapLoader;

import static com.car.game.Constants.DEFAULT_ZOOM;
import static com.car.game.Constants.GRAVITY;
import static com.car.game.Constants.PPM;

/**
 * Created by merkulov on 18.11.2018.
 */

public class PlayScreen implements Screen
{

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

        update(delta);
        draw();
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
