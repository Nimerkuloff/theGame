package com.car.game.tools;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;


public class BodyHolder
{

    protected static final int DIRECTION_FORWARD = 1;
    protected static final int DIRECTION_BACKWARD = 2;
    private static final float DRIFT_OFFSET = 1.0f;
    private static final int DIRECTION_NONE = 0;
    private final Body mBody;
    private final int mId;
    private Vector2 mForwardSpeedVec;
    private float mDrift = 1.0f;


    public BodyHolder(final Body mBody)
    {

        this.mBody = mBody;
        mId = -1;
    }

    public BodyHolder(final Vector2 position, final Vector2 size,
                      final BodyDef.BodyType type, final World world,
                      final float density, final boolean sensor,
                      final int id)
    {
        mBody = ShapeFactory.createRectangle(position, size, type, world, density, sensor);
        this.mId = id;
    }


    public void update(final float delta)
    {
        if (mDrift < 1) {

            mForwardSpeedVec = getForwardVelocity();
            Vector2 mLateralSpeedVec = getLateralVelocity();

            if (mLateralSpeedVec.len() < DRIFT_OFFSET && mId > 1) { //stop front wheels to drift after the turn
                killDrift();
            } else {
                handleDrift();
            }
        }
    }

    public void setDrift(final float drift)
    {

        this.mDrift = drift;
    }

    public Body getBody()
    {

        return mBody;
    }


    private void handleDrift()
    {
        final Vector2 forwardSpeed = getForwardVelocity();
        final Vector2 lateralSpeed = getLateralVelocity();
        mBody.setLinearVelocity(
                forwardSpeed.x + lateralSpeed.x * mDrift,
                forwardSpeed.y + lateralSpeed.y * mDrift);
    }


    private Vector2 getForwardVelocity()
    {
        //пригодится для актуализации вектора скорости на поворотах

        Vector2 currentNormal = mBody.getWorldVector(new Vector2(0, 1));
        float dotProduct = currentNormal.dot(mBody.getLinearVelocity());
        //dotProduct -- скалярное произведение
        return multiply(dotProduct, currentNormal);
    }

    private Vector2 getLateralVelocity()
    {
        final Vector2 currentNormal = mBody.getWorldVector(new Vector2(1, 0));
        final float dotProduct = currentNormal.dot(mBody.getLinearVelocity());
        return multiply(dotProduct, currentNormal);
    }

    private void killDrift()
    {

        mBody.setLinearVelocity(mForwardSpeedVec);
    }


    protected int direction()
    {
        final float tolerance = 0.2f;//to stay still when not driving
        if (getLocalVelocity().y < -tolerance) {
            return DIRECTION_BACKWARD;
        } else if (getLocalVelocity().y > tolerance) {
            return DIRECTION_FORWARD;
        } else {
            return DIRECTION_NONE;
        }
    }

    private Vector2 getLocalVelocity()
    {
        return mBody.getLocalVector(mBody.getLinearVelocityFromLocalPoint(new Vector2(0, 0)));
    }

    private Vector2 multiply(float a, Vector2 v)
    {

        return new Vector2(a * v.x, a * v.y);
    }

}
