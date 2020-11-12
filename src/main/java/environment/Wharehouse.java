package environment;

import ui.SwingStyle;

import java.awt.Color;
import java.awt.Graphics;

public class Wharehouse implements SwingStyle {

    private final Vec2 position;

    public Wharehouse(double x, double y) {
        this.position = Vec2.of(x, y);
    }

    public Vec2 getPosition() {
        return position;
    }

    @Override
    public void draw(Graphics g, Vec2 scale) {

        int x = ((int) position.getX());
        int y = ((int) position.getY());

        g.setColor(Color.PINK);
        g.fillRect(x, y, 30, 20);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, 30, 20);
    }
}
