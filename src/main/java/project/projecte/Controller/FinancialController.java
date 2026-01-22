package project.projecte.Controller;

import javafx.beans.property.SimpleStringProperty;

public class FinancialController {
    private final SimpleStringProperty metric;
    private final SimpleStringProperty amount;

    public FinancialController(String metric, double amount) {
        this.metric = new SimpleStringProperty(metric);
        this.amount = new SimpleStringProperty(String.format("%.2f", amount));
    }

    public String getMetric() {
        return metric.get();
    }

    public void setMetric(String metric) {
        this.metric.set(metric);
    }

    public String getAmount() {
        return amount.get();
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }
}
