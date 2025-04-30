import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginRecords {
    public static HashMap<String, User> credentials = new HashMap<>();
    public static User activeUser;

    public static boolean recordExists(String usr) {
        return credentials.containsKey(usr);
    }

    public static void addRecord(String usr, String pwd) {
        if (!credentials.isEmpty()) {
            if (!recordExists(usr)) {
                credentials.put(usr, new User(usr, pwd));
            } else {
                System.out.println("User already exists");
            }
        }
        else {
            credentials.put(usr, new User(usr, pwd));
        }
    }

    public static boolean validateLogin(String usr, String pwd) {
        if (!credentials.isEmpty()) {
            if (recordExists(usr)) {
                if (credentials.get(usr).validatePassword(pwd)) {
                    activeUser = credentials.get(usr);
                    MainMenu.transmitUsername(usr);
                    System.out.println(activeUser.getUsername());
                    return true;
                }
                System.out.println("False password");
                return false;
            }
            System.out.println("No such user");
            return false;
        } else {
            System.out.println("Empty hashmap");
            return false;
        }
    }
    
    public static void setEmpType(String usr, String type) {
        credentials.get(usr).setEmpType(type);
    }

}
