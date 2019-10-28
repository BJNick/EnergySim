package me.bjnick.energysim;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector;
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
    Queue<Vector2> lastForces;

    public PhysicalBody(Vector2 position) {
        this.position = position;
        shape = new BodyShape.Box();
        shape.color = Color.CYAN;
        shape.bounds = new Rectangle(0, 0, 1, 1);
        forces = new LinkedList<>();
        mass = 1f;
    }

    public PhysicalBody(Vector2 position, float mass, Vector2 size, Color color) {
        this.position = position.cpy();//.add(size.cpy().scl(0.5f));
        this.mass = mass;
        shape = new BodyShape.Box();
        shape.color = color;
        shape.bounds = new Rectangle(0, 0, size.x, size.y);
        recalculateShape();
        forces = new LinkedList<>();
    }

    public float move(float deltaTime) {

        Vector2 initialPos = position.cpy();

        var netForce = getNetForce();
        lastForces = forces;
        forces = new LinkedList<>();

        Vector2 acceleration = netForce.cpy().scl(1f / mass);

        position.add(velocity.cpy().scl(deltaTime)); // x = x0 + v(dt)

        position.add(acceleration.cpy().scl(0.5f * deltaTime * deltaTime)); // x = x0 + 1/2(a)(dt^2)

        velocity = velocity.add(acceleration.cpy().scl(deltaTime)); // v = v0 + a(dt)

        return initialPos.sub(position).len();
    }

    public Vector2 getNetForce() {
        Vector2 netForce = Vector2.Zero.cpy();
        for (Vector2 F : forces) {
            netForce.add(F);
        }
        return netForce;
    }

    public Vector2 getNormalForceDir(PhysicalBody b2) { // Force acting on b2
        this.recalculateShape();
        b2.recalculateShape();
        var topY1 = shape.bounds.y + shape.bounds.height;
        var bottomY1 = shape.bounds.y;
        var leftX1 = shape.bounds.x;
        var rightX1 = shape.bounds.x + shape.bounds.width;
        var topY2 = b2.shape.bounds.y + b2.shape.bounds.height;
        var bottomY2 = b2.shape.bounds.y;
        var leftX2 = b2.shape.bounds.x;
        var rightX2 = b2.shape.bounds.x + b2.shape.bounds.width;

        if (bottomY2 > this.position.y && !(leftX2 > rightX1 || rightX2 < leftX1)) {
            return new Vector2(0, 1);
        } else if (topY2 < this.position.y && !(leftX2 > rightX1 || rightX2 < leftX1)) {
            return new Vector2(0, -1);
        } else if (rightX2 < this.position.x && !(topY2 < bottomY1 || bottomY2 > topY1)) {
            return new Vector2(-1, 0);
        } else if (leftX2 > this.position.x && !(topY2 < bottomY1 || bottomY2 > topY1)) {
            return new Vector2(1, 0);
        }
        return new Vector2(0, 0);
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

    public boolean detectCollision(PhysicalBody b2) {
        recalculateShape();
        b2.recalculateShape();
        return shape.bounds.overlaps(b2.shape.bounds);
    }

    public boolean estimateCollision(PhysicalBody b2, float deltaTime) {
        var a = predictShapePos(deltaTime);
        var b = b2.predictShapePos(deltaTime);
        return a.overlaps(b);
    }

    protected void recalculateShape() {
        shape.bounds = new Rectangle(position.x - shape.bounds.width / 2, position.y - shape.bounds.height / 2, shape.bounds.width, shape.bounds.height);
    }

    protected Rectangle predictShapePos(float deltaTime) {
        var newPos = position.cpy().add(velocity.cpy().scl(deltaTime));
        if (Float.isFinite(mass)) {
            Vector2 acceleration = getNetForce().scl(1 / mass);
            newPos.add(acceleration.cpy().scl(0.5f * deltaTime * deltaTime));
        }
        return new Rectangle(newPos.x - shape.bounds.width / 2, newPos.y - shape.bounds.height / 2, shape.bounds.width, shape.bounds.height);
    }

    public Vector2[] resolveCollision(PhysicalBody b2, float energyLoss) {
        var newVels = solveMomentumKEEquation(this.mass, this.velocity, b2.mass, b2.velocity);
        newVels[0].scl((float) Math.sqrt(energyLoss));
        newVels[1].scl((float) Math.sqrt(energyLoss));

        float totalKEnergy = calculateKineticEnergy() + b2.calculateKineticEnergy();
        float newEnergy = (Float.isFinite(this.mass) ? newVels[0].len2() * this.mass / 2 : 0) + (Float.isFinite(b2.mass) ? newVels[1].len2() * b2.mass / 2 : 0);

        //this.velocity = newVels[0];
        //b2.velocity = newVels[1];

        return new Vector2[]{newVels[0], newVels[1], new Vector2(totalKEnergy - newEnergy, 0)};
    }

    private Vector2[] getNormalVelocity(Vector2 vel, PhysicalBody b) {
        var N = b.getNormalForceDir(this);
        var Vy = N.cpy().scl((float) Math.sin(Math.toRadians(90 + vel.angle() - N.angle())) * vel.len());
        var Vx = N.cpy().rotate(-90).scl((float) Math.cos(Math.toRadians(90 + vel.angle() - N.angle())) * this.velocity.len());
        return new Vector2[]{Vy, Vx};
    }

    private Vector2[] solveMomentumKEEquation(float mA, Vector2 vA, float mB, Vector2 vB) {
        if (Float.isInfinite(mA))
            return new Vector2[]{
                    vA.cpy().scl(1).add(vB.cpy().scl(0)),
                    vB.cpy().scl(-1).add(vA.cpy().scl(1))
            };
        else if (Float.isInfinite(mB))
            return new Vector2[]{
                    vA.cpy().scl(-1).add(vB.cpy().scl(1)),
                    vB.cpy().scl(1).add(vA.cpy().scl(0))
            };

        return new Vector2[]{
                vA.cpy().scl((mA - mB) / (mA + mB)).add(vB.cpy().scl((2 * mB) / (mA + mB))),
                vB.cpy().scl((mB - mA) / (mA + mB)).add(vA.cpy().scl((2 * mA) / (mA + mB)))
        };
    }


    @Override
    public void draw(DrawPanel dp, Graphics g) {
        recalculateShape();
        shape.draw(dp, g);
        g.setColor(Color.WHITE);
        dp.drawText(g, "KE = " + Math.round(calculateKineticEnergy()), position.cpy().add(shape.bounds.width / 2 + 0.03f, shape.bounds.height / 2));
        dp.drawText(g, "PE = " + Math.round(calculateGPotentialEnergy()), position.cpy().add(shape.bounds.width / 2 + 0.03f, shape.bounds.height / 2 - 0.03f));
        /*var centre = dp.transformPosition(position.cpy());
        if (lastForces != null)
            for (Vector2 force : lastForces) {
                force = force.cpy().scl(0.01f);
                g.drawLine(dp.intX(g, centre.cpy()), dp.intY(g, centre.cpy()), dp.intX(g, force.cpy()), dp.intY(g, force.cpy()));
            }*/
    }

    @Override
    public int getLayer() {
        return shape.getLayer();
    }
}
