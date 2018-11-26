package com.car.game.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import static com.car.game.Constants.PPM;

/**
 * Created by merkulov on 18.11.2018.
 */

public class ShapeFactory {

    private ShapeFactory() {}

    public static Body createRectangle(final Vector2 position, final Vector2 size,
                                       final BodyDef.BodyType type, final World world,
                                       float density, boolean sensor)
    {
        //define properties of a body
        final BodyDef bdef = new BodyDef();
        bdef.position.set(position.x / PPM, position.y / PPM);
        bdef.type = type;
        final Body body = world.createBody(bdef);

        //define fixture
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / PPM, size.y / PPM);
        final FixtureDef fdef = new FixtureDef();
        fdef.shape= shape;
        fdef.density=density;
        fdef.isSensor = sensor;
        body.createFixture(fdef);

        shape.dispose();
        return body;

    }


}
