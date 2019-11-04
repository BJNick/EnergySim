package me.bjnick.energysim;

import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {

    public static void main(String[] args) {

        ViewFrame frame = new ViewFrame();
        frame.setVisible(true);

        var physicsEngine = new PhysicsEngine();

        PhysicalBody pb = new PhysicalBody(new Vector2(-0.2f, 0f), 20, new Vector2(0.2f, 0.5f), Color.MAGENTA);
        pb.velocity = new Vector2(0, 10);
        physicsEngine.add(pb);

        pb = new PhysicalBody(new Vector2(0.2f, -0.2f), 7, new Vector2(.25f, .25f), Color.PINK);
        pb.velocity = new Vector2(-1, 0);
        physicsEngine.add(pb);

        pb = new PhysicalBody(new Vector2(-0.1f, .3f), 1, new Vector2(.1f, .1f), Color.GREEN);
        pb.velocity = new Vector2(1, 0);
        physicsEngine.add(pb);

        pb = new PhysicalBody(new Vector2(0.1f, .1f), 1, new Vector2(.1f, .1f), Color.BLUE);
        pb.velocity = new Vector2(-2, 1.2f);
        physicsEngine.add(pb);

        physicsEngine.add(new StaticBody(new Vector2(0, -1f), new Vector2(4f, 1f), Color.black));
        physicsEngine.add(new StaticBody(new Vector2(0, 1f), new Vector2(4f, 1f), Color.black));
        physicsEngine.add(new StaticBody(new Vector2(-1.5f, 0f), new Vector2(1f, 2f), Color.black));
        physicsEngine.add(new StaticBody(new Vector2(1.5f, 0f), new Vector2(1f, 2f), Color.black));

        frame.drawPanel.addListeners(physicsEngine.bodies);

        var timer = new FrameRateTimer() {
            public void update(float delta) {
                physicsEngine.simulateFor(delta);
            }

            public void render() {
                frame.drawPanel.repaint();
            }
        };

        // FPS display
        frame.drawPanel.addListener(new Drawable() {
            @Override
            public void draw(DrawPanel dp, Graphics g) {
                g.setColor(Color.white);
                g.drawString("FPS: " + (int) Math.ceil(timer.instantFPS), 5, 20);
                g.drawString("UPS: " + (int) Math.ceil(timer.instantUPS), 5, 40);
            }
            @Override
            public int getLayer() {
                return -100;
            }
        });

        frame.drawPanel.addListener(physicsEngine);

        timer.updating = false;
        timer.start();

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == ' ') {
                    timer.updating = !timer.updating;
                }
                if (e.getKeyChar() == '2') {
                    physicsEngine.simulationRate *= 2;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        // To stop
        //timer.running = false;

    }

}
