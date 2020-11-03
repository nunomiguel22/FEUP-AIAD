package mars_Explorer.agents;

import sajas.core.Agent;
import sajas.core.behaviours.TickerBehaviour;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class TransporterAgent extends Agent {
	
	private ContinuousSpace<Object> space;
	public int x, y;
	
	public TransporterAgent(ContinuousSpace<Object> space) {
		this.space = space;
		this.x = 0;
		this.y = 0;
	}
	
	@Override
	public void setup() {
		addBehaviour(new DrivingBehaviour(this, 50));
	}
	
	@Override
	public void takeDown() {
		
	}
	
	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
		space.moveTo(this, x, y);
	}
	
	private class DrivingBehaviour extends TickerBehaviour {
		private static final long serialVersionUID = 1L;
				
		public DrivingBehaviour(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			moveTo(x+1, y+1);
		}
	}
}
