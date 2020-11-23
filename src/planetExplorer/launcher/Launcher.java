package planetExplorer.launcher;

import planetExplorer.agents.BaseAgent;
import planetExplorer.agents.CollectorAgent;
import planetExplorer.agents.ExplorerAgent;
import planetExplorer.agents.TransporterAgent;
import planetExplorer.environment.Map;
import planetExplorer.environment.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import sajas.sim.repasts.RepastSLauncher;
import sajas.core.Runtime;
import sajas.wrapper.ContainerController;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.SimpleCartesianAdder;


public class Launcher extends RepastSLauncher {
	private ContinuousSpace<Object> space;
	private Context<Object> context;
	
    static private ArrayList<CollectorAgent> collectors;
    static private ArrayList<TransporterAgent> transporters;
    static private double startTime;
    static private Map map;
	
	@Override
	public String getName() {
		return "Planet Explorer";
	}
	
	@Override
	protected void launchJADE() {
	    collectors = new ArrayList<CollectorAgent>();
	    transporters = new ArrayList<TransporterAgent>();
		
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        ContainerController container = runtime.createAgentContainer(profile);

        startTime = System.currentTimeMillis() / 1000.0;

        launchAgents(container, map);
	}
	
	@Override
	public Context<?> build(Context<Object> context) {	
		context.setId("PlanetExplorer");
		this.context = context;
		
		map = new Map("maps/mars3.txt");
		
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		this.space = spaceFactory.createContinuousSpace("space", context, 
				new SimpleCartesianAdder<Object>(), new repast.simphony.space.continuous.StrictBorders(), 
				map.getBounds().getX() + 15, map.getBounds().getY() + 15);
		
		map.init(this.space, this.context);
		
		return super.build(context);
	}
	
    public static void launchAgents(ContainerController container, Map map) {
    	Context<Object> context = map.getContext();
    	ContinuousSpace<Object> space = map.getSpace();

        try {
            // Add Base Agent
            BaseAgent base = new BaseAgent(map.getBaseCoords(), map);
            container.acceptNewAgent("Base", base).start();
            context.add(base);
            space.moveTo(base, base.getPosition().getX(), base.getPosition().getY());

            // Add explorer agents
            int explorers = map.getExplorerCoords().size();
            for (Vec2 e : map.getExplorerCoords()) {
                ExplorerAgent explorerAgent = new ExplorerAgent(Vec2.of(e.getX(), e.getY()), map);
                String name = "Explorer" + explorers--;
                base.registerAgent(name);
                container.acceptNewAgent(name, explorerAgent).start();
                context.add(explorerAgent);
                space.moveTo(explorerAgent, explorerAgent.getPosition().getX(), explorerAgent.getPosition().getY());
            }

            // Add transporter agents
            List<Vec2> tpCoords = map.getTransporterCoords();
            for (int i = 0; i < tpCoords.size(); ++i) {
                String name = "TPAgent" + String.valueOf(i);
                TransporterAgent tp = new TransporterAgent(tpCoords.get(i), map);
                transporters.add(tp);
                base.registerTransporter(name);
                container.acceptNewAgent(name, tp).start();
                context.add(tp);
                space.moveTo(tp, tp.getPosition().getX(), tp.getPosition().getY());
            }

            // Add collector agents
            List<Vec2> collectorCoords = map.getCollectorCoords();
            for (int i = 0; i < collectorCoords.size(); ++i) {
                String name = "Collector" + i;
                CollectorAgent cla = new CollectorAgent(collectorCoords.get(i), map);
                collectors.add(cla);
                base.registerAgent(name);
                container.acceptNewAgent(name, cla).start();
                context.add(cla);
                space.moveTo(cla, cla.getPosition().getX(), cla.getPosition().getY());
            }
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean areTransportersCarrying() {
        for (TransporterAgent tp : transporters) {
            if (tp.isCarrying())
                return true;
        }
        return false;
    }

    public static void logOutput() {
        double timeElapsed = (System.currentTimeMillis() / 1000.0) - startTime;

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output/log.txt"));
            writer.append("planet explorer log\n");
            String tm = "Total Time: ";
            tm += String.valueOf(timeElapsed);
            tm += " seconds\n";
            writer.append(tm);

            for (TransporterAgent tp : transporters) {
                String op = tp.getLocalName() + "\t";
                op += String.valueOf(tp.getTotalTransportedAmount()) + "\n";
                writer.append(op);
            }

            for (CollectorAgent cl : collectors) {
                String op = cl.getLocalName() + "\t";
                op += String.valueOf(cl.getTotalAmountMined()) + "\n";
                writer.append(op);
            }

            writer.append("\n\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}