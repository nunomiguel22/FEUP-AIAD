package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAException;

import java.awt.Color;
import java.awt.Graphics;

import agents.behaviours.DrivingBehaviour;
import agents.behaviours.TransportContractResponder;
import commons.Constants;
import environment.Map;
import environment.Resource;
import environment.Vec2;
import ui.SwingStyle;

public class TransporterAgent extends Agent implements SwingStyle {
    public enum States {
        RANDOM, RETRIEVING, DELIVERING, WAITING
    }

    static final long serialVersionUID = 13400L;
    static final int capacity = 100;

    private States state;
    private Map map;
    private Vec2 position;
    private Vec2 direction;
    private Vec2 bounds;
    private Vec2 destination;
    private int destinationAmount;
    private int carrying;
    private int transportedAmount;
    private Color fillColor;

    public TransporterAgent(Vec2 startPos, Map map) {
        this.map = map;
        this.fillColor = Color.RED;
        this.position = startPos;
        this.bounds = map.getBounds();
        this.direction = Vec2.getRandomDirection();
        this.state = States.RANDOM;
        this.carrying = 0;
        this.transportedAmount = 0;
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
        addBehaviour(new TransportContractResponder(this));
    }

    private void goToBase() {
        destination = map.getBaseCoords();
        direction.setVec2(Vec2.getDirection(position, destination));
        state = States.DELIVERING;
        this.fillColor = Color.MAGENTA;
        destinationAmount = 0;
    }

    private void startPatrol() {
        this.fillColor = Color.RED;
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

                        carrying += destinationAmount;
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

    public void retrieve(Resource res) {
        destination = res.getPosition();
        destinationAmount = res.getAmount();
        direction.setVec2(Vec2.getDirection(position, destination));
        state = States.RETRIEVING;
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    @Override
    public void draw(Graphics g, Vec2 scale) {
        int x = (int) (position.getX() * scale.getX());
        int y = Constants.worldHeight - (int) (position.getY() * scale.getY());

        g.setColor(this.fillColor);
        g.fillOval(x - 5, y - 5, 10, 10);
        g.setColor(Color.WHITE);
        g.drawOval(x - 5, y - 5, 10, 10);
    }

    public Vec2 getPosition() {
        return this.position;
    }

    public States getTPState() {
        return this.state;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCarrying() {
        return carrying;
    }

    public int getTotalTransportedAmount() {
        return transportedAmount;
    }

    public void setFillColor(Color clr) {
        this.fillColor = clr;
    }
}
