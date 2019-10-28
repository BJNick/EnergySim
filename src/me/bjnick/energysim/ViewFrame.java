package me.bjnick.energysim;

import javax.swing.*;

class ViewFrame extends JFrame {

    DrawPanel drawPanel;

    ViewFrame() {

        super();

        setTitle("Energy Simulation - Mykyta APhysics SDS");
        setSize(1000, 1000);

        drawPanel = new DrawPanel();

        add(drawPanel);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

}
