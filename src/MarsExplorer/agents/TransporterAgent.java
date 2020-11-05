package MarsExplorer.agents;

import MarsExplorer.environment.Vec2;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class TransporterAgent extends Agent {
    static final long serialVersionUID = 1L;

    private Vec2 position;

    public TransporterAgent(Vec2 startPos) {
        this.position = startPos;
    }

    public Vec2 getPosition() {
        return this.position;
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

        addBehaviour(new DrivingBehaviour(this, 50));
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    private class DrivingBehaviour extends TickerBehaviour {
        private static final long serialVersionUID = 1L;

        public DrivingBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            position.addVec2(new Vec2(1, 1));
        }
    }

}
