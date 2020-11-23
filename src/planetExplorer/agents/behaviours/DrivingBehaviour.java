package planetExplorer.agents.behaviours;

import planetExplorer.environment.Vec2;
import repast.simphony.space.continuous.ContinuousSpace;
import sajas.core.Agent;
import sajas.core.behaviours.TickerBehaviour;

public class DrivingBehaviour extends TickerBehaviour {
    private static final long serialVersionUID = 1L;
    private Vec2 position;
    private Vec2 direction;
    private Vec2 bounds;
    private ContinuousSpace<Object> space;

    public DrivingBehaviour(Agent a, long period, Vec2 pos, Vec2 dir, Vec2 bounds, ContinuousSpace<Object> space) {
        super(a, period);
        this.position = pos;
        this.direction = dir;
        this.bounds = bounds;
        this.space = space;
    }

    @Override
    protected void onTick() {

        // Check bounds
        Vec2 dir = new Vec2(1, 1);
        boolean outOfBounds = checkBounds(dir);
        if (outOfBounds) {
            direction.setVec2(Vec2.getRandomDirection());
            if (direction.getX() * dir.getX() < 0)
                direction.setX(direction.getX() * -1);

            if (direction.getY() * dir.getY() < 0)
                direction.setY(direction.getY() * -1);
        }

        position.addVec2(direction);
        
        if (position.getX() < 0)
        	position.setX(0);
        if (position.getY() < 0)
        	position.setY(0);
        
        this.space.moveTo(super.getAgent(), position.getX(), position.getY());
    }

    private boolean checkBounds(Vec2 dir) {
        boolean outOfBounds = false;

        if (position.getX() >= bounds.getX()) {
            outOfBounds = true;
            dir.setX(-1);
        } else if (position.getX() <= 0)
            outOfBounds = true;
        else
            dir.setX(0);

        if (position.getY() >= bounds.getY()) {
            outOfBounds = true;
            dir.setY(-1);
        } else if (position.getY() <= 0)
            outOfBounds = true;
        else
            dir.setY(0);

        return outOfBounds;
    }
}
