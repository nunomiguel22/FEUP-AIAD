package agents.behaviours;

import agents.AbstractAgent;
import environment.Vec2;
import jade.core.behaviours.TickerBehaviour;

public class DrivingBehaviour extends TickerBehaviour {
    private static final long serialVersionUID = 1L;

    private final AbstractAgent agent;

    public DrivingBehaviour(AbstractAgent a, long period) {
        super(a, period);
        agent = a;
    }

    @Override
    protected void onTick() {

        // Check bounds
        Vec2 dir = new Vec2(1, 1);
        boolean outOfBounds = checkBounds(dir);
        if (outOfBounds) {
            agent.setDirection(Vec2.getRandomDirection());
            if (agent.getDirection().getX() * dir.getX() < 0)
                agent.getDirection().setX(agent.getDirection().getX() * -1);

            if (agent.getDirection().getY() * dir.getY() < 0)
                agent.getDirection().setY(agent.getDirection().getY() * -1);
        }

        agent.getPosition().addVec2(agent.getDirection());
    }

    private boolean checkBounds(Vec2 dir) {
        boolean outOfBounds = false;

        if (agent.getPosition().getX() >= agent.getBounds().getX()) {
            outOfBounds = true;
            dir.setX(-1);
        }
        if (agent.getPosition().getX() <= 0)
            outOfBounds = true;
        if (agent.getPosition().getY() >= agent.getBounds().getY()) {
            outOfBounds = true;
            dir.setY(-1);
        }
        if (agent.getPosition().getY() <= 0)
            outOfBounds = true;

        return outOfBounds;
    }
}
