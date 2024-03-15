import java.sql.*;
import java.util.Scanner;

public class Accounts {
    Connection connection;
    Scanner scanner;

    public Accounts(Connection connection ,Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }

    public boolean account_exists(String email) {
        String accountExistsQuery = "SELECT * FROM accounts WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(accountExistsQuery);
            preparedStatement.setString(1, email);

            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Long open_account(String email) throws SQLException {
        if(!account_exists(email)){
            String openAccountQuery = "INSERT INTO accounts (acc_no,full_name,email,balance,sec_pin) VALUES (?,?,?,?,?)";

            System.out.println("Enter your full name: ");
            String full_name = scanner.next();
            System.out.println("Enter your balance: ");
            double balance = scanner.nextDouble();
            System.out.println("Enter your security pin: ");
            int sec_pin = scanner.nextInt();
            try {
                long acc_no = generateAccountNumber();

                PreparedStatement preparedStatement = connection.prepareStatement(openAccountQuery);
                preparedStatement.setLong(1, acc_no);
                preparedStatement.setString(2,full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setInt(5, sec_pin);

                int rowsAffected = preparedStatement.executeUpdate();
                if(rowsAffected > 0){
                    return acc_no;
                }else {
                    throw new RuntimeException("Failed to create account");
                }


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return 0L;
    }

    private long generateAccountNumber() throws SQLException {
        String generateAccountNumberQuery = "SELECT acc_no from accounts ORDER BY acc_no DESC LIMIT 1";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(generateAccountNumberQuery);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return rs.getLong("acc_no")+1;
            }else{
                return 10000100;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Long getAccount_no(String email) {
        String getAccountNumberQuery = "SELECT acc_no FROM accounts WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getAccountNumberQuery);
            preparedStatement.setString(1, email);

            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return rs.getLong("acc_no");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0L;
    }


}
