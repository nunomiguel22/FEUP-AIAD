package agents.behaviours;

import agents.ExplorerAgent;
import commons.Constants;
import environment.Vec2;
import jade.core.behaviours.TickerBehaviour;

public class ExploreBehaviour extends TickerBehaviour {

    private final ExplorerAgent explorer;
    private boolean movingEast = true;

    public ExploreBehaviour(ExplorerAgent a, long period) {
        super(a, period);
        explorer = a;
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
        Vec2 actualPosition = this.explorer.getPosition();
        if (actualPosition.getX() < Constants.worldWidth ) {
            explorer.setPosition(Vec2.of(actualPosition.getX() + 10.0, actualPosition.getY()));
        } else if (actualPosition.getY() < Constants.worldHeight) {
            explorer.setPosition(Vec2.of(actualPosition.getX() - 10.0, actualPosition.getY() + 10.0));
            movingEast = false;
        } else {
            myAgent.doDelete();
        }
    }

    private void moveExplorerWest() {
        Vec2 actualPosition = this.explorer.getPosition();
        if (actualPosition.getX() > 0.0 ) {
            explorer.setPosition(Vec2.of(actualPosition.getX() - 10.0, actualPosition.getY()));
        } else if (actualPosition.getY() < Constants.worldHeight) {
            explorer.setPosition(Vec2.of(actualPosition.getX() + 10.0, actualPosition.getY() + 10.0));
            movingEast = true;
        } else {
            myAgent.doDelete();
        }
    }
}
