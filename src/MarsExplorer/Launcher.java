package MarsExplorer;

import MarsExplorer.agents.TransporterAgent;
import MarsExplorer.environment.Vec2;
import MarsExplorer.ui.SwingGUI;
import MarsExplorer.ui.TransporterStyle;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.tools.rma.rma;
import jade.wrapper.AgentController;
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

            // Testing Transporter
            TransporterAgent tp = new TransporterAgent(new Vec2(100, 100));
            container.acceptNewAgent("TPAgent", tp).start();
            gui.addComponent(new TransporterStyle(tp));

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }
}
