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
 * Class, that implements console command "clear"
 */

@CommandInfo(name = "clear", description = "clear the collection")
public class ClearCommand implements ClientCommand {

    public void execute(List<String> tokens) {}

    public String executeClient(CommandWrapper cm) {
        List<String> tokens = cm.getTokens();

        try {
            if (!tokens.isEmpty()) {
                throw new TokenMismatchException(0);
            }
        } catch (TokenMismatchException e) {
            return e.getMessage();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Only the dragons with owner ").append(Colors.YELLOW).append(cm.getUsername()).append(Colors.RESET)
                .append(" will be deleted. You don't have the rights to delete the other ones.\n");

        CollectionManager collectionManager = CollectionManager.getInstance();
        DragonDatabaseManager dm = new DragonDatabaseManager();
        int status = dm.clearTable(cm);
        if (!(status > 0)) {
            return "Nothing to clear";
        }
        Vector<DragonX> collection = collectionManager.getCollection();
        sb.append("Dragons to be removed:\n");

        Vector<DragonX> toDelete = new Vector<>();
        collection.stream().filter(item ->
                item.getOwner().equals(cm.getUsername())
        ).forEach(item -> {
            sb.append(item.returnInfo());
            toDelete.add(item);
        });

        if (toDelete.isEmpty()) {
            sb.append("No dragons found with owner ").append(Colors.YELLOW).append(cm.getUsername()).append(Colors.RESET)
                    .append(".\n");
            return sb.toString();
        }
        toDelete.forEach(collection::remove);
        collectionManager.setCollection(collection);
        sb.append("Collection was cleared.");
        return sb.toString();
    }
}
