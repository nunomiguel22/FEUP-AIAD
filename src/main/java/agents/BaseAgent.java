package agents;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import commons.Constants;
import environment.Vec2;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import ui.SwingStyle;

public class BaseAgent extends Agent implements SwingStyle {
    static final long serialVersionUID = 134L;
    private Vec2 position;

    private ArrayList<String> agents;

    public BaseAgent(Vec2 pos) {
        this.position = pos;
        this.agents = new ArrayList<String>();
    }

    public void registerAgent(String name) {
        this.agents.add(name);
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

    private class ListeningBehaviour extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            ACLMessage msg = receive();

            if (msg != null) {
                String senderName = msg.getSender().getLocalName();
                broadcastMessage(msg.getContent(), senderName);
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
        g.fillRect(x, y, 15, 15);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, 15, 15);
    }

    public Vec2 getPosition() {
        return this.position;
    }
}
