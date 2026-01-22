package project.projecte.Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Supplier implements Serializable {
    private String name;
    private String contactInfo;
    private List<String> products;

    public Supplier(String name, String contactInfo) {
        this.name = name;
        this.contactInfo = contactInfo;
        this.products = new ArrayList<>();
    }

    public void addProduct(String product) {
        if (!products.contains(product)) {
            products.add(product);
        }
    }

    public List<String> getProducts() {
        return products;
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "name='" + name + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", products=" + products +
                '}';
    }
}
