package com.car.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.car.game.tools.BodyHolder;

import static com.car.game.Constants.DRIVE_DIRECTION_BACKWARD;
import static com.car.game.Constants.DRIVE_DIRECTION_FORWARD;
import static com.car.game.Constants.DRIVE_DIRECTION_NONE;
import static com.car.game.Constants.DRIVE_SPEED;
import static com.car.game.Constants.MAX_SPEED;
import static com.car.game.Constants.TURN_DIRECTION_LEFT;
import static com.car.game.Constants.TURN_DIRECTION_NONE;
import static com.car.game.Constants.TURN_DIRECTION_RIGHT;
import static com.car.game.Constants.TURN_SPEED;


public class Car extends BodyHolder
{
    private int mDriveDirection = DRIVE_DIRECTION_NONE;
    private int mTurnDirection = TURN_DIRECTION_NONE;

    public Car(Body mBody)
    {
        super(mBody);
        //It is used inside a sub-class method definition to call a method defined in the super class.
        getBody().setLinearDamping(0.5f);
        //USed to control inertia

    }


    private void processInput()
    {
        Vector2 baseVector = new Vector2();


        if (mTurnDirection == TURN_DIRECTION_RIGHT) {
            getBody().setAngularVelocity(-TURN_SPEED);
        } else if (mTurnDirection == TURN_DIRECTION_LEFT) {
            getBody().setAngularVelocity(TURN_SPEED);
        } else if (mTurnDirection == TURN_DIRECTION_NONE && getBody().getAngularVelocity() != 0) {
            getBody().setAngularVelocity(0.0f);
        }

        if (mDriveDirection == DRIVE_DIRECTION_FORWARD) {
            baseVector.set(0, DRIVE_SPEED);
        } else if (mDriveDirection == DRIVE_DIRECTION_BACKWARD) {
            baseVector.set(0, -DRIVE_SPEED);
        }
//todo drift
        if (!baseVector.isZero() && getBody().getLinearVelocity().len() < MAX_SPEED) {
            getBody().applyForceToCenter(getBody().getWorldVector(baseVector), true);
        }
    }

    public void setDriveDirection(final int driveDirection)
    {
        this.mDriveDirection = driveDirection;
    }


    public void setTurnDirection(final int turnDirection)
    {
        this.mTurnDirection = turnDirection;
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
        processInput();
    }
}
