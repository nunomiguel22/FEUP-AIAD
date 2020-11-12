package agents.behaviours;

import agents.AbstractAgent;
import commons.Constants;
import environment.Vec2;
import jade.core.behaviours.TickerBehaviour;

public class ExploreBehaviour extends TickerBehaviour {

    private final AbstractAgent agent;
    private boolean movingEast = true;

    public ExploreBehaviour(AbstractAgent a, long period) {
        super(a, period);
        agent = a;
    }

    @Override
    protected void onTick() {

        if (movingEast) {
            moveExplorerEast();
        } else {
            moveExplorerWest();
        }
    }

    private void moveExplorerEast() {
        Vec2 actualPosition = this.agent.getPosition();
        if (actualPosition.getX() < Constants.worldWidth ) {
            agent.setPosition(Vec2.of(actualPosition.getX() + 10.0, actualPosition.getY()));
        } else if (actualPosition.getY() < Constants.worldHeight) {
            agent.setPosition(Vec2.of(actualPosition.getX() - 10.0, actualPosition.getY() + 10.0));
            movingEast = false;
        } else {
            myAgent.doDelete();
        }
    }

    private void moveExplorerWest() {
        Vec2 actualPosition = this.agent.getPosition();
        if (actualPosition.getX() > 0.0 ) {
            agent.setPosition(Vec2.of(actualPosition.getX() - 10.0, actualPosition.getY()));
        } else if (actualPosition.getY() < Constants.worldHeight) {
            agent.setPosition(Vec2.of(actualPosition.getX() + 10.0, actualPosition.getY() + 10.0));
            movingEast = true;
        } else {
            myAgent.doDelete();
        }
    }
}
