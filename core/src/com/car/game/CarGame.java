package com.car.game;

import com.badlogic.gdx.Game;
import com.car.game.screens.Menu;

public class CarGame extends Game
{
    @Override
    public void create()
    {
        setScreen(new Menu());

    }

    @Override
    public void render()
    {
        super.render();
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }
}
