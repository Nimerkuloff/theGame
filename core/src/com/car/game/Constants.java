package com.car.game;

import com.badlogic.gdx.math.Vector2;



public class Constants
{
    public static final Vector2 GRAVITY = new Vector2(0, 0);

    public static final float PPM = 50.0f;//pixels per meter

    public static final float DEFAULT_ZOOM = 21f;
    public static final float DEFAULT_AXIS_SENS = 0.23f;

    public static final int DRIVE_DIRECTION_NONE = 0;
    public static final int DRIVE_DIRECTION_FORWARD = 1;
    public static final int DRIVE_DIRECTION_BACKWARD = 2;

    public static final int TURN_DIRECTION_NONE = 0;
    public static final int TURN_DIRECTION_LEFT = 1;
    public static final int TURN_DIRECTION_RIGHT = 2;


    public static final float TURN_SPEED = 2.0f;
    public static final float DRIVE_SPEED = 120.0f;

    public static final float DRIFT = 0.8f;
    public static final float MAX_SPEED = 35.0f;
    public static final float LINEAR_DAMPING = 0.5f;
    public static final float RESTITUTION = 0.2f;//bouncing from walls

    public static final double MIN_HIT = 0.2f;
    public static final double MAX_HIT = 0.5f;


    public static String MAP_NAME = "new_map.tmx";
}

