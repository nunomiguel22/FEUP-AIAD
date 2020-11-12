package launcher;

import agents.ExplorerAgent;
import agents.TransporterAgent;
import commons.Constants;
import environment.Map;
import environment.Resource;
import environment.Vec2;
import environment.Wharehouse;
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
            container.acceptNewAgent("swing", gui).start();

            // TEMPORARY

            // Testing Transporter
            TransporterAgent tp = new TransporterAgent(new Vec2(10, 10), map.getBounds());
            gui.addStyle(tp);
            container.acceptNewAgent("TPAgent", tp).start();

            // Add explorer agent
            Vec2 explorerStartPosition = Vec2.of(map.getWharehouse().getPosition().getX(), map.getWharehouse().getPosition().getY());
            ExplorerAgent explorerAgent = new ExplorerAgent(
                    explorerStartPosition, map.getBounds());
            gui.addStyle(explorerAgent);
            container.acceptNewAgent("Explorer", explorerAgent).start();

            map.getResources().forEach(gui::addStyle);
            gui.addStyle(map.getWharehouse());

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
