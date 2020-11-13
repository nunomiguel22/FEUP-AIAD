package agents;

import commons.Constants;
import environment.Map;
import environment.Vec2;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import ui.SwingStyle;

import java.awt.*;

public class CollectorAgent extends Agent implements SwingStyle {
    private final Map map;
    private final Vec2 position;
    private final Vec2 bounds;
    private final Vec2 direction;
    private final int carrying;

    public CollectorAgent(Vec2 startPos, Map map) {
        this.map = map;
        this.position = startPos;
        this.bounds = map.getBounds();
        this.direction = Vec2.getRandomDirection();
//        this.state = States.RANDOM;
        this.carrying = 0;
    }

    private Vec2 getPosition() {
        return this.position;
    }

    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("collector");
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
        Vec2 pos = this.getPosition();
        int x = (int) (pos.getX() * scale.getX());
        int y = Constants.worldHeight - (int) (pos.getY() * scale.getY());

        g.setColor(Color.YELLOW);
        g.fillOval(x, y, 10, 10);
        g.setColor(Color.WHITE);
        g.drawOval(x, y, 10, 10);
    }
}
