package com.car.game.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.car.game.tools.BodyHolder;
import com.car.game.tools.MapLoader;

import static com.car.game.Constants.DRIVE_DIRECTION_BACKWARD;
import static com.car.game.Constants.DRIVE_DIRECTION_FORWARD;
import static com.car.game.Constants.DRIVE_DIRECTION_NONE;
import static com.car.game.Constants.PPM;
import static com.car.game.Constants.TURN_DIRECTION_LEFT;
import static com.car.game.Constants.TURN_DIRECTION_NONE;
import static com.car.game.Constants.TURN_DIRECTION_RIGHT;


public class Car extends BodyHolder
{

    public static final int DRIVE_2WD = 0;
    private static final int DRIVE_4WD = 1;

    private static final Vector2 WHEEL_SIZE = new Vector2(16, 32);
    private static final float LINEAR_DAMPING = 0.5f;
    private static final float RESTITUTION = 0.2f;

    private static final float MAX_WHEEL_ANGLE = 20.0f;
    private static final float WHEEL_TURN_INCREMENT = 1.0f;

    private static final float WHEEL_OFFSET_X = 64;
    private static final float WHEEL_OFFSET_Y = 80;
    private static final int WHEEL_NUMBER = 4;

    private static final float BREAK_POWER = 1.3f;
    private static final float REVERSE_POWER = 0.5f;
    private final Array<Wheel> mAllWheels = new Array<Wheel>();
    private final Array<Wheel> mRevolvingWheels = new Array<Wheel>();
    private int mDriveDirection = DRIVE_DIRECTION_NONE;
    private int mTurnDirection = TURN_DIRECTION_NONE;
    private float mCurrentWheelAngle = 0;
    private float mDrift;
    private float mCurrentMaxSpeed;

    private float mAcceleration;

    public Car(final float maxSpeed, final float drift, final float acceleration,
               final MapLoader mapLoader, int wheelDrive, World world)
    {
        super(mapLoader.placePlayer());
        //It is used inside a sub-class method definition to call a method defined in the super class.
        this.mCurrentMaxSpeed = maxSpeed;
        this.mDrift = drift;
        this.mAcceleration = acceleration;
        getBody().setLinearDamping(LINEAR_DAMPING);
        getBody().getFixtureList().get(0).setRestitution(RESTITUTION);
        createWheels(world, wheelDrive);
    }

    private void createWheels(World world, int wheelDrive)
    {
        for (int i = 0; i < WHEEL_NUMBER; i++) {
            float xOffset;
            float yOffset;

            switch (i) {
                case Wheel.UPPER_LEFT:
                    xOffset = -WHEEL_OFFSET_X;
                    yOffset = WHEEL_OFFSET_Y;
                    break;
                case Wheel.UPPER_RIGHT:
                    xOffset = WHEEL_OFFSET_X;
                    yOffset = WHEEL_OFFSET_Y;
                    break;
                case Wheel.DOWN_LEFT:
                    xOffset = -WHEEL_OFFSET_X;
                    yOffset = -WHEEL_OFFSET_Y;
                    break;
                case Wheel.DOWN_RIGHT:
                    xOffset = WHEEL_OFFSET_X;
                    yOffset = -WHEEL_OFFSET_Y;
                    break;
                default:
                    throw new IllegalArgumentException
                            ("Wheel number not supported. " + "Create logic for positioning " +
                                    "wheel with number " + i);
            }

            final boolean powered = wheelDrive == DRIVE_4WD || (wheelDrive == DRIVE_2WD && i < 2);

            final Wheel wheel = new Wheel(
                    new Vector2(
                            getBody().getPosition().x * PPM + xOffset,
                            getBody().getPosition().y * PPM + yOffset
                    ),
                    WHEEL_SIZE,
                    BodyDef.BodyType.DynamicBody,
                    world,
                    0.4f,
                    i,
                    this,
                    powered);

            if (i < 2) {
                final RevoluteJointDef jointDef = new RevoluteJointDef();
                jointDef.initialize(getBody(), wheel.getBody(), wheel.getBody().getWorldCenter());
                jointDef.enableMotor = false;
                world.createJoint(jointDef);
            } else {
                final PrismaticJointDef jointDef = new PrismaticJointDef();
                jointDef.initialize(getBody(), wheel.getBody(), wheel.getBody()
                        .getWorldCenter(), new Vector2(1, 0));
                jointDef.enableLimit = true;
                jointDef.lowerTranslation = jointDef.upperTranslation = 0;
                world.createJoint(jointDef);
            }

            mAllWheels.add(wheel);
            if (i < 2) {
                mRevolvingWheels.add(wheel);
            }
            wheel.setDrift(mDrift);
        }

    }


    private void processInput()
    {
        Vector2 baseVector = new Vector2();


        if (mTurnDirection == TURN_DIRECTION_LEFT) {
            if (mCurrentWheelAngle < 0) {
                mCurrentWheelAngle = 0;
            }
            mCurrentWheelAngle = Math.min(mCurrentWheelAngle += WHEEL_TURN_INCREMENT, MAX_WHEEL_ANGLE);
            //save some lines with Math.min to restrict WheelAngle
        } else if (mTurnDirection == TURN_DIRECTION_RIGHT) {
            if (mCurrentWheelAngle > 0) {
                mCurrentWheelAngle = 0;
            }
            mCurrentWheelAngle = Math.max(mCurrentWheelAngle -= WHEEL_TURN_INCREMENT, -MAX_WHEEL_ANGLE);
        } else {
            mCurrentWheelAngle = 0;
        }

        for (final Wheel wheel : new Array.ArrayIterator<Wheel>(mRevolvingWheels)) {
            wheel.setAngle(mCurrentWheelAngle);
        }


        if (mDriveDirection == DRIVE_DIRECTION_FORWARD) {
            baseVector.set(0, mAcceleration);
        } else if (mDriveDirection == DRIVE_DIRECTION_BACKWARD) {
            if (direction() == DIRECTION_BACKWARD) {
                baseVector.set(0, -mAcceleration * REVERSE_POWER);
            } else if (direction() == DIRECTION_FORWARD) {
                baseVector.set(0, -mAcceleration * BREAK_POWER);
            } else {
                baseVector.set(0, -mAcceleration);
            }
        }


        if (getBody().getLinearVelocity().len() < mCurrentMaxSpeed) {
            for (final Wheel wheel : new Array.ArrayIterator<Wheel>(mAllWheels)) {
                if (wheel.isPowered()) {
                    wheel.getBody()
                            .applyForceToCenter(wheel.getBody().getWorldVector(baseVector), true);
                }
            }
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
        //todo показать использование коллекций
        for (final Wheel wheel : new Array.ArrayIterator<Wheel>(mAllWheels)) {
            wheel.update(delta);
        }
    }
}
