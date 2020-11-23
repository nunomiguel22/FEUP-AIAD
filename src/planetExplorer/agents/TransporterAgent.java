package planetExplorer.agents;

import sajas.core.AID;
import sajas.core.Agent;
import sajas.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.awt.Color;

import planetExplorer.agents.behaviours.DrivingBehaviour;
import planetExplorer.agents.behaviours.TransportContractResponder;
import planetExplorer.commons.Constants;
import planetExplorer.environment.Map;
import planetExplorer.environment.Resource;
import planetExplorer.environment.Vec2;
import repast.simphony.space.continuous.ContinuousSpace;

public class TransporterAgent extends Agent {
    public enum States {
        RANDOM, RETRIEVING, DELIVERING, WAITING
    }

    static final long serialVersionUID = 13400L;
    static final int capacity = 110;

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
    private ContinuousSpace<Object> space;

    public TransporterAgent(Vec2 startPos, Map map) {
        this.map = map;
        this.fillColor = Color.RED;
        this.position = startPos;
        this.bounds = map.getBounds();
        this.direction = Vec2.getRandomDirection();
        this.state = States.RANDOM;
        this.carrying = 0;
        this.transportedAmount = 0;
        this.space = map.getSpace();
    }

    @Override
    protected void setup() {
        addBehaviour(new DrivingBehaviour(this, 33, position, direction, bounds, this.space));
        addBehaviour(new TransporterBehaviour(this, 33));
        addBehaviour(new TransportContractResponder(this));
    }

    public boolean isCarrying() {
        return this.carrying > 0;
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
    	fillColor = clr;
    }
    
    public Color getColor() {
    	return fillColor;
    }
}
