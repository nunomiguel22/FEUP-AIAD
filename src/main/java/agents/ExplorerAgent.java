package agents;

import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import agents.behaviours.DrivingBehaviour;
import commons.Constants;
import environment.Map;
import environment.Resource;
import environment.Vec2;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import ui.SwingStyle;
import jade.domain.FIPAException;

public class ExplorerAgent extends Agent implements SwingStyle {

    private Vec2 position;
    private Vec2 direction;
    private Vec2 bounds;
    private List<Resource> resourcesAvailable;
    private final Vec2 baseCoords;

    public ExplorerAgent(Vec2 startPosition, Map map) {
        this.position = startPosition;
        this.direction = Vec2.getRandomDirection();
        this.bounds = map.getBounds();
        resourcesAvailable = new ArrayList<>();
        resourcesAvailable.addAll(map.getResources());
        baseCoords = map.getBaseCoords();
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
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("Explorer");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new DrivingBehaviour(this, Constants.explorerTickPeriod, position, direction, bounds));
        addBehaviour(new ExploreBehaviour(this, Constants.explorerTickPeriod));
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

        g.setColor(Color.BLUE);
        g.fillOval(x, y, 10, 10);
        g.setColor(Color.WHITE);
        g.drawOval(x, y, 10, 10);
    }

    private class ExploreBehaviour extends TickerBehaviour {

        public ExploreBehaviour(ExplorerAgent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {

            if (getResourcesAvailable().isEmpty()) {
                if (position.equals(baseCoords)) {
                    return;
                }
                goToBase();
                return;
            }

            Predicate<Resource> found = r -> r.getPosition().calcDistance(getPosition()) < Constants.explorerDistanceFromResource;
            getResourcesAvailable().stream()
                    .filter(found)
                    .forEach(r -> {
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        String msgStr = String.format("FOUND %d %d", (int) r.getPosition().getX(), (int) r.getPosition().getY());
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

        @Override
        public void action() {
            ACLMessage message = receive();

            if (message != null) {
                String[] content = message.getContent().split(" ");
                if (content[0].equals("FOUND")) {
                    resourcesAvailable.removeIf(r ->
                            (int) r.getPosition().getX() == Integer.parseInt(content[1]) && (int) r.getPosition().getY() == Integer.parseInt(content[2]));
                }
            }
        }
    }
}
