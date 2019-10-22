package me.bjnick.energysim;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BodyShape implements Drawable {

    public Rectangle bounds;
    public Color color;
    public float dragC;
    public Function<Vector2, Float> area = (var dir) -> 0f;
    public Supplier<Float> volume = () -> bounds.height * bounds.width * bounds.width;

    public static class Box extends BodyShape {

        public Box() {
            dragC = 0.8f;
            area = (var direction) -> Math.abs(direction.cpy().nor().x) * bounds.height * bounds.width + Math.abs(direction.cpy().nor().y) * bounds.width * bounds.width;
            volume = () -> bounds.height * bounds.width * bounds.width;
        }

        @Override
        public void draw(DrawPanel dp, Graphics g) {
            g.setColor(color);
            dp.drawRectangle(g, bounds, true);
        }

        @Override
        public int getLayer() {
            return 0;
        }
    }

}
