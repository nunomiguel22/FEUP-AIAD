package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.awt.Color;
import java.awt.Graphics;

import agents.behaviours.DrivingBehaviour;
import commons.Constants;
import environment.Map;
import environment.Vec2;
import ui.SwingStyle;

public class TransporterAgent extends Agent implements SwingStyle {
    static final long serialVersionUID = 1L;

    private Map map;
    private Vec2 position;
    private Vec2 direction;
    private Vec2 bounds;

    public TransporterAgent(Vec2 startPos, Map map) {
        this.map = map;
        this.position = startPos;
        this.bounds = map.getBounds();
        this.direction = Vec2.getRandomDirection();
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

        addBehaviour(new DrivingBehaviour(this, 33, position, direction, bounds));
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    @Override
    public void draw(Graphics g, Vec2 scale) {
        int x = (int) (position.getX() * scale.getX());
        int y = Constants.worldHeight - (int) (position.getY() * scale.getY());

        g.setColor(Color.RED);
        g.fillOval(x, y, 10, 10);
        g.setColor(Color.WHITE);
        g.drawOval(x, y, 10, 10);
    }

    public Vec2 getPosition() {
        return this.position;
    }
}
