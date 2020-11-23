package planetExplorer.agents;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import planetExplorer.agents.behaviours.TransportContractInitiator;
import planetExplorer.commons.Constants;
import planetExplorer.environment.Map;
import planetExplorer.environment.Resource;
import planetExplorer.environment.Vec2;
import planetExplorer.launcher.Launcher;
import sajas.core.AID;
import sajas.core.Agent;
import sajas.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.proto.ContractNetInitiator;

public class BaseAgent extends Agent {
    static final long serialVersionUID = 134L;

    private Vec2 position;
    private Map map;
    private ArrayList<String> agents;
    private ArrayList<String> transporters;
    private ArrayList<Resource> futureContracts;
    private ContractNetInitiator currectContract;

    public BaseAgent(Vec2 pos, Map map) {
        this.position = pos;
        this.map = map;
        this.agents = new ArrayList<String>();
        this.transporters = new ArrayList<String>();
        this.futureContracts = new ArrayList<Resource>();
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
        Launcher.logOutput();
        System.exit(0);
    }

    @Override
    protected void setup() {
        addBehaviour(new ListeningBehaviour());
    }

    private void initiateTransportContract(Resource res) {
        if (TransportContractInitiator.isInContract()) {
            if (!futureContracts.contains(res))
                futureContracts.add(res);
            return;
        }
        if (currectContract != null)
            removeBehaviour(currectContract);

        currectContract = new TransportContractInitiator(this, res);
        addBehaviour(currectContract);
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


    public Vec2 getPosition() {
        return this.position;
    }
}
