import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FinanceTracker {

    private final List<Transaction> transactions;
    private final String FILE_NAME = "transactions.csv";

    public FinanceTracker() {
        this.transactions = new ArrayList<>();
        loadTransactions();
    }

    private void loadTransactions() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("📝 No previous data found. Starting fresh.");
            return;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            int lineCount = 0;
            while (fileScanner.hasNextLine()) {
                lineCount++;
                String line = fileScanner.nextLine();
                if (line.trim().isEmpty()) continue;
                try {
                    transactions.add(Transaction.fromCSV(line));
                } catch (IllegalArgumentException e) {
                    System.err.println("❌ Skipped corrupted data on line " + lineCount + ": " + e.getMessage());
                }
            }
            System.out.println("✅ Loaded " + transactions.size() + " transactions from file.");
        } catch (FileNotFoundException e) {
            System.err.println("Error loading file: " + e.getMessage());
        }
    }

    private void saveTransactions() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            for (Transaction t : transactions) {
                writer.write(t.toCSV() + "\n");
            }
            System.out.println("\n✅ Data saved successfully to " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("❌ ERROR saving data: " + e.getMessage());
        }
    }

    public void addTransaction(Transaction t) {
        this.transactions.add(t);
        System.out.println("✅ Transaction added: " + t.getDescription());
    }

    public void displaySummary() {
        double income = 0;
        double expense = 0;

        for (Transaction t : transactions) {
            if (t.getAmount() > 0) {
                income += t.getAmount();
            } else {
                expense += t.getAmount();
            }
        }
        double balance = income + expense;

        System.out.println("\n--- Financial Summary ---");
        System.out.printf("Total Income:  $%.2f\n", income);
        System.out.printf("Total Expense: $%.2f\n", Math.abs(expense));
        System.out.println("-------------------------");
        System.out.printf("Net Balance:   $%.2f\n", balance);
        System.out.println("-------------------------");
    }

    public void displayAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded yet.");
            return;
        }
        System.out.println("\n--- Transaction History (Total: " + transactions.size() + ") ---");
        System.out.printf("%-8s | %-15s | %-12s | %s\n", "TYPE", "CATEGORY", "AMOUNT", "DESCRIPTION");
        System.out.println("-----------------------------------------------------------------");

        for (Transaction t : transactions) {
            System.out.println(t);
        }
        System.out.println("-----------------------------------------------------------------");
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n--- Budget Tracker Menu ---");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View Summary");
            System.out.println("4. View All Transactions");
            System.out.println("0. Exit and Save");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        addTransactionCLI(scanner, "Income");
                        break;
                    case 2:
                        addTransactionCLI(scanner, "Expense");
                        break;
                    case 3:
                        displaySummary();
                        break;
                    case 4:
                        displayAllTransactions();
                        break;
                    case 0:
                        saveTransactions();
                        System.out.println("Exiting application. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number from the menu.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        scanner.close();
    }

    private void addTransactionCLI(Scanner scanner, String type) {
        try {
            System.out.println("\n--- Add " + type + " ---");
            System.out.print("Enter description: ");
            String desc = scanner.nextLine();

            System.out.print("Enter category (e.g., Salary, Food, Rent): ");
            String category = scanner.nextLine();

            System.out.print("Enter amount: ");
            double amount = Double.parseDouble(scanner.nextLine());

            if (type.equalsIgnoreCase("Expense")) {
                amount = -Math.abs(amount);
            }

            addTransaction(new Transaction(desc, amount, category));

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid amount entered. Transaction cancelled.");
        }
    }

    public static void main(String[] args) {
        FinanceTracker tracker = new FinanceTracker();
        tracker.run();
    }
}
