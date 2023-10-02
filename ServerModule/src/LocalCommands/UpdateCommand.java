package LocalCommands;

import Annotations.CommandInfo;
import Collection.DragonX;
import Exceptions.TokenMismatchException;
import Utils.CollectionManager;
import Utils.Colors;
import Utils.CommandWrapper;
import Utils.DragonDatabaseManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

/**
 * Class, that implements console command "update"
 */

@CommandInfo(name = "update", description = "update the value of the collection element, which ID is equal to the given one")
public class UpdateCommand implements ClientCommand {

    public void execute(List<String> tokens) {

    }

    @Override
    public String executeClient(CommandWrapper commandWrapper) {
        List<String> tokens = commandWrapper.getTokens();
        long givenID;

        try {
            if (tokens.size() != 1) {
                throw new TokenMismatchException(1);
            }

            givenID = Long.parseLong(tokens.get(0));
        } catch (TokenMismatchException | NumberFormatException exception) {
            return exception.getMessage();
        }

        CollectionManager cm = CollectionManager.getInstance();
        Vector<DragonX> collection = cm.getCollection();

        StringBuilder sb = new StringBuilder();

        DragonX newDragon = commandWrapper.getDragon();
        newDragon.setId(givenID);
        newDragon.setCreationDate(LocalDate.now());
        Optional<DragonX> toUpdate = collection.stream().filter(item -> item.getId() == givenID).findFirst();

        if (toUpdate.isEmpty()) {
            return "Couldn't find a dragon with provided ID.";
        }

        if (!toUpdate.get().getOwner().equals(commandWrapper.getUsername())) {
            sb.append("Cannot update the dragon with ID: ")
                    .append(toUpdate.get().getId())
                    .append(". YOU DON'T OWN THIS DRAGON.\nReal owner: ")
                    .append(Colors.YELLOW).append(toUpdate.get().getOwner()).append(Colors.RESET);
            return sb.toString();
        }

        sb.append("Dragon before the update:\n");
        sb.append(toUpdate.get().returnInfo());

        DragonDatabaseManager dm = new DragonDatabaseManager();
        int status = dm.updateDragon(newDragon);
        if (!(status > 0)) return "(Unexpected) Couldn't find a dragon with provided ID in the database.";

        collection.remove(toUpdate.get());
        collection.add(newDragon);
        sb.append("Dragon with ID ").append(givenID).append(" was successfully updated");

        return sb.toString();
    }
}
