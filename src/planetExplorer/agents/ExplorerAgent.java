package planetExplorer.agents;

import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import planetExplorer.agents.behaviours.DrivingBehaviour;
import planetExplorer.commons.Constants;
import planetExplorer.environment.Map;
import planetExplorer.environment.Resource;
import planetExplorer.environment.Vec2;
import repast.simphony.space.continuous.ContinuousSpace;
import sajas.core.AID;
import sajas.core.Agent;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ExplorerAgent extends Agent {
    static final long serialVersionUID = 1343400L;
    private Vec2 position;
    private Vec2 direction;
    private Vec2 bounds;
    private List<Resource> resourcesAvailable;
    private final Vec2 baseCoords;
    private ContinuousSpace<Object> space;

    public ExplorerAgent(Vec2 startPosition, Map map) {
        this.position = startPosition;
        this.direction = Vec2.getRandomDirection();
        this.bounds = map.getBounds();
        resourcesAvailable = new ArrayList<>();
        resourcesAvailable.addAll(map.getResources());
        baseCoords = map.getBaseCoords();
        this.space = map.getSpace();
    }

    public Vec2 getPosition() {
        return position;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }

    public List<Resource> getResourcesAvailable() {
        return resourcesAvailable;
    }

    @Override
    protected void setup() {
        addBehaviour(new DrivingBehaviour(this, Constants.explorerTickPeriod, position, direction, bounds, this.space));
        addBehaviour(new ExploreBehaviour(this, Constants.explorerTickPeriod));
        addBehaviour(new ListeningBehaviour());
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    private class ExploreBehaviour extends TickerBehaviour {
        static final long serialVersionUID = 33400L;

        public ExploreBehaviour(ExplorerAgent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {

            if (getResourcesAvailable().isEmpty()) {
                if (position.calcDistance(baseCoords) < 0.5) {
                    direction.setVec2(new Vec2(0, 0));
                    removeBehaviour(this);
                    return;
                }
                goToBase();
                return;
            }

            Predicate<Resource> found = r -> r.getPosition()
                    .calcDistance(getPosition()) < Constants.explorerDistanceFromResource;
            getResourcesAvailable().stream().filter(found).forEach(r -> {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                String msgStr = String.format("FOUND %d %d", (int) r.getPosition().getX(),
                        (int) r.getPosition().getY());
                r.setColor(Color.CYAN);
                msg.setContent(msgStr);
                msg.addReceiver(new AID("Base", AID.ISLOCALNAME));
                send(msg);
            });

            getResourcesAvailable().removeIf(found);
        }

        private void goToBase() {
            direction.setVec2(Vec2.getDirection(position, baseCoords));
        }
    }

    private class ListeningBehaviour extends CyclicBehaviour {
        static final long serialVersionUID = 344560L;

        @Override
        public void action() {
            ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));

            if (message != null) {
                String[] content = message.getContent().split(" ");
                if (content[0].equals("FOUND")) {
                    resourcesAvailable.removeIf(r -> (int) r.getPosition().getX() == Integer.parseInt(content[1])
                            && (int) r.getPosition().getY() == Integer.parseInt(content[2]));
                }
            }
        }
    }
}
