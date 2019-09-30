package me.bjnick.energysim;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import javax.swing.*;
import java.awt.*;

public class DrawPanel extends JPanel {

    public Rectangle viewport;
    public final float aspectRatio = 3/2f;

    public DrawPanel() {
        super();
        viewport = new Rectangle(0, 0, 16 * aspectRatio, 16);
        viewport.setCenter(0, 0);
    }

    @Override
    protected void paintComponent(Graphics g) {

        int height = g.getClipBounds().height;
        int width = Math.round(aspectRatio * height);

        g.setColor(new Color(0x153657));
        g.fillRect(0, 0, width, height);

        g.setColor(new Color(0x53A969));
        drawRectangle(g, new Rectangle(-12, -8, 2, 2), true);
        g.setColor(new Color(0xA94C3F));
        drawRectangle(g, new Rectangle(-12, -6, 2, 2), true);
        g.setColor(new Color(0xA97D21));
        drawRectangle(g, new Rectangle(8, 6, 2, 2), true);

    }

    Vector2 transformPosition(Vector2 position) {
        var localPos = position.sub(viewport.getPosition(new Vector2()));
        localPos = localPos.scl(1/viewport.width, 1/viewport.height);
        return localPos;
    }

    Rectangle transformRectangle(Rectangle rectangle) {
        Rectangle ret = new Rectangle(rectangle);
        ret.setSize(ret.width / viewport.width, ret.height / viewport.height);
        ret.setPosition(transformPosition(ret.getPosition(new Vector2())));
        return ret;
    }

    int intX(Graphics g, Vector2 v) {
        return (int) Math.floor(v.x * g.getClipBounds().height * aspectRatio);
    }

    int intY(Graphics g, Vector2 v) {
        return (int) Math.floor(v.y * g.getClipBounds().height);
    }

    void drawRectangle(Graphics g, Rectangle rectangle, boolean fill) {

        Rectangle localRect = transformRectangle(rectangle);

        Vector2 localPos = localRect.getPosition(new Vector2());
        Vector2 localSize = localRect.getSize(new Vector2());

        if (fill)
            g.fillRect(intX(g, localPos), intY(g, localPos), intX(g, localSize), intY(g, localSize));
        else
            g.drawRect(intX(g, localPos), intY(g, localPos), intX(g, localSize), intY(g, localSize));
    }


}
