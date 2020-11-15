package agents.behaviours;

import java.awt.Color;

import agents.TransporterAgent;
import agents.TransporterAgent.States;
import environment.Resource;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;

public class TransportContractResponder extends ContractNetResponder {
    static final long serialVersionUID = 14434L;

    private TransporterAgent tp;
    private Resource potentialResource;

    public TransportContractResponder(TransporterAgent agent) {
        super(agent, MessageTemplate.MatchPerformative(ACLMessage.CFP));
        this.tp = agent;
    }

    protected ACLMessage handleCfp(ACLMessage cfp) {
        ACLMessage reply = cfp.createReply();

        States state = tp.getTPState();
        // Already on a contract
        if (state.equals(States.RETRIEVING) || state.equals(States.DELIVERING)) {
            reply.setPerformative(ACLMessage.REFUSE);
            return reply;
        }
        try {
            Resource res = (Resource) cfp.getContentObject();
            int amount = res.getAmount();
            // Check if transporter has enough space
            if (tp.getCapacity() - tp.getCarrying() > amount) {
                double distance = tp.getPosition().calcDistance(res.getPosition());
                reply.setContent(String.valueOf(distance));
                potentialResource = res;
                reply.setPerformative(ACLMessage.PROPOSE);
            }
        } catch (UnreadableException e) {
            e.printStackTrace();
            reply.setPerformative(ACLMessage.REFUSE);
        }
        return reply;
    }

    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {

    }

    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        ACLMessage result = accept.createReply();
        result.setPerformative(ACLMessage.CONFIRM);
        tp.setFillColor(new Color(255, 87, 15));
        tp.retrieve(potentialResource);
        return result;
    }

    protected void handleOutOfSequence(ACLMessage msg) {

    }

}
