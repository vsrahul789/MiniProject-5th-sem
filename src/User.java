import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    Connection connection;
    Scanner scanner;
    public User(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void register() {
        System.out.println("Full Name: ");
        String full_name = scanner.next();
        System.out.println("Email: ");
        String  email = scanner.next();
        System.out.println("Password: ");
        String password = scanner.next();

        if(user_exist(email)) {
            System.out.println("User Already Exists for this Email Address!!");
            return;
        }

        String registerQuery = "INSERT INTO user (full_name, eamil, password) VALUES (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(registerQuery);
            preparedStatement.setString(1, full_name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Rows affected: " + rowsAffected);
                System.out.println("You're Registered Successfully");
            }else{
                System.out.println("Registration failed");
            }

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean user_exist(String email) {
        String userExistQuery = "SELECT * FROM user WHERE eamil = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(userExistQuery);
            preparedStatement.setString(1, email);

            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String login() {
        System.out.println("Email: ");
        String email = scanner.next();
        System.out.println("Password: ");
        String password = scanner.next();

        String loginQuery = "SELECT * FROM user WHERE eamil = ? AND password = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(loginQuery);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return email;
            }else{
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
