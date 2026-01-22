package project.projecte.Model;

import java.io.Serializable;
import java.time.LocalDate;

public class Employee implements Serializable {
    private String name;
    private String username; // Links to User
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;
    private double salary;
    private String accessLevel;

    public Employee(String name, String username, LocalDate dateOfBirth, String phoneNumber, String email, double salary, String accessLevel) {
        this.name = name;
        this.username = username;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.salary = salary;
        this.accessLevel = accessLevel;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public double getSalary() {
        return salary;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }
}
