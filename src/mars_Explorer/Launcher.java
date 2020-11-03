package mars_Explorer;

import sajas.sim.repasts.RepastSLauncher;
import sajas.core.Runtime;
import sajas.wrapper.ContainerController;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StrictBorders;



import mars_Explorer.agents.*;

public class Launcher extends RepastSLauncher {
	private ContinuousSpace<Object> space;
	private Context<Object> context;
	
	@Override
	public String getName() {
		return "Mars Explorer";
	}
	
	@Override
	protected void launchJADE() {
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		ContainerController container = rt.createAgentContainer(p1);
		
		try {
			launchAgents(container);
		}
		catch(StaleProxyException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Context<?> build(Context<Object> context) {	
		context.setId("Mars Explorer");
		this.context = context;
		
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		this.space = spaceFactory.createContinuousSpace("space", context, 
				new RandomCartesianAdder<Object>(), new repast.simphony.space.continuous.StrictBorders(), 50, 50);
		

		return super.build(context);
	}
	
	public void launchAgents(ContainerController container) throws StaleProxyException   {
		
		TransporterAgent tp = new TransporterAgent(this.space);
		container.acceptNewAgent("TransporterAgent", tp).start();
		context.add(tp);
	}
}
