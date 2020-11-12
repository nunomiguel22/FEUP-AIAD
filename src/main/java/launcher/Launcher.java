package launcher;

import java.util.List;

import agents.BaseAgent;
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

        try {
            launchAgents(container, map);
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }

    public static void launchAgents(ContainerController container, Map map) throws StaleProxyException {

        try {
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

            List<Vec2> tpCoords = map.getTransporterCoords();
            for (int i = 0; i < tpCoords.size(); ++i) {
                String name = "TPAgent" + String.valueOf(i);
                TransporterAgent tp = new TransporterAgent(tpCoords.get(i), map);
                gui.addStyle(tp);
                base.registerAgent(name);
                container.acceptNewAgent(name, tp).start();
            }

            // Add explorer agent

            ExplorerAgent explorerAgent = new ExplorerAgent(
                    Vec2.of(Constants.explorerStartXPos, Constants.explorerStartYPos), map);
            gui.addStyle(explorerAgent);
            base.registerAgent("Explorer");
            container.acceptNewAgent("Explorer", explorerAgent).start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }
}
