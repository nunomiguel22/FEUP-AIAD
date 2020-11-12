//package agents.behaviours;
//
//import agents.ExplorerAgent;
//import environment.Resource;
//import jade.core.AID;
//import jade.core.behaviours.TickerBehaviour;
//import jade.lang.acl.ACLMessage;
//
//import java.util.function.Predicate;
//
//public class ExploreBehaviour extends TickerBehaviour {
//
//    ExplorerAgent agent;
//
//    public ExploreBehaviour(ExplorerAgent a, long period) {
//        super(a, period);
//        this.agent = a;
//    }
//
//    @Override
//    protected void onTick() {
//
//        Predicate<Resource> found = r -> r.getPosition().calcDistance(agent.getPosition()) < 10;
//        agent.getResourcesAvailable().stream()
//                .filter(found)
//                .forEach(r -> {
//                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
//                    String msgStr = String.format("FOUND %d %d", (int) r.getPosition().getX(), (int) r.getPosition().getY());
//                    msg.setContent(msgStr);
//                    msg.addReceiver(new AID("Base", AID.ISLOCALNAME));
//                    agent.send(msg);
//                });
//
//        agent.getResourcesAvailable().removeIf(found);
//    }
//}
