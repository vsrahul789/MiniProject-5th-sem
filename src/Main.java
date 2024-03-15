import java.sql.*;
import java.util.Scanner;

//  CONNECTION IS DONE HERE AND CALLING CLASSES AND METHODS IN CLASSES IN HERE
public class Main {
    private static String url = "jdbc:mysql://localhost:3306/BankingSystem";
    private static String username = "root";
    private static String password = "password";
    public static void main(String[] args) {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
//          Your DataBase Drivers have been loaded
        }catch (ClassNotFoundException e){
            e.getException();
        }

        try{
//            Here you Connect to your database
            Connection connection = DriverManager.getConnection(url, username, password);
            Scanner scanner = new Scanner(System.in);

            User user = new User(connection , scanner);
            AccountManager accountManager = new AccountManager(connection , scanner);
            Accounts accounts = new Accounts(connection , scanner);

            String email;
            Long acc_no;

            while (true) {
                System.out.println("****** Welcome To The Bank *******");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");

                System.out.println("Enter Your Choice");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        user.register();
                        break;
                    case 2:
                        email = user.login();
                        if (email != null) {
                            System.out.println("User Logged in:");
                            if(!accounts.account_exists(email)) {
                                System.out.println("1. Create new Account");
                                System.out.println("2. Exit");

                                if(scanner.nextInt() == 1) {
                                    acc_no = accounts.open_account(email);
                                    System.out.println("Account Created Successfully");
                                    System.out.println("Your Account Number is: " + acc_no);
                                }else{
                                    break;
                                }
                            }
                            acc_no = accounts.getAccount_no(email);
                            int choice2 = 0;
                            while(choice2 != 5) {
<<<<<<< HEAD
                                System.out.println("1. Deposite Money");
                                System.out.println("2. Withdraw Money");
=======
                                System.out.println("1. Debit Money");
                                System.out.println("2. Credit Money");
>>>>>>> origin/main
                                System.out.println("3. Transfer Money");
                                System.out.println("4. Check Balance");
                                System.out.println("5. Log Out");
                                System.out.println("Enter your choice: ");
                                choice2 = scanner.nextInt();
                                switch (choice2) {
                                    case 1:
                                        accountManager.deposit(acc_no);
                                        break;
                                    case 2:
                                        accountManager.withdraw(acc_no);
                                        break;
                                    case 3:
                                        accountManager.transfer(acc_no);
                                        break;
                                    case 4:
                                        accountManager.checkBalance(acc_no);
                                        break;
                                    case 5:
                                        break;
                                }

                            }
                        }else{
                            System.out.println("Email or Password is Wrong");
                        }
                    case 3:
                        System.out.println("THANK YOU FOR USING BANKING SYSTEM!!!");
                        System.out.println("Exiting System!");
                        return;
                    default:
                        System.out.println("Enter Valid Choice");
                        break;
                }


            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}