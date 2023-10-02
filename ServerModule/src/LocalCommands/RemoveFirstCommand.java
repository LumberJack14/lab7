package LocalCommands;

import Annotations.CommandInfo;
import Collection.DragonX;
import Exceptions.TokenMismatchException;
import Utils.CollectionManager;
import Utils.Colors;
import Utils.CommandWrapper;
import Utils.DragonDatabaseManager;

import java.util.List;
import java.util.Vector;

/**
 * Class, that implements console command "remove_first"
 */

@CommandInfo(name = "remove_first", description = "remove the first element from the collection")
public class RemoveFirstCommand implements ClientCommand {

    public void execute(List<String> tokens){}

    @Override
    public String executeClient(CommandWrapper commandWrapper) {
        List<String> tokens = commandWrapper.getTokens();
        try {
            if (!tokens.isEmpty()) {
                throw new TokenMismatchException(0);
            }
        } catch (TokenMismatchException e) {
            return e.getMessage();
        }

        StringBuilder sb = new StringBuilder();

        CollectionManager cm = CollectionManager.getInstance();
        Vector<DragonX> collection = cm.getCollection();
        DragonX firstDragon;

        try {
            firstDragon = collection.get(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            return "Error: No dragon found";
        }

        sb.append("Dragon to be removed:\n");
        sb.append(firstDragon.returnInfo());

        if (!commandWrapper.getUsername().equals(firstDragon.getOwner())) {
            sb.append("You don't own the first dragon. Cannot remove it. Owner: ").append(Colors.YELLOW)
                    .append(commandWrapper.getUsername()).append(Colors.RESET).append("'.\n");
            return sb.toString();
        }

        DragonDatabaseManager dm = new DragonDatabaseManager();
        int status = dm.deleteDragon(firstDragon.getId());
        if (!(status > 0)) {
            return "Error: no such dragon in the database.";
        }

        collection.remove(firstDragon);
        sb.append("The dragon was successfully removed!");

        return sb.toString();
    }
}
