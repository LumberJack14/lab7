package LocalCommands;

import Annotations.CommandInfo;
import Collection.DragonX;
import Commands.Command;
import Utils.Colors;
import Utils.CommandWrapper;
import Utils.DragonDatabaseManager;
import Utils.UserDatabaseManager;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerCommandManager {
    private static HashMap<String, Command> serverCommandMap = new HashMap<>();
    private static HashMap<String, ClientCommand> clientCommandMap = new HashMap<>();

    public void registerServerCommands(Command[] commands) {
        for (Command command : commands) {
            serverCommandMap.put(command.getClass().getAnnotation(CommandInfo.class).name(), command);
        }
    }

    public void registerClientCommands(ClientCommand[] commands) {
        for (ClientCommand command : commands) {
            clientCommandMap.put(command.getClass().getAnnotation(CommandInfo.class).name(), command);
        }
    }

    public void executeServerCommand(ArrayList<String> tokens) {
        String command = tokens.get(0);

        if (!serverCommandMap.containsKey(command)) {
            System.out.printf("No such command found: %s\nUse 'help' to see available commands", command);
            return;
        }

        tokens.remove(0);
        serverCommandMap.get(command).execute(tokens);

    }

    public String executeClientCommand(CommandWrapper cm) {

        UserDatabaseManager udm = new UserDatabaseManager();
        boolean exists = udm.checkUser(cm.getUsername());
        if (!exists) {
            int status = udm.addUser(cm.getUsername(), cm.getPassword());
            if (!(status > 0))
                return "Couldn't add user '" + cm.getUsername() + "' to the database. Try logging in as a different user.";
        } else {
            boolean passCorrect = udm.verifyUser(cm.getUsername(), cm.getPassword());
            if (!passCorrect) return "Password is not correct for the username " + Colors.YELLOW + cm.getUsername()
                    + Colors.RESET + ". Cannot execute the command.";

            DragonX dragon = cm.getDragon();
            if (dragon != null) {
                dragon.setOwner(cm.getUsername());
                cm.setDragon(dragon);
            }
        }


        ArrayList<String> tokens = cm.getTokens();
        String command = tokens.get(0);

        if (!clientCommandMap.containsKey(command)) {
            return String.format("No such command found: %s\nUse 'help' to see available commands", command);
        }

        System.out.println(LocalTime.now() + " --> " + command);
        tokens.remove(0);
        return clientCommandMap.get(command).executeClient(cm);
    }
}
