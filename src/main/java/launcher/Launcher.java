package launcher;

import java.util.List;

import agents.BaseAgent;
import agents.CollectorAgent;
import agents.ExplorerAgent;
import agents.TransporterAgent;
import commons.Constants;
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
    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();

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
            BaseAgent base = new BaseAgent(map.getBaseCoords());
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
                gui.addStyle(tp);
                base.registerTransporter(name);
                container.acceptNewAgent(name, tp).start();
            }

            // Add collector agents
            List<Vec2> collectorCoords = map.getCollectorCoords();
            for (int i = 0; i < collectorCoords.size(); ++i) {
                String name = "Collector" + i;
                CollectorAgent cla = new CollectorAgent(collectorCoords.get(i), map);
                gui.addStyle(cla);
                base.registerAgent(name);
                container.acceptNewAgent(name, cla).start();
            }
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }
}
