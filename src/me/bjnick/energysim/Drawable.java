package me.bjnick.energysim;

import java.awt.*;

public interface Drawable {

    void draw(DrawPanel dp, Graphics g);

    int getLayer();

}
