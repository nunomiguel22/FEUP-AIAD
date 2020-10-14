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

        ContainerController mainContainer = runtime.createMainContainer(profile);

        try {
            AgentController rmaController = mainContainer.acceptNewAgent("rma", new rma());
            rmaController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
