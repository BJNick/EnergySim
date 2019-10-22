package me.bjnick.energysim;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class PhysicalBody implements Drawable {

    BodyShape shape;
    float mass;

    Vector2 position;
    Vector2 velocity = Vector2.Zero.cpy();

    PhysicsEngine engine;

    Queue<Vector2> forces;

    public PhysicalBody(Vector2 position) {
        this.position = position;
        shape = new BodyShape.Box();
        shape.color = Color.CYAN;
        shape.bounds = new Rectangle(0, 0, 1, 1);
        forces = new LinkedList<>();
        mass = 1f;
    }

    public PhysicalBody(Vector2 position, float mass, Vector2 size, Color color) {
        this.position = position;
        this.mass = mass;
        shape = new BodyShape.Box();
        shape.color = color;
        shape.bounds = new Rectangle(0, 0, size.x, size.y);
        forces = new LinkedList<>();
    }

    public float move(float deltaTime) {

        Vector2 initialPos = position.cpy();

        Vector2 netForce = Vector2.Zero.cpy();
        for (Vector2 F : forces) {
            netForce.add(F);
        }
        forces.clear();

        Vector2 acceleration = netForce.cpy().scl(1f / mass);

        position.add(velocity.cpy().scl(deltaTime)); // x = x0 + v(dt)

        position.add(acceleration.cpy().scl(0.5f * deltaTime * deltaTime)); // x = x0 + 1/2(a)(dt^2)

        velocity = velocity.add(acceleration.cpy().scl(deltaTime)); // v = v0 + a(dt)

        // Bounce
        if (position.y < -8) {
            velocity = new Vector2(0, Math.abs(velocity.y));
        }

        return initialPos.sub(position).len();
    }

    public Vector2 getDragForce(Vector2 velocity) {
        Vector2 relVel = velocity.cpy().sub(engine.wind);
        return relVel.cpy().nor().scl(-0.5f * engine.airDensity * shape.area.apply(relVel.cpy()) * shape.dragC * relVel.len2());
    }

    public float calculateKineticEnergy() {
        return 0.5f * mass * velocity.len2();
    }

    public float calculateGPotentialEnergy() {
        return mass * (position.y - engine.potentialEnergyZero) * engine.gField.len();
    }


    @Override
    public void draw(DrawPanel dp, Graphics g) {
        shape.bounds = new Rectangle(position.x - shape.bounds.width / 2, position.y + shape.bounds.height / 2, shape.bounds.width, shape.bounds.height);
        shape.draw(dp, g);
        g.setColor(Color.WHITE);
        dp.drawText(g, "KE = " + Math.round(calculateKineticEnergy()), position.cpy().add(1, 0));
        dp.drawText(g, "PE = " + Math.round(calculateGPotentialEnergy()), position.cpy().add(1, -0.7f));
    }

    @Override
    public int getLayer() {
        return shape.getLayer();
    }
}
