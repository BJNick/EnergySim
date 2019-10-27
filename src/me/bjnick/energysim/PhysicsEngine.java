package me.bjnick.energysim;

import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.ArrayList;

public class PhysicsEngine implements Drawable {

    ArrayList<PhysicalBody> bodies;

    float simulationRate = 1f;

    Vector2 gField = new Vector2(0, -9.81f);
    float airDensity = 1.225f; // kg / m^3

    float roomVolume = 24 * 16 * 3;
    float airHeatCapacity = 0.718f;

    Vector2 wind = Vector2.Zero.cpy();

    float airEnergy = 0;
    float temperature = 20 + 273.15f;

    float collisionEnergyLoss = 0.7f;

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

            var buoyancy = gField.cpy().scl(-airDensity * body.shape.volume.get());
            body.forces.add(buoyancy);

            //var dist = body.move(delta);
            // Add heat energy, work done by the bodies on air
            //addHeatEnergy(drag.len() * dist);

        }
        for (int i = 0; i < bodies.size(); i++) {
            for (int j = i + 1; j < bodies.size(); j++) {
                var a = bodies.get(i);
                var b = bodies.get(j);
                if (bodies.get(i).estimateCollision(bodies.get(j), delta)) {
                    //var excess_energy = bodies.get(i).resolveCollision(bodies.get(j), collisionEnergyLoss);
                    //addHeatEnergy(excess_energy);
                    //var a = bodies.get(i);
                    //var b = bodies.get(j);
                    /*if (!a.estimateCollision(b, delta*2)) {
                        a.velocity.scl(1.1f);
                        b.velocity.scl(1.1f);
                        System.out.println("Accounting...");
                    }*/
                    var result = a.resolveCollision(b, collisionEnergyLoss);
                    a.forces.add(result[0].cpy().sub(a.velocity).scl(a.mass / delta).sub(a.getNetForce()));
                    b.forces.add(result[1].cpy().sub(b.velocity).scl(b.mass / delta).sub(b.getNetForce()));
                    addHeatEnergy(result[2].x);
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
