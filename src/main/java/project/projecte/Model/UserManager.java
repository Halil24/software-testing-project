package project.projecte.Model;

import java.util.ArrayList;
import java.util.List;
import project.projecte.Model.User;
import project.projecte.DAO.FileManagement;

public class UserManager {
    private List<User> users;
    private static final String FILE_NAME = "data/users.txt";

    public UserManager() {
        this.users = new ArrayList<>();
        loadUsers(); // check!!!
    }

    // Add a new user to the list
    public void addUser(User user) {
        users.add(user);
        saveUsers(); // Save to file after adding the user
    }

    // Find a user by their username
    public User findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    // Remove a user from the list
    public void removeUser(User user) {
        users.remove(user);
        saveUsers(); // Save to file after removing the user
    }

    // Get all users in the list
    public List<User> getUsers() {
        return users;
    }

    // Update a user's details
    public void updateUser(User oldUser, User newUser) {
        int index = users.indexOf(oldUser);
        if (index != -1) {
            users.set(index, newUser);
            saveUsers(); // Save to file after updating the user
        }
    }

    // Save the list of users to the binary file
    public void saveUsers() {
        FileManagement.saveUsers(FILE_NAME, users);
    }

    // Load the list of users from the binary file
    public void loadUsers() {
        System.out.println("DEBUG UserManager.loadUsers: Loading from " + FILE_NAME);
        List<User> loadedUsers = FileManagement.loadUsers(FILE_NAME);
        if (loadedUsers != null) {
            users = loadedUsers;
            System.out.println("DEBUG UserManager.loadUsers: Loaded " + users.size() + " users");
            for (User user : users) {
                System.out.println("DEBUG UserManager.loadUsers: - " + user.getUsername() + " (" + user.getRole() + ")");
            }
        } else {
            System.out.println("No existing user data found.");
        }
    }
}
