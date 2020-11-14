package environment;

import ui.SwingStyle;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Polygon;
import java.io.Serializable;

import javax.swing.SwingUtilities;

import commons.Constants;

public class Resource implements SwingStyle, Serializable {
    static final long serialVersionUID = 1423L;
    private final int size = 5;
    private final Vec2 position;
    private int amount;
    private Color clr;

    public Resource(Vec2 pos, int amount) {
        this.position = pos;
        this.amount = amount;
        this.clr = Color.DARK_GRAY;
    }

    @Override
    public void draw(Graphics g, Vec2 scale) {

        int x = (int) (position.getX() * scale.getX());
        int y = Constants.worldHeight - (int) (position.getY() * scale.getY());

        Polygon triangle = new Polygon(new int[] { x - size, x, x + size }, new int[] { y + size, y - size, y + size },
                3);

        g.setColor(this.clr);
        g.fillPolygon(triangle);
        g.setColor(Color.WHITE);
        g.drawPolygon(triangle);
    }

    public int getAmount() {
        return this.amount;
    }

    public Vec2 getPosition() {
        return position;
    }

    public void setColor(Color nclr) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                clr = nclr;
            }
        });

    }
}
