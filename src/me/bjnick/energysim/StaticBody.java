package me.bjnick.energysim;

import com.badlogic.gdx.math.Vector2;

import java.awt.*;

public class StaticBody extends PhysicalBody {

    public StaticBody(Vector2 position, Vector2 size, Color color) {
        super(position, Float.POSITIVE_INFINITY, size, color);
    }

    @Override
    public float move(float deltaTime) {
        return 0;
    }

    @Override
    public float calculateKineticEnergy() {
        return 0;
    }

    @Override
    public float calculateGPotentialEnergy() {
        return 0;
    }

    @Override
    public boolean detectCollision(PhysicalBody b2) {
        return !(b2 instanceof StaticBody) && b2.detectCollision(this);
    }

    @Override
    public Vector2 getDragForce(Vector2 velocity) {
        return Vector2.Zero.cpy();
    }

    @Override
    public float resolveCollision(PhysicalBody b2, float energyLoss) {
        if (!(b2 instanceof StaticBody)) {
            return b2.resolveCollision(this, energyLoss);
        } else {
            return 0;
        }
    }
}
