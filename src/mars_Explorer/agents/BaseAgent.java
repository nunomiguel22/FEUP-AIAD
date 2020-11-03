package mars_Explorer.agents;

import repast.simphony.space.continuous.ContinuousSpace;
import sajas.core.Agent;

public class BaseAgent extends Agent {
	
	private ContinuousSpace<Object> space;
	
	public BaseAgent(ContinuousSpace<Object> space) {
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
