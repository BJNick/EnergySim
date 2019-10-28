package me.bjnick.energysim;

import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.ArrayList;

public class PhysicsEngine implements Drawable {

    ArrayList<PhysicalBody> bodies;

    float simulationRate = 1f;

    Vector2 gField = new Vector2(0, -9.81f);
    float airDensity = 1.225f; // kg / m^3

    float roomVolume = 1;
    float airHeatCapacity = 718f; // J / kg

    Vector2 wind = Vector2.Zero.cpy();

    float airEnergy = 0;
    float temperature = 20 + 273.15f;

    float collisionEnergyLoss = 0.5f;
    float defaultCoefFriction = 0.2f;

    float potentialEnergyZero = -9;

    public PhysicsEngine() {
        bodies = new ArrayList<>();
    }

    public void simulateFor(float delta /* in seconds */) {
        delta *= simulationRate;
        moveBodies(delta);
    }

    private void moveBodies(float delta) {
        for (PhysicalBody body : bodies) {
            // Gravity
            body.forces.add(gField.cpy().scl(body.mass));
            // Air resistance
            var drag = body.getDragForce(body.velocity);
            body.forces.add(drag);
            // Buoyancy
            var buoyancy = gField.cpy().scl(-airDensity * body.shape.volume.get());
            body.forces.add(buoyancy);

        }
        for (int i = 0; i < bodies.size(); i++) {
            for (int j = i + 1; j < bodies.size(); j++) {
                var a = bodies.get(i);
                var b = bodies.get(j);
                if (bodies.get(i).estimateCollision(bodies.get(j), delta)) {
                    var result = a.resolveCollision(b, collisionEnergyLoss);
                    // Normal force
                    var f1 = result[0].cpy().sub(a.velocity).scl(a.mass / delta).sub(a.getNetForce());
                    var N1 = b.getNormalForceDir(a);
                    var Fy1 = N1.cpy().scl((float) Math.sin(Math.toRadians(90 + f1.angle() - N1.angle())) * f1.len());
                    a.forces.add(Fy1);
                    var fric1 = N1.cpy().rotate90(-1).scl((float) Math.cos(Math.toRadians(90 + a.velocity.angle() - N1.angle())) * Fy1.len() * -defaultCoefFriction);
                    var vel1 = N1.cpy().rotate90(-1).scl((float) Math.cos(Math.toRadians(90 + a.velocity.angle() - N1.angle())) * a.velocity.len());
                    if (vel1.len() > (fric1.len() / a.mass) * delta)
                        a.forces.add(fric1);
                    else
                        a.forces.add(fric1.nor().scl((vel1.len() * a.mass) / delta));

                    var f2 = result[1].cpy().sub(b.velocity).scl(b.mass / delta).sub(b.getNetForce());
                    var N2 = a.getNormalForceDir(b);
                    var Fy2 = N2.cpy().scl((float) Math.sin(Math.toRadians(90 + f2.angle() - N2.angle())) * f2.len());
                    b.forces.add(Fy2);
                    var fric2 = N2.cpy().rotate90(-1).scl((float) Math.cos(Math.toRadians(90 + b.velocity.angle() - N2.angle())) * Fy2.len() * -defaultCoefFriction);
                    var vel2 = N2.cpy().rotate90(-1).scl((float) Math.cos(Math.toRadians(90 + b.velocity.angle() - N2.angle())) * b.velocity.len());
                    if (vel2.len() > (fric2.len() / b.mass) * delta)
                        b.forces.add(fric2);
                    else
                        b.forces.add(fric2.nor().scl((vel2.len() * b.mass) / delta));

                    //addHeatEnergy(result[2].x); TODO
                }
            }
        }
        for (PhysicalBody body : bodies) {
            var drag = body.getDragForce(body.velocity);
            var dist = body.move(delta);
            // Add heat energy, work done by the bodies on air
            addHeatEnergy(drag.len() * dist);
        }
    }

    private void addHeatEnergy(float work) {
        airEnergy += work;
        temperature += work / ((roomVolume * airDensity) * airHeatCapacity);
    }

    public void add(PhysicalBody body) {
        body.engine = this;
        bodies.add(body);
    }

    @Override
    public void draw(DrawPanel dp, Graphics g) {
        g.setColor(Color.white);
        g.drawString("E-heat: " + Math.round(airEnergy), dp.width - 100, 20);
        g.drawString("T: " + Math.round(temperature * 10 - 2731.5f) / 10f + "C", dp.width - 100, 40);
        g.drawString("S: " + Math.round((airEnergy / temperature) * 10) / 10f, dp.width - 100, 60);
    }

    @Override
    public int getLayer() {
        return -1000;
    }
}
