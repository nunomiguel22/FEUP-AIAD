package launcher;

import agents.ExplorerAgent;
import agents.TransporterAgent;
import commons.Constants;
import environment.Vec2;
import ui.ExplorerStyle;
import ui.SwingGUI;
import ui.TransporterStyle;
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

        try {
            launchAgents(container);
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }

    public static void launchAgents(ContainerController container) throws StaleProxyException {

        try {
            container.acceptNewAgent("rma", new rma()).start();
            // Init Swing Gui
            SwingGUI gui = new SwingGUI();
            container.acceptNewAgent("swing", gui).start();

            //Testing Transporter
            TransporterAgent tp = new TransporterAgent(new Vec2(100, 100));
            container.acceptNewAgent("TPAgent", tp).start();
            gui.addComponent(new TransporterStyle(tp));

            //Add explorer agent
            ExplorerAgent explorerAgent = new ExplorerAgent(Vec2.of(Constants.explorerStartXPos, Constants.explorerStartYPos));
            container.acceptNewAgent("Explorer", explorerAgent).start();
            gui.addComponent(new ExplorerStyle(explorerAgent));

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }
}
