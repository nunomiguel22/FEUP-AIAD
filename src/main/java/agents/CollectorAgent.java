package agents;

import agents.behaviours.DrivingBehaviour;
import commons.Constants;
import environment.Map;
import environment.Resource;
import environment.Vec2;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ui.SwingStyle;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.awt.*;
import java.util.ArrayList;

public class CollectorAgent extends Agent implements SwingStyle {
    static final long serialVersionUID = 567L;

    public enum States {
        PATROLLING, MINING, MOVING
    }

    private States state;
    private final Map map;
    private final Vec2 position;
    private final Vec2 bounds;
    private Vec2 direction;
    private Vec2 destination;
    private Resource resourceToMine;
    private ArrayList<Resource> knownResources;
    private int amountMined = 0;

    public CollectorAgent(Vec2 startPos, Map map) {
        this.map = map;
        this.position = startPos;
        this.bounds = map.getBounds();
        this.direction = Vec2.getRandomDirection();
        this.state = States.PATROLLING;
        this.knownResources = new ArrayList<>();
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

        addBehaviour(new DrivingBehaviour(this, Constants.collectorTickPeriod, position, direction, bounds));
        addBehaviour(new CollectorBehaviour(this, Constants.collectorTickPeriod));
        addBehaviour(new ListeningBehaviour());
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

    private void goToResource() {
        direction.setVec2(Vec2.getDirection(position, destination));
    }


    private void patrol() {
        direction.setVec2(Vec2.getRandomDirection());
        state = States.PATROLLING;
    }


    private class CollectorBehaviour extends TickerBehaviour {
        public CollectorBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            switch (state) {
                case PATROLLING: {
                    break;
                }

                case MINING: {
                    ACLMessage message = new ACLMessage(ACLMessage.INFORM);

                    AID base = new AID("Base", AID.ISLOCALNAME);
                    if (amountMined >= resourceToMine.getAmount()) {
                        String messageStr = String.format("RETRIEVE %d %d %d", amountMined, (int) destination.getX(), (int) destination.getY());
                        message.setContent(messageStr);
                        message.addReceiver(base);
                        send(message);

                        amountMined = 0;
                        resourceToMine = null;
                        destination = null;
                        patrol();
                        return;
                    }

                    String messageStr = String.format("MINING %d %d", (int) destination.getX(), (int) destination.getY());
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

                default:break;
            }
        }
    }

    private class ListeningBehaviour extends CyclicBehaviour {
        private final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

        @Override
        public void action() {
            ACLMessage message = receive(messageTemplate);

            if (message != null) {
                String[] content = message.getContent().split(" ");
                if (content[0].equals("FOUND")) {
                    int xCoord = Integer.parseInt(content[1]);
                    int yCoord = Integer.parseInt(content[2]);

                    knownResources.add(map.getResourceAt(Vec2.of(xCoord, yCoord)));
                    destination = Vec2.of(xCoord, yCoord);
                    state = States.MOVING;
                } else if (content[0].equals("MINING")) {

                }
            } else {
                block();
            }
        }
    }
}
