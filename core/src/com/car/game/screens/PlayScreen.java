package com.car.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.car.game.tools.ShapeFactory;

import static com.car.game.Constants.DEFAULT_ZOOM;
import static com.car.game.Constants.GRAVITY;
import static com.car.game.Constants.PPM;

/**
 * Created by merkulov on 18.11.2018.
 */

public class PlayScreen implements Screen {

    //SpriteBatch is used for
    //effective drawing of multiple sprites
    private final SpriteBatch mBatch;

    private final World mWorld;
    private final Box2DDebugRenderer mB2dr;

    //Orthographic == 2d
    private final OrthographicCamera mCamera;

    //It is where camera is placed
    private  final Viewport mViewport;

    private final Body mPlayer;

    public PlayScreen() {
        mBatch = new SpriteBatch();
        mWorld = new World(GRAVITY, true);//sleep is used to prevent redundant calculations
        mB2dr = new Box2DDebugRenderer();
        mCamera = new OrthographicCamera();
        mCamera.zoom= DEFAULT_ZOOM;
        mViewport= new FitViewport(640/PPM,480/PPM,mCamera);
        mPlayer= ShapeFactory.createRectangle(new Vector2(0,0),new Vector2(64,128),
                                              BodyDef.BodyType.DynamicBody, mWorld, 0.4f);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        draw();
    }

    private void draw() {

    }

    private void update(final float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        mBatch.dispose();
        mWorld.dispose();
        mB2dr.dispose();
    }
}
