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
    private int amountMined = 0;

    public CollectorAgent(Vec2 startPos, Map map) {
        this.map = map;
        this.position = startPos;
        this.bounds = map.getBounds();
        this.direction = Vec2.getRandomDirection();
        this.state = States.PATROLLING;
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

        addBehaviour(new DrivingBehaviour(this, Constants.explorerTickPeriod, position, direction, bounds));
        addBehaviour(new CollectorBehaviour(this, 33));
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
        direction = Vec2.getRandomDirection();
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
                    if (amountMined >= resourceToMine.getAmount()) {
                        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                        String messageStr = String.format("RETRIEVE %d %d %d", amountMined, (int) destination.getX(), (int) destination.getY());
                        message.setContent(messageStr);
                        message.addReceiver(new AID("Base", AID.ISLOCALNAME));
                        send(message);
                        System.out.println("sent retrieve");
                        amountMined = 0;
                        resourceToMine = null;
                        destination = null;
                        patrol();
                        return;
                    }
                    ++amountMined;
                    break;
                }

                case MOVING: {
                    if (position != destination) {
                        if (position.calcDistance(destination) < 0.5) {
                            direction.setVec2(new Vec2(0, 0));
                            resourceToMine = map.getResourceAt(destination);
                            System.out.println(resourceToMine.getAmount());
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

                    destination = Vec2.of(xCoord, yCoord);
                    state = States.MOVING;
                }
            } else {
                block();
            }
        }
    }
}
