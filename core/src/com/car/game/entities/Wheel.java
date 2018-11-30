package com.car.game.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.car.game.tools.BodyHolder;


class Wheel extends BodyHolder
{
    static final int UPPER_LEFT = 0;
    static final int UPPER_RIGHT = 1;
    static final int DOWN_LEFT = 2;
    static final int DOWN_RIGHT = 3;
    private final boolean mPowered;
    private final Car mCar;


    Wheel(final Vector2 position,
          final Vector2 size,
          final BodyDef.BodyType type,
          final World world, final float density, final int id,
          final Car car,
          final boolean powered//true if there is an acceleration
    )
    {
        super(position, size, type, world, density, true, id);
        this.mCar = car;//good example of "final" importance here
        this.mPowered = powered;

    }

    void setAngle(final float angle)
    {
        getBody().setTransform(getBody().getPosition(),
                mCar.getBody().getAngle() + MathUtils.degreesToRadians * angle);
    }

    boolean isPowered()
    {
        return mPowered;
    }
}
