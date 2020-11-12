package agents;

import java.awt.Color;
import java.awt.Graphics;

import commons.Constants;
import environment.Vec2;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import ui.SwingStyle;

public class BaseAgent extends Agent implements SwingStyle {
    static final long serialVersionUID = 134L;
    private Vec2 position;

    public BaseAgent(Vec2 pos) {
        this.position = pos;
    }

    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("Base");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    @Override
    public void draw(Graphics g, Vec2 scale) {
        int x = (int) (position.getX() * scale.getX());
        int y = Constants.worldHeight - (int) (position.getY() * scale.getY());

        g.setColor(Color.GREEN);
        g.fillRect(x, y, 15, 15);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, 15, 15);
    }

    public Vec2 getPosition() {
        return this.position;
    }
}
