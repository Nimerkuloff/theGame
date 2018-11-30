package com.car.game.tools;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;


public class BodyHolder
{

    public static final float DRIFT_OFFSET = 1.0f;
    protected static final int DIRECTION_NONE = 0;
    protected static final int DIRECTION_FORWARD = 1;
    protected static final int DIRECTION_BACKWARD = 2;

    private final Body mBody;

    protected Vector2 mForwardSpeedVec;
    protected Vector2 mLateralSpeedVec;

    private float mDrift = 1;


    public BodyHolder(final Body mBody)
    {

        this.mBody = mBody;
    }

    public BodyHolder(final Vector2 position, final Vector2 size,
                      final BodyDef.BodyType type, final World world,
                      float density, boolean sensor)
    {
        mBody = ShapeFactory.createRectangle(position, size, type, world, density, sensor);
    }

    //todo drift
    public void update(final float delta)
    {
        if (mDrift < 1) {

            mForwardSpeedVec = getForwardVelocity();
            mLateralSpeedVec = getLateralVelocity();

            if (mLateralSpeedVec.len() < DRIFT_OFFSET) {
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

    //todo drift
    private void handleDrift()
    {
        final Vector2 forwardSpeed = getForwardVelocity();
        final Vector2 lateralSpeed = getLateralVelocity();
        mBody.setLinearVelocity(forwardSpeed.x + lateralSpeed.x + mDrift, forwardSpeed.y + lateralSpeed.y + mDrift);
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

    public void killDrift()
    {

        mBody.setLinearVelocity(mForwardSpeedVec);
    }


    public int direction()
    {
        final float tolerance = 0.2f;
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
