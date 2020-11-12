package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import commons.Constants;
import java.util.ArrayList;
import environment.Map;
import environment.Vec2;

public class SwingGUI extends Agent {
    static final long serialVersionUID = 123L;
    private JFrame frame;
    private Panel panel;
    private Map map;
    private Vec2 scale;

    public SwingGUI(Map map) {
        this.map = map;
        this.scale = Vec2.divVec2(new Vec2(Constants.worldWidth, Constants.worldHeight), map.getBounds());
        panel = new Panel(this.scale);
        frame = new JFrame("Planet Explorer");
        panel.setMinimumSize(new Dimension(Constants.worldWidth, Constants.worldHeight));
        panel.setBackground(new Color(210, 180, 140));
        frame.setMinimumSize(new Dimension(Constants.worldWidth + 100, Constants.worldHeight + 100));
        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void addStyle(SwingStyle style) {
        panel.addStyle(style);
    }

    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("GUI");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new RenderBehaviour(this, 33)); // 30 FPS, frame every 33ms
        frame.setVisible(true);
    }

    private class Panel extends JPanel {
        static final long serialVersionUID = 12L;
        private ArrayList<SwingStyle> styles;
        private Vec2 scale;
        private int yBorder;
        private int xBorder;

        public Panel(Vec2 scale) {
            this.scale = scale;
            styles = new ArrayList<SwingStyle>();
            Vec2 border = Vec2.mulVec2(map.getBounds(), scale);
            xBorder = (int) border.getX() + 10;
            yBorder = (int) border.getY() + 10;
        }

        public void addStyle(SwingStyle style) {
            styles.add(style);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawLine(0, 0, xBorder, 0);
            g.drawLine(0, 0, 0, yBorder);
            g.drawLine(0, yBorder, xBorder, yBorder);
            g.drawLine(xBorder, 0, xBorder, yBorder);

            for (SwingStyle style : styles)
                style.draw(g, this.scale);
        }
    }

    private class RenderBehaviour extends TickerBehaviour {
        private static final long serialVersionUID = 1L;

        public RenderBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            SwingUtilities.updateComponentTreeUI(frame);
        }
    }
}
