package agents;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import agents.behaviours.TransportContractInitiator;
import commons.Constants;
import launcher.Launcher;
import environment.Map;
import environment.Resource;
import environment.Vec2;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ui.SwingStyle;

public class BaseAgent extends Agent implements SwingStyle {
    static final long serialVersionUID = 134L;

    private Vec2 position;
    private Map map;
    private ArrayList<String> agents;
    private ArrayList<String> transporters;

    public BaseAgent(Vec2 pos, Map map) {
        this.position = pos;
        this.map = map;
        this.agents = new ArrayList<String>();
        this.transporters = new ArrayList<String>();
    }

    public void registerAgent(String name) {
        this.agents.add(name);
    }

    public void registerTransporter(String name) {
        registerAgent(name);
        this.transporters.add(name);
    }

    public ArrayList<String> getTransporterList() {
        return this.transporters;
    }

    public void broadcastMessage(String content, String senderName) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(content);

        for (String agentName : agents) {
            if (agentName.equals(senderName))
                continue;
            msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
        }
        send(msg);
    }

    public void endMap() {
        System.out.println("All resources collected");
        System.exit(0);
    }

    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("Base");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new ListeningBehaviour());
    }

    private void initiateTransportContract(Resource res) {
        addBehaviour(new TransportContractInitiator(this, res));
    }

    private class ListeningBehaviour extends CyclicBehaviour {
        static final long serialVersionUID = 1321234L;

        @Override
        public void action() {
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));

            if (!map.hasResources()) {
                if (!Launcher.areTransportersCarrying())
                    endMap();
            }

            if (msg != null) {
                // Initiate contract when a collector asks for retrieval
                String[] info = msg.getContent().split(" ");
                if (info[0].equals("RETRIEVE")) {
                    int amount = Integer.parseInt(info[1]);
                    int x = Integer.parseInt(info[2]);
                    int y = Integer.parseInt(info[3]);
                    Resource res = new Resource(new Vec2(x, y), amount);
                    initiateTransportContract(res);
                } else {
                    // Broadcast to all agents
                    String senderName = msg.getSender().getLocalName();
                    broadcastMessage(msg.getContent(), senderName);
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

        g.setColor(Color.GREEN);
        g.fillRect(x - 7, y - 7, 15, 15);
        g.setColor(Color.WHITE);
        g.drawRect(x - 7, y - 7, 15, 15);
    }

    public Vec2 getPosition() {
        return this.position;
    }
}
