package me.bjnick.energysim;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;

public class Main {

    public static void main(String[] args) {

        ViewFrame frame = new ViewFrame();
        frame.setVisible(true);

        frame.drawPanel.addListener(new Drawable() {

            @Override
            public void draw(DrawPanel dp, Graphics g) {
                g.setColor(new Color(0xCB1931));
                dp.drawOval(g, new Rectangle(-2,-2,4,4), true);
                g.setColor(Color.WHITE);
                dp.drawText(g, "Oval", new Vector2(-2,-2));
            }

            @Override
            public int getLayer() {
                return 0;
            }
        });

        frame.drawPanel.addListener(new Drawable() {

            @Override
            public void draw(DrawPanel dp, Graphics g) {
                g.setColor(new Color(0x39CB3C));
                dp.drawImageFile(g, new Rectangle(2,1,3,3), "C:\\Users\\BJNick.DESKTOP-QK8PP2K\\Desktop\\Files\\saved001.png");
                g.setColor(Color.WHITE);
                dp.drawText(g, "Square", new Vector2(2,1));
            }

            @Override
            public int getLayer() {
                return -1;
            }
        });

    }

}
