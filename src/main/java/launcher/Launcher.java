package launcher;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import agents.BaseAgent;
import agents.CollectorAgent;
import agents.ExplorerAgent;
import agents.TransporterAgent;
import environment.Map;
import environment.Resource;
import environment.Vec2;
import ui.SwingGUI;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.tools.rma.rma;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Launcher {
    static private ArrayList<CollectorAgent> collectors;
    static private ArrayList<TransporterAgent> transporters;

    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();

        collectors = new ArrayList<CollectorAgent>();
        transporters = new ArrayList<TransporterAgent>();

        Profile profile = new ProfileImpl();
        profile.setParameter("port", "8000");

        ContainerController container = runtime.createMainContainer(profile);

        Map map = new Map("maps/mars.txt");

        launchAgents(container, map);
    }

    public static void launchAgents(ContainerController container, Map map) {

        try {
            // Add RMA agent to container
            container.acceptNewAgent("rma", new rma()).start();

            // Init Swing Gui
            SwingGUI gui = new SwingGUI(map);
            map.setGUI(gui);
            container.acceptNewAgent("swing", gui).start();

            // Add resources to gui
            List<Resource> rscr = map.getResources();
            for (Resource resource : rscr)
                gui.addStyle(resource);

            // Add Base Agent
            BaseAgent base = new BaseAgent(map.getBaseCoords(), map);
            gui.addStyle(base);
            container.acceptNewAgent("Base", base).start();

            // Add explorer agents
            int explorers = map.getExplorerCoords().size();
            for (Vec2 e : map.getExplorerCoords()) {
                ExplorerAgent explorerAgent = new ExplorerAgent(Vec2.of(e.getX(), e.getY()), map);
                gui.addStyle(explorerAgent);
                String name = "Explorer" + explorers--;
                base.registerAgent(name);
                container.acceptNewAgent(name, explorerAgent).start();
            }

            // Add transporter agents
            List<Vec2> tpCoords = map.getTransporterCoords();
            for (int i = 0; i < tpCoords.size(); ++i) {
                String name = "TPAgent" + String.valueOf(i);
                TransporterAgent tp = new TransporterAgent(tpCoords.get(i), map);
                transporters.add(tp);
                gui.addStyle(tp);
                base.registerTransporter(name);
                container.acceptNewAgent(name, tp).start();
            }

            // Add collector agents
            List<Vec2> collectorCoords = map.getCollectorCoords();
            for (int i = 0; i < collectorCoords.size(); ++i) {
                String name = "Collector" + i;
                CollectorAgent cla = new CollectorAgent(collectorCoords.get(i), map);
                collectors.add(cla);
                gui.addStyle(cla);
                base.registerAgent(name);
                container.acceptNewAgent(name, cla).start();
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
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output/log.txt"));
            writer.append("planet explorer log\n");

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
