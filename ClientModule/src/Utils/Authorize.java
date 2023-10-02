package Utils;

import java.io.BufferedReader;
import java.io.IOException;

public class Authorize {
    private static String username;
    private static String password;

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static void askAuthorization(BufferedReader bufferedReader) {
        while (true) {
            System.out.print("Enter your nickname (spaces don't count):\n>>>");
            try {
                String nickname = bufferedReader.readLine().trim();
                if (nickname.isBlank()) {
                    System.out.println("Incorrect format. Try again");
                    continue;
                }
                System.out.print("Enter your password (press 'Enter' if you don't need a password):\n>>>");
                String pass = bufferedReader.readLine().trim();

                password = pass;
                username = nickname;
                break;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
