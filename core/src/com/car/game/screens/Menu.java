package com.car.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.car.game.Constants.MAP_NAME;
import static com.car.game.Constants.PPM;

public class Menu implements Screen
{
    private static final float DEFAULT_MENU_ZOOM = 24f;
    private Stage mStage;
    private Viewport mViewport;
    private OrthographicCamera mCamera;


    public Menu()
    {
        SpriteBatch mBatch = new SpriteBatch();


        mCamera = new OrthographicCamera();
        mCamera.zoom = DEFAULT_MENU_ZOOM;
        mViewport = new FitViewport(640 / PPM, 480 / PPM, mCamera);
        mViewport.apply();

        mCamera.position.set(mCamera.viewportWidth / 2, mCamera.viewportHeight / 2, 0);
        mCamera.update();

        mStage = new Stage(mViewport, mBatch);


    }

    @Override
    public void show()
    {

        Gdx.input.setInputProcessor(mStage);//Stage should control input:


        Table mainTable = new Table();//Set table to fill stage
        mainTable.setFillParent(true);
        mainTable.top();


        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new BitmapFont();

        TextButton playButton = new TextButton("Play", textButtonStyle);
        TextButton anotherMapButton = new TextButton("Another map", textButtonStyle);
        TextButton yetAnotherMapButton = new TextButton("Yet Another map", textButtonStyle);
        TextButton exitButton = new TextButton("Exit", textButtonStyle);


        playButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen());
            }
        });

        anotherMapButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                MAP_NAME = "another_map.tmx";
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen());
            }
        });
        yetAnotherMapButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                MAP_NAME = "yet_another_map.tmx";
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen());
            }
        });
        exitButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Gdx.app.exit();
            }
        });

        //Add buttons to table
        mainTable.add(playButton);
        mainTable.row();
        mainTable.add(anotherMapButton);
        mainTable.row();
        mainTable.add(yetAnotherMapButton);
        mainTable.row();
        mainTable.add(exitButton);

        //Add table to stage
        mStage.addActor(mainTable);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mStage.act();
        mStage.draw();
    }

    @Override
    public void resize(int width, int height)
    {
        mViewport.update(width, height);
        mCamera.position.set(mCamera.viewportWidth / 2, mCamera.viewportHeight / 2, 0);
        mCamera.update();
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

    }
}