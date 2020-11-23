package planetExplorer.agents.behaviours;

import planetExplorer.agents.BaseAgent;
import planetExplorer.environment.Resource;
import sajas.core.AID;
import sajas.proto.ContractNetInitiator;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class TransportContractInitiator extends ContractNetInitiator {
    static final long serialVersionUID = 1224434L;
    private Resource resource;
    private BaseAgent base;
    private static boolean inContract = false;

    public static boolean isInContract() {
        return inContract;
    }

    public TransportContractInitiator(BaseAgent base, Resource res) {
        super(base, new ACLMessage(ACLMessage.CFP));
        this.base = base;
        this.resource = res;
    }

    protected Vector prepareCfps(ACLMessage cfp) {
        inContract = true;
        Vector vec = new Vector();

        List<String> tps = base.getTransporterList();

        // Add all transporters as receivers
        for (String tp : tps)
            cfp.addReceiver(new AID(tp, AID.ISLOCALNAME));

        // Add Resource object to message
        try {
            cfp.setContentObject(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        vec.add(cfp);
        return vec;
    }

    protected void handleAllResponses(Vector responses, Vector acceptances) {
        ACLMessage closestTp = null;
        double dist = 99999999;

        for (Object response : responses) {
            ACLMessage received = ((ACLMessage) response);
            ACLMessage reply = ((ACLMessage) response).createReply();
            if (received.getContent() == null) {
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                continue;
            }

            double receivedDist = Double.parseDouble(received.getContent());

            if (receivedDist < dist) {
                dist = receivedDist;
                closestTp = reply;
            }
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            acceptances.add(reply);
        }

        if (closestTp != null)
            closestTp.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        inContract = false;
    }

    protected void handleAllResultNotifications(Vector resultNotifications) {

    }

}
