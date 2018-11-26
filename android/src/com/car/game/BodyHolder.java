package com.car.game;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.car.game.tools.ShapeFactory;


public class BodyHolder
{

    public static final float DRIFT_OFFSET = 1.0f;
    protected static final int DIRECTION_NONE = 0;
    protected static final int DIRECTION_FORWARD = 1;
    protected static final int DIRECTION_BACKWARD = 2;
    private final Body mBody;
    protected Vector2 mForwardSpeed;
    protected Vector2 mLateralSpeed;
    private float mDrift = 0.99f;

    public BodyHolder(Body mBody)
    {
        this.mBody = mBody;
    }

    public BodyHolder(final Vector2 position, final Vector2 size,
                      final BodyDef.BodyType type, final World world,
                      float density, boolean sensor)
    {
        mBody = ShapeFactory.createRectangle(position, size, type, world, density, sensor);
    }

    public void setDrift(float drift)
    {
        this.mDrift = drift;
    }

    public Body getBody()
    {
        return mBody;
    }

    protected void update(final float delta)
    {

    }

    private void handleDrift()
    {
        Vector2 forwardSpeed = getForwardVelocity();
        Vector2 lateralSpeed = getLateralVelocity();
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
        Vector2 currentNormal = mBody.getWorldVector(new Vector2(1, 0));
        float dotProduct = currentNormal.dot(mBody.getLinearVelocity());
        return multiply(dotProduct, currentNormal);
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
