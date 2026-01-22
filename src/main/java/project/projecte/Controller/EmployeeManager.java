package project.projecte.Controller;

import project.projecte.Model.Employee;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = "data/employees.dat";

    private final List<Employee> employees;

    public EmployeeManager() {
        this.employees = new ArrayList<>();
        loadEmployees(); // Ngarko t� dh�nat nga skedari n� inicializim
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        saveEmployees(); // Ruaj t� dh�nat sa her� shtohet nj� punonj�s
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        saveEmployees(); // Ruaj t� dh�nat sa her� fshihet nj� punonj�s
    }

    public Employee findEmployeeByName(String name) {
        return employees.stream()
                .filter(emp -> emp.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Employee findEmployeeByUsername(String username) {
        return employees.stream()
                .filter(emp -> emp.getUsername() != null && emp.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public double calculateTotalSalaries() {
        return employees.stream().mapToDouble(Employee::getSalary).sum();
    }

    /**
     * Ruajtja e t� dh�nave t� punonj�sve n� nj� skedar binar.
     */
    public void saveEmployees() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(employees);
        } catch (IOException e) {
            System.err.println("Failed to save employees: " + e.getMessage());
        }
    }

    /**
     * Leximi i t� dh�nave t� punonj�sve nga nj� skedar binar.
     */
    private void loadEmployees() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return; // Skedari nuk ekziston ende, kalo
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Employee> loadedEmployees = (List<Employee>) ois.readObject();
            employees.clear();
            employees.addAll(loadedEmployees);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load employees: " + e.getMessage());
        }
    }
}
