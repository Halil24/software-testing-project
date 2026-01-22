package project.projecte.Model;

import java.io.Serializable;

// Abstract class for Users
public class User implements Serializable {
    private String username;
    private String password;
    private String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
    
    public void setUsername(String username) {
    	this.username = username;
    }
    
    public void setPassword(String pass) {
    	this.password = pass;
    }
    
    public void setRole(String role) {
    	this.role = role;
    }

}
