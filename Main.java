package dibella.erika;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {
    private static final int EXIT = 0;
    private static final int CREATE = 1;
    private static final int READ = 2;
    private static final int UPDATE = 3;
    private static final int DELETE = 4;
    private static final int LIST = 5;
    private static final int FOOD_ID_NOT_FOUND = -1;

    private static Connection openDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/foods", "erika", "4ErikaDiBella!");
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    private static void closeDatabase(Connection connection) {
        try {
            connection.close();
        } catch (Exception e) {
        }
    }

    private static boolean foodIsInDatabase(Connection connection, String foodName) {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM food WHERE name = ?");
            st.setString(1, foodName);
            ResultSet rs = st.executeQuery();
            return rs.next();
        } catch (Exception e) {
            return true;
        }
    }

    private static void addFoodToDatabase(Connection connection, String foodName) {
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO food (name) VALUES (?)");
            st.setString(1, foodName);
            int rowsInserted = st.executeUpdate();
            System.out.println("Rows Inserted: " + rowsInserted);
        } catch (Exception e) {
        }
    }

    private static void createFoodInDatabase(Connection connection, String foodName) {
        if (!foodIsInDatabase(connection, foodName)) {
            addFoodToDatabase(connection, foodName);
        }
    }

    private static int readFoodInDatabase(Connection connection, String foodName) {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM food WHERE name = ?");
            st.setString(1, foodName);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return FOOD_ID_NOT_FOUND;
            }
        } catch (Exception e) {
            return FOOD_ID_NOT_FOUND;
        }
    }

    private static void updateFoodInDatabase(Connection connection, String currentFoodName, String newFoodName) {
        try {
            PreparedStatement st = connection.prepareStatement("UPDATE food SET name = ? WHERE name = ?");
            st.setString(1, newFoodName);
            st.setString(2, currentFoodName);
            st.executeUpdate();
        } catch (Exception e) {
        }
    }

    private static void deleteFoodInDatabase(Connection connection, String foodName) {
        try {
            PreparedStatement st = connection.prepareStatement("DELETE FROM food WHERE name = ?");
            st.setString(1, foodName);
            st.executeUpdate();
        } catch (Exception e) {
        }
    }

    private static int displayMenuAndGetInput() {
        while (true) {
            System.out.println();
            System.out.println();
            System.out.println("1 to create a food, ");
            System.out.println("2 to read a food,");
            System.out.println("3 to update a food,");
            System.out.println("4 to delete a food,");
            System.out.println("5 list foods,");
            System.out.println("0 to exit");
            System.out.println();
            System.out.print("Enter your selection: ");
            Scanner s = new Scanner(System.in);
            int i = s.nextInt();
            if ((i >= EXIT) && (i <= LIST)) {
                return i;
            }
        }
    }

    private static void createFood(Connection connection) {
        Scanner s = new Scanner(System.in);
        System.out.println();
        System.out.print("Enter new food name: ");
        String foodName = s.next();
        createFoodInDatabase(connection, foodName);
    }

    private static void readFood(Connection connection) {
        Scanner s = new Scanner(System.in);
        System.out.println();
        System.out.print("Enter food name: ");
        String foodName = s.next();
        int foodId = readFoodInDatabase(connection, foodName);
        if (foodId == FOOD_ID_NOT_FOUND) {
            System.out.println("Food Not In Database!");
        } else {
            System.out.println("Food ID: " + foodId);
        }
    }

    private static void updateFood(Connection connection) {
        Scanner s = new Scanner(System.in);
        System.out.println();
        System.out.print("Enter current food name: ");
        String currentFoodName = s.next();
        System.out.print("Enter new food name: ");
        String newFoodName = s.next();
        updateFoodInDatabase(connection, currentFoodName, newFoodName);
    }

    private static void deleteFood(Connection connection) {
        Scanner s = new Scanner(System.in);
        System.out.println();
        System.out.print("Enter food name: ");
        String foodName = s.next();
        deleteFoodInDatabase(connection, foodName);
    }

    private static void listFoods(Connection connection) {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM food");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                System.out.println("[" + rs.getInt("id") + "] " + rs.getString("name"));
            }
        } catch (Exception e) {
        }
    }

    private static void crudSomeFood(Connection connection) {
        while (true) {
            int menuChoice = displayMenuAndGetInput();
            switch (menuChoice) {
                case CREATE:
                    createFood(connection);
                    break;
                case READ:
                    readFood(connection);
                    break;
                case UPDATE:
                    updateFood(connection);
                    break;
                case DELETE:
                    deleteFood(connection);
                    break;
                case LIST:
                    listFoods(connection);
                    break;
                case EXIT:
                    return;
            }
        }
    }

    public static void main(String[] args) {
        Connection connection = openDatabase();
        if (connection != null) {
            try {
                crudSomeFood(connection);
            } finally {
                closeDatabase(connection);
            }
        }
    }
}
