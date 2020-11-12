package agents;

import environment.Vec2;
import jade.core.Agent;

public abstract class AbstractAgent extends Agent {

    private Vec2 position;
    private Vec2 bounds;
    private Vec2 direction;

    public AbstractAgent(Vec2 startPos, Vec2 bounds) {
        this.position = startPos;
        this.bounds = bounds;
        this.direction = Vec2.getRandomDirection();
    }

    public Vec2 getPosition() {
        return position;
    }

    public Vec2 getBounds() {
        return bounds;
    }

    public Vec2 getDirection() {
        return direction;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }

    public void setDirection(Vec2 direction) {
        this.direction = direction;
    }
}
