package ui;

import agents.ExplorerAgent;
import commons.Constants;
import environment.Vec2;

import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Color;

public class ExplorerStyle extends JComponent {

    private final ExplorerAgent explorerAgent;

    public ExplorerStyle(ExplorerAgent explorerAgent) {
        this.explorerAgent = explorerAgent;
    }

    public void paint(Graphics g) {
        Vec2 pos = this.explorerAgent.getPosition();
        g.setColor(Color.BLUE);
        g.fillOval((int) pos.getX(), Constants.worldHeight - (int) pos.getY(), 10, 10);
        g.setColor(Color.WHITE);
        g.drawOval((int) pos.getX(), Constants.worldHeight - (int) pos.getY(), 10, 10);
    }

}
