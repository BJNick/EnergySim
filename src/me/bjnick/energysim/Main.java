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

        PhysicalBody pb = new PhysicalBody(new Vector2(-2, 7));
        PhysicalBody pb2 = new PhysicalBody(new Vector2(2, 3), 8, new Vector2(2, 2), Color.MAGENTA);

        physicsEngine.add(pb);
        physicsEngine.add(pb2);

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
