import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class BankingInformationSystem extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField amountField;
    private JTextArea statementArea;
    private Connection connection;
    private String currentUser;
    

    public BankingInformationSystem() {
        initializeUI();
        initializeDatabase();
    }

    private void initializeUI() {
        setTitle("Banking Information System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5,2));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel amountLabel = new JLabel("Amount:");
        JLabel emptyLabel = new JLabel("");

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        amountField = new JTextField();

        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton statementButton = new JButton("Account Statement");
        JButton logoutButton = new JButton("Logout");

        

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });

        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                depositAmount();
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                withdrawAmount();
            }
        });

        transferButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transferAmount();
            }
        });

        statementButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAccountStatement();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logoutUser();
            }
        });

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(registerButton);
        add(loginButton);
        add(amountLabel);
        add(amountField);
        add(depositButton);
        add(withdrawButton);
        add(transferButton);
        add(statementButton);
        add(emptyLabel);
        add(logoutButton);
        add(new JScrollPane(statementArea));

        statementArea = new JTextArea();
        statementArea.setEditable(false);

        setVisible(true);
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "user1", "*****");
            createTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS accounts (username VARCHAR(50) PRIMARY KEY, password VARCHAR(50), balance DOUBLE)";
            statement.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Statement statement = connection.createStatement();
            String query = "INSERT INTO accounts (username, password, balance) VALUES ('" + username + "', '" + password + "', 0.0)";
            statement.executeUpdate(query);
            JOptionPane.showMessageDialog(null, "Registration successful");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Registration failed");
            e.printStackTrace();
        }
    }

    private void loginUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM accounts WHERE username='" + username + "' AND password='" + password + "'";
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                currentUser = username;
                JOptionPane.showMessageDialog(null, "Login successful");
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void depositAmount() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "Please log in");
            return;
        }

        double amount = Double.parseDouble(amountField.getText());

        try {
            Statement statement = connection.createStatement();
            String query = "UPDATE accounts SET balance=balance+" + amount + " WHERE username='" + currentUser + "'";
            statement.executeUpdate(query);
            JOptionPane.showMessageDialog(null, "Deposit successful");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Deposit failed");
            e.printStackTrace();
        }
    }

    private void withdrawAmount() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "Please log in");
            return;
        }

        double amount = Double.parseDouble(amountField.getText());

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT balance FROM accounts WHERE username='" + currentUser + "'";
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                if (balance >= amount) {
                    query = "UPDATE accounts SET balance=balance-" + amount + " WHERE username='" + currentUser + "'";
                    statement.executeUpdate(query);
                    JOptionPane.showMessageDialog(null, "Withdrawal successful");
                } else {
                    JOptionPane.showMessageDialog(null, "Insufficient balance");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Withdrawal failed");
            e.printStackTrace();
        }
    }

    private void transferAmount() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "Please log in");
            return;
        }

        double amount = Double.parseDouble(amountField.getText());
        String recipient = JOptionPane.showInputDialog(null, "Enter recipient username:");

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT balance FROM accounts WHERE username='" + currentUser + "'";
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                if (balance >= amount) {
                    query = "UPDATE accounts SET balance=balance-" + amount + " WHERE username='" + currentUser + "'";
                    statement.executeUpdate(query);

                    query = "UPDATE accounts SET balance=balance+" + amount + " WHERE username='" + recipient + "'";
                    statement.executeUpdate(query);

                    JOptionPane.showMessageDialog(null, "Transfer successful");
                } else {
                    JOptionPane.showMessageDialog(null, "Insufficient balance");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Transfer failed");
            e.printStackTrace();
        }
    }

    private void showAccountStatement() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "Please log in");
            return;
        }

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT balance FROM accounts WHERE username='" + currentUser + "'";
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                statementArea.setText("Account statement for " + currentUser + "\n");
                statementArea.append("Balance: " + balance + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void logoutUser() {
        currentUser = null;
        JOptionPane.showMessageDialog(null, "Logged out");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BankingInformationSystem();
            }
        });
    }
}
