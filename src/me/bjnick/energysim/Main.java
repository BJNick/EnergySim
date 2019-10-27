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

        PhysicalBody pb = new PhysicalBody(new Vector2(-2, -5), 80, new Vector2(2, 4), Color.MAGENTA);
        pb.velocity = new Vector2(0, 20);
        physicsEngine.add(pb);

        pb = new PhysicalBody(new Vector2(2, 0), 40, new Vector2(2, 2), Color.PINK);
        pb.velocity = new Vector2(-10, 0);
        physicsEngine.add(pb);

        pb = new PhysicalBody(new Vector2(-4, 1), 100, new Vector2(1, 1), Color.GREEN);
        pb.velocity = new Vector2(10, 10);
        physicsEngine.add(pb);

        pb = new PhysicalBody(new Vector2(4, 1), 100, new Vector2(1, 1), Color.BLUE);
        pb.velocity = new Vector2(-20, 12f);
        physicsEngine.add(pb);

        physicsEngine.add(new StaticBody(new Vector2(0, -7.5f), new Vector2(24, 1), Color.black));
        physicsEngine.add(new StaticBody(new Vector2(0, 7.5f), new Vector2(24, 1), Color.black));
        physicsEngine.add(new StaticBody(new Vector2(-11.5f, 0f), new Vector2(1, 16), Color.black));
        physicsEngine.add(new StaticBody(new Vector2(11.5f, 0f), new Vector2(1, 16), Color.black));

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
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        // To stop
        //timer.running = false;

    }

}
