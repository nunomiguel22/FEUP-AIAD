package mars_Explorer.agents;

import repast.simphony.space.continuous.ContinuousSpace;
import sajas.core.Agent;

public class ExplorerAgent extends Agent {
	
	private ContinuousSpace<Object> space;
	
	public ExplorerAgent(ContinuousSpace<Object> space) {
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
