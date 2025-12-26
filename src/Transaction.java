public class Transaction {
    private String description;
    private double amount;
    private String category;

    public Transaction(String description, double amount, String category) {
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String toCSV() {
        return String.format("%s,%.2f,%s", description.replace(",", " "), amount, category);
    }

    public static Transaction fromCSV(String csvLine) throws IllegalArgumentException {
        String[] parts = csvLine.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid CSV format: " + csvLine);
        }

        try {
            String description = parts[0].trim();
            double amount = Double.parseDouble(parts[1].trim());
            String category = parts[2].trim();
            return new Transaction(description, amount, category);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in CSV: " + csvLine);
        }
    }

    @Override
    public String toString() {
        String type = (amount >= 0) ? "INCOME" : "EXPENSE";
        return String.format("| %-8s | %-15s | $%-10.2f | %s",
                             type, category, Math.abs(amount), description);
    }
}
