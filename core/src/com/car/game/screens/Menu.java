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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.car.game.Constants.PPM;

public class Menu implements Screen
{
    private static final float DEFAULT_MENU_ZOOM = 24f;
    protected Stage mStage;
    private SpriteBatch mBatch;
    private Viewport mViewport;
    private OrthographicCamera mCamera;
    private Skin mSkin;


    private TextButton.TextButtonStyle textButtonStyle;
    private BitmapFont font;


    public Menu()
    {
        mBatch = new SpriteBatch();


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
        //Stage should controll input:
        Gdx.input.setInputProcessor(mStage);

        //Create Table
        Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();


        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new BitmapFont();


        //Create buttons
        TextButton playButton = new TextButton("Play", textButtonStyle);
        TextButton optionsButton = new TextButton("Options", textButtonStyle);
        TextButton exitButton = new TextButton("Exit", textButtonStyle);

        //Add listeners to buttons
        playButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
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
        mainTable.add(optionsButton);
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
        mSkin.dispose();
    }
}