package agents;

import java.awt.Graphics;
import java.awt.Color;

import agents.behaviours.DrivingBehaviour;
import agents.behaviours.ExploreBehaviour;
import commons.Constants;
import environment.Map;
import environment.Vec2;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import ui.SwingStyle;
import jade.domain.FIPAException;

public class ExplorerAgent extends Agent implements SwingStyle {

    private Map map;
    private Vec2 position;
    private Vec2 direction;
    private Vec2 bounds;

    public ExplorerAgent(Vec2 startPosition, Map map) {
        this.map = map;
        this.position = startPosition;
        this.direction = Vec2.getRandomDirection();
        this.bounds = map.getBounds();
    }

    public Vec2 getPosition() {
        return position;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }

    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("Explorer");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new DrivingBehaviour(this, Constants.explorerTickPeriod, position, direction, bounds));
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    @Override
    public void draw(Graphics g, Vec2 scale) {
        Vec2 pos = this.getPosition();
        int x = (int) (pos.getX() * scale.getX());
        int y = Constants.worldHeight - (int) (pos.getY() * scale.getY());

        g.setColor(Color.BLUE);
        g.fillOval(x, y, 10, 10);
        g.setColor(Color.WHITE);
        g.drawOval(x, y, 10, 10);
    }
}
