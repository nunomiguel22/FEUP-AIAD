package ui;

import javax.swing.JComponent;

import agents.TransporterAgent;
import environment.Vec2;

import java.awt.Color;
import java.awt.Graphics;

public class TransporterStyle extends JComponent {

    private final int height = 600;
    private TransporterAgent tp;

    public TransporterStyle(TransporterAgent a) {
        this.tp = a;
    }

    public void paint(Graphics g) {
        Vec2 pos = this.tp.getPosition();
        g.setColor(Color.RED);
        g.fillOval((int) pos.getX(), height - (int) pos.getY(), 10, 10);
        g.setColor(Color.WHITE);
        g.drawOval((int) pos.getX(), height - (int) pos.getY(), 10, 10);
    }
}
