package agents;

import environment.Vec2;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import ui.SwingStyle;
import jade.domain.FIPAException;
import java.awt.Color;
import java.awt.Graphics;
import commons.Constants;

public class TransporterAgent extends Agent implements SwingStyle {
    static final long serialVersionUID = 1L;

    private Vec2 position;
    private Vec2 direction;
    private Vec2 bounds;

    public TransporterAgent(Vec2 startPos, Vec2 bounds) {
        this.position = startPos;
        this.bounds = bounds;
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

        addBehaviour(new DrivingBehaviour(this, 33));
    }

    private boolean checkBounds(Vec2 dir) {
        boolean outOfBounds = false;

        if (position.getX() >= bounds.getX()) {
            outOfBounds = true;
            dir.setX(-1);
        }
        if (position.getX() <= 0)
            outOfBounds = true;
        if (position.getY() >= bounds.getY()) {
            outOfBounds = true;
            dir.setY(-1);
        }
        if (position.getY() <= 0)
            outOfBounds = true;

        return outOfBounds;
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    private class DrivingBehaviour extends TickerBehaviour {
        private static final long serialVersionUID = 1L;

        public DrivingBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {

            // Check bounds
            Vec2 dir = new Vec2(1, 1);
            boolean outOfBounds = checkBounds(dir);
            if (outOfBounds) {
                direction = Vec2.getRandomDirection();
                if (direction.getX() * dir.getX() < 0)
                    direction.setX(direction.getX() * -1);

                if (direction.getY() * dir.getY() < 0)
                    direction.setY(direction.getY() * -1);
            }

            position.addVec2(direction);

        }
    }

    @Override
    public void draw(Graphics g, Vec2 scale) {
        Vec2 pos = this.getPosition();
        g.setColor(Color.RED);
        int x = (int) (pos.getX() * scale.getX());
        int y = Constants.worldHeight - (int) (pos.getY() * scale.getY());

        g.fillOval(x, y, 10, 10);
        g.setColor(Color.WHITE);
        g.drawOval(x, y, 10, 10);
    }

    public Vec2 getPosition() {
        return this.position;
    }

}
