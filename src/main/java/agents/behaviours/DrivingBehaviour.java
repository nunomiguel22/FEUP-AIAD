package agents.behaviours;

import environment.Vec2;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class DrivingBehaviour extends TickerBehaviour {
    private static final long serialVersionUID = 1L;
    private Vec2 position;
    private Vec2 direction;
    private Vec2 bounds;

    public DrivingBehaviour(Agent a, long period, Vec2 pos, Vec2 dir, Vec2 bounds) {
        super(a, period);
        this.position = pos;
        this.direction = dir;
        this.bounds = bounds;
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
}
