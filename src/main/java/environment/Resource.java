package environment;

import ui.SwingStyle;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Polygon;

public class Resource implements SwingStyle {

    private final Vec2 position;

    public Resource(double x, double y) {
        this.position = Vec2.of(x, y);
    }

    @Override
    public void draw(Graphics g, Vec2 scale) {

        int x = ((int) position.getX());
        int y = ((int) position.getY());

        Polygon triangle = new Polygon(new int[]{x - 5, x, x + 5}, new int[]{y + 5, y - 5, y + 5}, 3);

        g.setColor(Color.DARK_GRAY);
        g.fillPolygon(triangle);
        g.setColor(Color.WHITE);
        g.drawPolygon(triangle);
    }
}
