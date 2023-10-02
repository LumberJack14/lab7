package LocalCommands;

import Annotations.CommandInfo;
import Collection.DragonX;
import Exceptions.TokenMismatchException;
import Utils.CollectionManager;
import Utils.Colors;
import Utils.CommandWrapper;
import Utils.DragonDatabaseManager;

import java.util.List;
import java.util.Optional;
import java.util.Vector;

/**
 * Class, that implements console command "remove_by_id"
 */

@CommandInfo(name = "remove_by_id", description = "remove an element from the collection by its id")
public class RemoveById implements ClientCommand {

    public void execute(List<String> tokens) {
    }

    @Override
    public String executeClient(CommandWrapper commandWrapper) {

        List<String> tokens = commandWrapper.getTokens();

        try {
            if (tokens.size() != 1) {
                throw new TokenMismatchException(1);
            }
        } catch (TokenMismatchException e) {
            return e.getMessage();
        }

        long givenID;

        try {
            givenID = Long.parseLong(tokens.get(0));
        } catch (NumberFormatException e) {
            return "Error while parsing ID" + e.getMessage();
        }

        CollectionManager cm = CollectionManager.getInstance();
        Vector<DragonX> collection = cm.getCollection();

        StringBuilder sb = new StringBuilder();

        Optional<DragonX> toDelete = collection.stream().filter(item -> item.getId() == givenID).findFirst();

        if (toDelete.isEmpty()) {
            return "Couldn't find a dragon with provided ID.";
        }

        if (!toDelete.get().getOwner().equals(commandWrapper.getUsername())) {
            sb.append("Cannot remove the dragon with ID: ")
                    .append(toDelete.get().getId())
                    .append(". YOU DON'T OWN THIS DRAGON.\nReal owner: ")
                    .append(Colors.YELLOW).append(toDelete.get().getOwner()).append(Colors.RESET);
            return sb.toString();
        }

        DragonDatabaseManager dm = new DragonDatabaseManager();
        int status = dm.deleteDragon(givenID);
        if (!(status > 0)) {
            return "(database error) couldn't delete the dragon with such ID. Make sure that it exists.";
        }

        sb.append("Dragon that will be deleted: \n").append(toDelete.get().returnInfo());
        collection.remove(toDelete.get());
        sb.append("Dragon with ID '").append(givenID).append("' was successfully removed\n");

        return sb.toString();
    }
}
