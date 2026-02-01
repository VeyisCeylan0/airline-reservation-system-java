package ServicesAndManagers;

import Users.Customer;
import Users.Staff;
import Users.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static final Path FILE_PATH = Path.of("data", "users.txt");

    private static final List<User> users = new ArrayList<>();
    private static User activeUser = null;

    static {
        ensureFile();
        loadUsers();

        // Dosyada hiç user yoksa default admin ekle
        if (users.isEmpty()) {
            users.add(new Staff("admin", "1234"));
            saveUsers();
        }
    }

    private static void ensureFile() {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            if (!Files.exists(FILE_PATH)) {
                Files.createFile(FILE_PATH);
            }
        } catch (IOException e) {
            throw new RuntimeException("users.txt oluşturulamadı: " + e.getMessage(), e);
        }
    }

    public static boolean login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                activeUser = u;
                return true;
            }
        }
        return false;
    }

    public static void logout() {
        activeUser = null;
    }

    public static User getActiveUser() {
        return activeUser;
    }

    public static String getActiveUsername() {
        return activeUser == null ? "GUEST" : activeUser.getUsername();
    }

    public static boolean isStaff() {
        return activeUser != null && activeUser.getRoleType() == User.Role.STAFF;
    }

    public static boolean registerCustomer(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username)) return false;
        }
        users.add(new Customer(username, password));
        saveUsers();
        return true;
    }

    public static List<User> getAllUsers() {
        return new ArrayList<>(users);
    }


    private static void saveUsers() {
        try (BufferedWriter bw = Files.newBufferedWriter(FILE_PATH)) {
            for (User u : users) {
                bw.write(u.getUsername() + "|" + u.getPassword() + "|" + u.getRoleType());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("users.txt yazılamadı: " + e.getMessage(), e);
        }
    }

    private static void loadUsers() {
        users.clear();
        try (BufferedReader br = Files.newBufferedReader(FILE_PATH)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] p = line.split("\\|");
                if (p.length != 3) continue;

                String username = p[0];
                String password = p[1];
                User.Role role = User.Role.valueOf(p[2]);

                if (role == User.Role.STAFF) {
                    users.add(new Staff(username, password));
                } else {
                    users.add(new Customer(username, password));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("users.txt okunamadı: " + e.getMessage(), e);
        }
    }
}
