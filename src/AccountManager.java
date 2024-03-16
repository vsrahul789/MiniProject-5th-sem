import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    Connection connection;
    Scanner scanner;
    private int generateTransaction_id() throws SQLException {
        String generateTransactionIDQuery = "SELECT transaction_id from transaction_details ORDER BY transaction_id DESC LIMIT 1";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(generateTransactionIDQuery);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return rs.getInt("transaction_id")+1;
            }else{
                return 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }
    public void deposit(Long accNo) {


        System.out.println("Enter the amount you want to deposite: ");
        double amount = scanner.nextDouble();
        String depositQuery = "UPDATE accounts SET balance = balance + ? WHERE acc_no = ?";

        try {
            System.out.println("Deposite 1");
            connection.setAutoCommit(false); // set auto commit to false to handle transactions manually

            PreparedStatement preparedStatement = connection.prepareStatement(depositQuery);

            preparedStatement.setDouble(1, amount);
            preparedStatement.setLong(2, accNo);
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("2");
            if (rowsAffected > 0) {
                connection.commit(); // commit the transaction
                System.out.println("Amount deposited successfully");
            }else{
                connection.rollback(); // rollback the transaction
                System.out.println("Failed to deposit amount");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void withdraw(Long accNo) {
        String withdrawQuery = "UPDATE accounts SET balance = balance - ? WHERE acc_no = ?";

        System.out.println("Enter the amount you want to withdraw: ");
        double amount = scanner.nextDouble();

        try {
//            if(checkBalance(accNo) - amount < 0) {
//                System.out.println("Insufficient balance");
//                return;
//            }
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(withdrawQuery);
            preparedStatement.setDouble(1, amount);
            preparedStatement.setLong(2, accNo);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Amount withdrawn successfully");
                connection.commit();
            }else{
                System.out.println("Failed to withdraw amount");
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void transfer(long sender_account_number) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Receiver Account Number: ");
        long receiver_account_number = scanner.nextLong();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        try{
            connection.setAutoCommit(false);
            if(sender_account_number!=0 && receiver_account_number!=0){
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE acc_no = ? AND sec_pin = ? ");
                preparedStatement.setLong(1, sender_account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    double current_balance = resultSet.getDouble("balance");
                    if (amount<=current_balance){

                        // Write debit and credit queries
                        String debit_query = "UPDATE accounts SET balance = balance - ? WHERE acc_no = ?";
                        String credit_query = "UPDATE accounts SET balance = balance + ? WHERE acc_no = ?";

                        // Debit and Credit prepared Statements
                        PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
                        PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);

                        // Set Values for debit and credit prepared statements
                        creditPreparedStatement.setDouble(1, amount);
                        creditPreparedStatement.setLong(2, receiver_account_number);
                        debitPreparedStatement.setDouble(1, amount);
                        debitPreparedStatement.setLong(2, sender_account_number);

                        String transaction_query = "INSERT INTO transaction_details (transaction_id,sender_id, receiver_id,transaction_time,amount) VALUES (?,?,?, ?, ?)";
                        PreparedStatement transactionPreparedStatement = connection.prepareStatement(transaction_query);
                        int transaction_id = generateTransaction_id();
                        transactionPreparedStatement.setInt(1, transaction_id);
                        transactionPreparedStatement.setLong(2, sender_account_number);
                        transactionPreparedStatement.setLong(3, receiver_account_number);
                        transactionPreparedStatement.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                        transactionPreparedStatement.setDouble(5, amount);

                        transactionPreparedStatement.executeUpdate();

                        int rowsAffected1 = debitPreparedStatement.executeUpdate();
                        int rowsAffected2 = creditPreparedStatement.executeUpdate();
                        if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                            System.out.println("Transaction Successful!");
                            System.out.println("Rs."+amount+" Transferred Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction Failed");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }else{
                        System.out.println("Insufficient Balance!");
                    }
                }else{
                    System.out.println("Invalid Security Pin!");
                }
            }else{
                System.out.println("Invalid account number");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public double checkBalance(Long accNo) {
        String checkBalanceQuery = "SELECT balance FROM accounts WHERE acc_no = ? AND sec_pin = ?";
        System.out.println("Enter your security pin: ");
        int sec_pin = scanner.nextInt();


        try {
            PreparedStatement preparedStatement = connection.prepareStatement(checkBalanceQuery);
            preparedStatement.setLong(1, accNo);
            preparedStatement.setInt(2, sec_pin);

            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                double balance = rs.getDouble("balance");
                System.out.println("Balance: " + balance);
            }else{
                System.out.println("Incorrect security pin ");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0; // return 0 if no balance found for the account number
    }
}
