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
 * Class, that implements console command "remove_all_by_description"
 */

@CommandInfo(name = "remove_all_by_description", description = "remove from the collection all elements whose description field value is equivalent to the given one")
public class RemoveAllByDescriptionCommand implements ClientCommand {

    public void execute(List<String> tokens) {}

    @Override
    public String executeClient(CommandWrapper commandWrapper) {

        List<String> tokens  = commandWrapper.getTokens();

        try {
            if (tokens.size() != 1) {
                throw new TokenMismatchException(1);
            }
        } catch (TokenMismatchException e) {
            return e.getMessage();
        }

        StringBuilder sb = new StringBuilder();

        String subString = tokens.get(0);

        CollectionManager cm = CollectionManager.getInstance();
        Vector<DragonX> collection = cm.getCollection();

        sb.append("Dragons to be removed:\n");

        Vector<DragonX> toDelete = new Vector<>();
        collection.stream().filter(item ->
                item.getDescription().startsWith(subString)
        ).forEach(item -> {
            sb.append(item.returnInfo());
            toDelete.add(item);
        });
        if (toDelete.isEmpty()) {
            return "No dragons found which description starts with given substring";
        }
        DragonDatabaseManager dm = new DragonDatabaseManager();
        toDelete.forEach(item -> {
            if (item.getOwner().equals(commandWrapper.getUsername())) {
                dm.deleteDragon(item.getId());
                collection.remove(item);
                sb.append("Removed dragon with ID: ").append(item.getId()).append("\n");
            } else {
                sb.append("Cannot remove the dragon with ID: ")
                        .append(item.getId())
                        .append(". YOU DON'T OWN THIS DRAGON.\nReal owner: ")
                        .append(Colors.YELLOW).append(item.getOwner()).append(Colors.RESET);
            }
        });

        sb.append("Command ended with success");
        return sb.toString();
    }
}
