package me.bjnick.energysim;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.PriorityQueue;

public class DrawPanel extends JPanel {

    Rectangle viewport;
    final float aspectRatio = 3 / 2f; // 24 x 16 metres

    private PriorityQueue<Drawable> drawListeners;

    private Dictionary<String, BufferedImage> bufferedImages;

    int width, height;

    public DrawPanel() {
        super();
        viewport = new Rectangle(0, 0, 16 * aspectRatio, 16);
        viewport.setCenter(0, 0);
        drawListeners = new PriorityQueue<>((d1, d2) -> d2.getLayer()-d1.getLayer());
        bufferedImages = new Hashtable<>();
    }

    @Override
    protected void paintComponent(Graphics g) {

        height = g.getClipBounds().height;
        width = Math.round(aspectRatio * height);
        g.setColor(new Color(0x0F2840));
        g.fillRect(0, 0, width, height);

        for (Drawable d : drawListeners) {
            d.draw(this, g);
        }

    }

    void addListener(Drawable drawable) {
        drawListeners.add(drawable);
    }

    void addListeners(Iterable<PhysicalBody> drawables) {
        for (Drawable d : drawables) {
            drawListeners.add(d);
        }
    }

    void removeListener(Drawable drawable) {
        drawListeners.remove(drawable);
    }

    Vector2 transformPosition(Vector2 position) {
        position = position.scl(1, -1);
        var localPos = position.sub(viewport.getPosition(new Vector2()));
        localPos = localPos.scl(1/viewport.width, 1/viewport.height);
        return localPos;
    }

    Rectangle transformRectangle(Rectangle rectangle) {
        Rectangle ret = new Rectangle(rectangle);
        ret.setSize(ret.width / viewport.width, ret.height / viewport.height);
        if (ret.width == 0 || ret.height == 0)
            System.out.println("WARNING: Width or height of the rectangle is zero.");
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

    void drawOval(Graphics g, Rectangle rectangle, boolean fill) {

        Rectangle localRect = transformRectangle(rectangle);

        Vector2 localPos = localRect.getPosition(new Vector2());
        Vector2 localSize = localRect.getSize(new Vector2());

        if (fill)
            g.fillOval(intX(g, localPos), intY(g, localPos), intX(g, localSize), intY(g, localSize));
        else
            g.drawOval(intX(g, localPos), intY(g, localPos), intX(g, localSize), intY(g, localSize));
    }

    void drawImage(Graphics g, Rectangle rectangle, Image image) {

        if (image == null) {
            System.out.println("WARNING: Image is null!");
            return;
        }

        Rectangle localRect = transformRectangle(rectangle);

        Vector2 localPos = localRect.getPosition(new Vector2());
        Vector2 localSize = localRect.getSize(new Vector2());

        g.drawImage(image, intX(g, localPos), intY(g, localPos), intX(g, localSize), intY(g, localSize), this);
    }

    void drawImageFile(Graphics g, Rectangle rectangle, String imageSrc) {

        var image = bufferedImages.get(imageSrc);

        if (image != null) {
            drawImage(g, rectangle, image);
            return;
        }

        File file = new File(imageSrc);

        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bufferedImages.put(imageSrc, image);
        drawImage(g, rectangle, image);
    }

    void drawText(Graphics g, String text, Vector2 position) {

        Vector2 localPos = transformPosition(position);
        g.drawString(text, intX(g, localPos), intY(g, localPos));
    }




}
