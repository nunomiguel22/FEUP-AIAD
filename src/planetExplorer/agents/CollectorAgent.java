package planetExplorer.agents;

import planetExplorer.agents.behaviours.DrivingBehaviour;
import repast.simphony.space.continuous.ContinuousSpace;

import planetExplorer.commons.Constants;
import planetExplorer.environment.Map;
import planetExplorer.environment.Resource;
import planetExplorer.environment.Vec2;

import sajas.core.AID;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.core.behaviours.TickerBehaviour;
import sajas.core.Agent;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


import java.util.ArrayList;

public class CollectorAgent extends Agent{
    static final long serialVersionUID = 567L;

    public enum States {
        PATROLLING, MINING, WAITINGFORTRANSPORT, CHECKING, MOVING
    }

    private States state;
    private final Map map;
    private final Vec2 position;
    private final Vec2 bounds;
    private Vec2 direction;
    private Vec2 destination;
    private Resource resourceToMine;
    private ArrayList<Resource> resourcesLeft;
    private int amountMined = 0;
    private int totalAmountMined = 0;
    private int tickDelay = 0;
    private int transportWait = 0;
    private ContinuousSpace<Object> space;

    public CollectorAgent(Vec2 startPos, Map map) {
    	this.map = map;
    	this.space = map.getSpace();
        this.position = startPos;
        this.bounds = map.getBounds();
        this.direction = Vec2.getRandomDirection();
        this.state = States.PATROLLING;
        this.resourcesLeft = new ArrayList<>();
    }

    public Vec2 getPosition() {
        return this.position;
    }

    @Override
    protected void setup() {
        addBehaviour(new DrivingBehaviour(this, Constants.collectorTickPeriod, position, direction, bounds, this.space));
        addBehaviour(new CollectorBehaviour(this, Constants.collectorTickPeriod));
        addBehaviour(new ListeningBehaviour());
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }


    private void goToResource() {
        direction.setVec2(Vec2.getDirection(position, destination));
    }

    private void startPatrol() {
        direction.setVec2(Vec2.getRandomDirection());
        state = States.PATROLLING;
    }

    public int getTotalAmountMined() {
        return this.totalAmountMined;
    }

    private class CollectorBehaviour extends TickerBehaviour {
        static final long serialVersionUID = 3550L;

        public CollectorBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            switch (state) {
                case WAITINGFORTRANSPORT: {
                    ++transportWait;
                    if (transportWait > 240) {
                        transportWait = 0;
                        AID base = new AID("Base", AID.ISLOCALNAME);
                        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                        String messageStr = String.format("RETRIEVE %d %d %d", amountMined, (int) destination.getX(),
                                (int) destination.getY());
                        message.setContent(messageStr);
                        message.addReceiver(base);
                        send(message);
                    }
                    break;
                }
                case PATROLLING: {
                    break;
                }

                case MINING: {
                    ACLMessage message = new ACLMessage(ACLMessage.INFORM);

                    AID base = new AID("Base", AID.ISLOCALNAME);
                    if (resourceToMine != null && amountMined >= resourceToMine.getAmount()) {
                        ++tickDelay;
                        if (tickDelay == amountMined / 2) {
                            totalAmountMined += amountMined;
                            String messageStr = String.format("RETRIEVE %d %d %d", amountMined,
                                    (int) destination.getX(), (int) destination.getY());
                            message.setContent(messageStr);
                            message.addReceiver(base);
                            send(message);
                            state = States.WAITINGFORTRANSPORT;
                        }

                        return;
                    }

                    String messageStr = String.format("MINING %d %d", (int) destination.getX(),
                            (int) destination.getY());
                    message.setContent(messageStr);
                    message.addReceiver(base);
                    send(message);

                    ++amountMined;
                    break;
                }

                case MOVING: {
                    if (position != destination) {
                        if (position.calcDistance(destination) < 0.5) {
                            direction.setVec2(new Vec2(0, 0));
                            resourceToMine = map.getResourceAt(destination);
                            state = States.MINING;
                            return;
                        }
                        goToResource();
                    }
                    break;
                }

                default:
                    break;
            }
        }
    }

    private class ListeningBehaviour extends CyclicBehaviour {
        private final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        static final long serialVersionUID = 85135220L;

        @Override
        public void action() {
            ACLMessage message = receive(messageTemplate);

            if (message != null) {
                String[] content = message.getContent().split(" ");
                if (content[0].equals("FOUND")) {
                    int xCoord = Integer.parseInt(content[1]);
                    int yCoord = Integer.parseInt(content[2]);

                    Resource resource = map.getResourceAt(Vec2.of(xCoord, yCoord));
                    resourcesLeft.add(resource);

                    if (state == States.PATROLLING) {
                        resourceToMine = resource;
                        destination = Vec2.of(xCoord, yCoord);
                        state = States.MOVING;
                    }
                    // state = States.CHECKING;

                } else if (content[0].equals("TRANSPORT")) {
                    int xCoord = Integer.parseInt(content[1]);
                    int yCoord = Integer.parseInt(content[2]);
                    Vec2 transportPos = Vec2.of(xCoord, yCoord);

                    if (transportPos.equals(destination)) {
                        resourcesLeft.removeIf(
                                resource -> resource.getPosition().getX() == resourceToMine.getPosition().getX()
                                        && resource.getPosition().getY() == resourceToMine.getPosition().getY());
                        map.removeResource(resourceToMine);
                        amountMined = 0;
                        tickDelay = 0;

                        if (resourcesLeft.isEmpty()) {
                            startPatrol();
                        } else {
                            resourceToMine = resourcesLeft.get(0);
                            destination = resourceToMine.getPosition();
                            state = States.MOVING;
                            goToResource();
                        }
                    }
                } else if (content[0].equals("MINING")) {
                    int xCoord = Integer.parseInt(content[1]);
                    int yCoord = Integer.parseInt(content[2]);
                    Vec2 resourcePos = Vec2.of(xCoord, yCoord);

                    if (resourcePos.equals(destination)) {
                        for (int i = 0; i < resourcesLeft.size(); ++i) {
                            Resource res = resourcesLeft.get(i);
                            if (res == null || res.getPosition().equals(resourceToMine.getPosition()))
                                resourcesLeft.remove(i);
                        }

                        /*
                         * resourcesLeft.removeIf( resource -> resource.getPosition().getX() ==
                         * resourceToMine.getPosition().getX() && resource.getPosition().getY() ==
                         * resourceToMine.getPosition().getY());
                         */

                        amountMined = 0;
                        tickDelay = 0;
                        destination = null;

                        if (resourcesLeft.isEmpty()) {
                            startPatrol();
                        } else {
                            resourceToMine = resourcesLeft.get(0);
                            destination = resourceToMine.getPosition();
                            state = States.MOVING;
                            goToResource();
                        }
                    }
                }
            } else {
                block();
            }
        }
    }
}
