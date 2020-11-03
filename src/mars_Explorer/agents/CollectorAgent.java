package mars_Explorer.agents;

import repast.simphony.space.continuous.ContinuousSpace;
import sajas.core.Agent;

public class CollectorAgent extends Agent {
	
	private ContinuousSpace<Object> space;
	
	public CollectorAgent(ContinuousSpace<Object> space) {
		this.space = space;
	}
	
	@Override
	public void setup() {
		//Setup before starting
	}
	
	@Override
	public void takeDown() {
		
	}

}
