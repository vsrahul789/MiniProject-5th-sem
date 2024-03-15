import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    Connection connection;
    Scanner scanner;

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

    public void transfer(Long accNo) {
//         I'LL DO THIS LATER
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
