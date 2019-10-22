package me.bjnick.energysim;

import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.Stack;

public class PhysicsEngine implements Drawable {

    Stack<PhysicalBody> bodies;

    float simulationRate = 1f;

    Vector2 gField = new Vector2(0, -9.81f);
    float airDensity = 1.225f; // kg / m^3

    float roomVolume = 24 * 16 * 3;
    float airHeatCapacity = 0.718f;

    Vector2 wind = Vector2.Zero.cpy();

    float airEnergy = 0;
    float temperature = 20 + 273.15f;

    float potentialEnergyZero = -9;

    public PhysicsEngine() {
        bodies = new Stack<>();
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
        g.drawString("E-heat: " + Math.round(airEnergy), dp.width - 100, 20);
        g.drawString("T: " + Math.round(temperature * 10 - 2731.5f) / 10f + "C", dp.width - 100, 40);
        g.drawString("S: " + Math.round((airEnergy / temperature) * 10) / 10f, dp.width - 100, 60);
    }

    @Override
    public int getLayer() {
        return -1000;
    }
}
