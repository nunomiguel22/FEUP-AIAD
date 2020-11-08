package ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

//import environment.Vec2;

public class SwingGUI extends Agent {
    private final int width = 600;
    private final int height = 600;

    private JFrame frame;

    public SwingGUI() {
        frame = new JFrame("Mars Explorer");
        frame.setMinimumSize(new Dimension(width, height));
        frame.getContentPane().setBackground(new Color(210, 180, 140));
        frame.setVisible(true);

    }

    public void addComponent(JComponent comp) {
        frame.getContentPane().add(comp);
    }

    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("Transporter");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new RenderBehaviour(this, 33)); // 30 FPS, frame every 33ms
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
