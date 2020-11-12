package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAException;

import java.awt.Color;
import java.awt.Graphics;

import agents.behaviours.DrivingBehaviour;
import commons.Constants;
import environment.Map;
import environment.Vec2;
import ui.SwingStyle;

public class TransporterAgent extends Agent implements SwingStyle {
    private enum States {
        RANDOM, RETRIEVING, DELIVERING, WAITING
    }

    static final long serialVersionUID = 1L;
    static final int capacity = 30;

    private States state;
    private Map map;
    private Vec2 position;
    private Vec2 direction;
    private Vec2 bounds;
    private Vec2 destination;
    private int destinationAmount;
    private int carrying;
    private int transportedAmount;

    public TransporterAgent(Vec2 startPos, Map map) {
        this.map = map;
        this.position = startPos;
        this.bounds = map.getBounds();
        this.direction = Vec2.getRandomDirection();
        this.state = States.RANDOM;
        this.carrying = 0;
    }

    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("Transporter");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new DrivingBehaviour(this, 33, position, direction, bounds));
        addBehaviour(new TransporterBehaviour(this, 33));
        addBehaviour(new ListeningBehaviour());
    }

    private void goToBase() {
        destination = map.getBaseCoords();
        direction.setVec2(Vec2.getDirection(position, destination));
        state = States.DELIVERING;
        destinationAmount = 0;
    }

    private void startPatrol() {
        state = States.RANDOM;
        destination = null;
        destinationAmount = 0;
        direction.setVec2(Vec2.getRandomDirection());
    }

    private class TransporterBehaviour extends TickerBehaviour {
        private static final long serialVersionUID = 1L;

        public TransporterBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            switch (state) {
                case RANDOM: {
                    return;
                }

                case RETRIEVING: {
                    if (position.calcDistance(destination) <= 1.5) {
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        String msgstr = String.format("TRANSPORT %d %d", (int) destination.getX(),
                                (int) destination.getY());
                        msg.setContent(msgstr);
                        msg.addReceiver(new AID("Base", AID.ISLOCALNAME));
                        send(msg);

                        goToBase();
                    }
                    break;
                }
                case DELIVERING: {
                    if (position.calcDistance(destination) <= 1.5) {
                        transportedAmount += carrying;
                        carrying = 0;
                        startPatrol();
                    }
                }

                default:
                    break;
            }
        }
    }

    private class ListeningBehaviour extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            ACLMessage msg = receive();

            if (msg != null) {
                String[] info = msg.getContent().split(" ");

                /**
                 * RETRIEVE MESSAGE
                 * 
                 * EX: "RETRIEVE 20 30 50"
                 * 
                 * Means to retrieve 20 resources at X=30 and Y=50
                 */
                if (info[0].equals("RETRIEVE")) {
                    // Check if transporter is full or already on a mission
                    if (state.equals(States.RETRIEVING))
                        return;

                    int amount = Integer.parseInt(info[1]);
                    // Check if transporter has enough space
                    if (capacity - carrying < amount)
                        return;

                    int destX = Integer.parseInt(info[2]);
                    int destY = Integer.parseInt(info[3]);
                    destination = new Vec2(destX, destY);
                    destinationAmount = amount;
                    direction.setVec2(Vec2.getDirection(position, destination));
                    state = States.RETRIEVING;
                }
                /**
                 * TRANSPORTING MESSAGE
                 * 
                 * EX: "TRANSPORT 30 50"
                 * 
                 * Means a transporter collected resources at X=30 and Y=50
                 */
                if (info[0].equals("TRANSPORT")) {
                    // This message only matters for retrieving transporters
                    if (!state.equals(States.RETRIEVING))
                        return;

                    int destX = Integer.parseInt(info[2]);
                    int destY = Integer.parseInt(info[2]);
                    Vec2 tpLocation = new Vec2(destX, destY);

                    // Return to delivering or patroling if there is already a tp on scene
                    if (destination.equals(tpLocation)) {
                        if (carrying > 0)
                            goToBase();
                        else
                            startPatrol();

                    }
                }
            }
        }
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    @Override
    public void draw(Graphics g, Vec2 scale) {
        int x = (int) (position.getX() * scale.getX());
        int y = Constants.worldHeight - (int) (position.getY() * scale.getY());

        g.setColor(Color.RED);
        g.fillOval(x, y, 10, 10);
        g.setColor(Color.WHITE);
        g.drawOval(x, y, 10, 10);
    }

    public Vec2 getPosition() {
        return this.position;
    }
}
