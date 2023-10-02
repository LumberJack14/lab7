import Utils.CollectionManager;
import Utils.ServerCLI;


public class Main {
    public static void main(String[] args) {
        CollectionManager.getInstance().initializeCollection();
        ServerCLI cli = new ServerCLI();
        cli.run();
    }
}
