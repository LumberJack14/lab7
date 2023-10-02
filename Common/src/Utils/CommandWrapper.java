package Utils;

import Collection.DragonX;

import java.io.Serializable;
import java.util.ArrayList;

public class CommandWrapper implements Serializable {
    private ArrayList<String> tokens;
    private DragonX dragon;
    private String username;
    private String password;

    public CommandWrapper(ArrayList<String> tokens, DragonX dragon, String username, String password) {
        this.tokens = tokens;
        this.dragon = dragon;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public void setTokens(ArrayList<String> tokens) {
        this.tokens = tokens;
    }

    public void setDragon(DragonX dragon) {
        this.dragon = dragon;
    }

    public DragonX getDragon() {
        return dragon;
    }
}
