package org.home.console;

import lombok.NoArgsConstructor;
import org.home.model.Frequency;
import org.home.model.Habit;
import org.home.model.User;
import org.home.service.HabitRecordService;
import org.home.service.HabitService;
import org.home.service.StatisticsService;
import org.home.service.UserService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;

import static org.home.model.Role.ADMIN;

/**
 * The {@code ConsoleApp} class represents a console-based application for managing
 * users, habits, habit records, and statistics. It provides a main loop to interact
 * with the user, allowing them to log in and navigate through different features
 * like managing habits and tracking progress.
 */
@NoArgsConstructor
public class ConsoleApp {
    private static final UserService USER_SERVICE = new UserService();
    private static final HabitService HABIT_SERVICE = new HabitService();
    private static final HabitRecordService RECORD_SERVICE = new HabitRecordService();
    private static final StatisticsService STATISTICS_SERVICE = new StatisticsService();
    private static User currentUser = null;
    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Starts the application and show menus to the user.
     * <p>
     * If no user is logged in, the login menu is displayed. Once a user logs in,
     * the main menu is shown where the user can manage their habits and view statistics.
     */
    public static void run() {
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("Welcome to the Habit Tracker App!");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("0. Exit");

        int choice = Integer.parseInt(SCANNER.nextLine());

        switch (choice) {
            case 1 -> register();
            case 2 -> login();
            case 0 -> exitApp();
            default -> System.out.println("Invalid option, please try again.");
        }
    }

    private static void showMainMenu() {
        System.out.println("Main Menu:");
        System.out.println("1. Manage Habits");
        System.out.println("2. Edit Profile");
        System.out.println("3. Delete Account");
        System.out.println("4. Admin Menu");
        System.out.println("0. Logout");

        int choice = Integer.parseInt(SCANNER.nextLine());

        switch (choice) {
            case 1 -> manageHabits();
            case 2 -> editProfile();
            case 3 -> deleteAccount();
            case 4 -> showAdminMenu();
            case 0 -> logout();
            default -> System.out.println("Invalid option, please try again.");
        }
    }

    private static void manageHabits() {
        System.out.println("Habit Management:");
        System.out.println("1. Create Habit");
        System.out.println("2. View All Habits");
        System.out.println("3. Edit Habit");
        System.out.println("4. Delete Habit");
        System.out.println("5. Track Habit Completion");
        System.out.println("6. View Habit Statistics");
        System.out.println("0. Back to Main Menu");

        int choice = Integer.parseInt(SCANNER.nextLine());

        switch (choice) {
            case 1 -> createHabit();
            case 2 -> viewAllHabits();
            case 3 -> editHabit();
            case 4 -> deleteHabit();
            case 5 -> trackHabitCompletion();
            case 6 -> viewHabitStatistics();
            case 0 -> showMainMenu();
            default -> System.out.println("Invalid option, please try again.");
        }
    }

    private static void register() {
        System.out.println("Enter your name:");
        String name = SCANNER.nextLine();
        System.out.println("Enter your email:");
        String email = SCANNER.nextLine();
        System.out.println("Enter your password:");
        String password = SCANNER.nextLine();

        currentUser = USER_SERVICE.register(name, email, password);
        if (currentUser != null) {
            System.out.println("Registration successful.");
        } else {
            System.out.println("Account already exists. Please login using this email or register another account.");
        }
    }

    private static void login() {
        System.out.println("Enter your email:");
        String email = SCANNER.nextLine().trim();
        System.out.println("Enter your password:");
        String password = SCANNER.nextLine();

        currentUser = USER_SERVICE.login(email, password);
        if (currentUser == null) {
            System.out.println("Invalid email or password. Try again.");
        }
    }

    private static void editProfile() {
        System.out.println("Enter new name:");
        String newName = SCANNER.nextLine();
        System.out.println("Enter new email:");
        String newEmail = SCANNER.nextLine();
        System.out.println("Enter new password:");
        String newPassword = SCANNER.nextLine();

        USER_SERVICE.editProfile(currentUser, newName, newEmail, newPassword);
    }

    private static void deleteAccount() {
        System.out.println("Are you sure you want to delete your account? (yes/no)");
        String confirmation = SCANNER.nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            USER_SERVICE.deleteUser(currentUser);
            currentUser = null;
            System.out.println("Account deleted.");
        }
    }

    private static void createHabit() {
        System.out.println("Enter habit title:");
        String title = SCANNER.nextLine();
        System.out.println("Enter habit description:");
        String description = SCANNER.nextLine();
        System.out.println("Enter habit frequency (DAILY/WEEKLY):");
        Frequency frequency = Frequency.valueOf(SCANNER.nextLine().toUpperCase());

        HABIT_SERVICE.createHabit(currentUser, title, description, frequency);
        System.out.println("Habit \"" + title + "\" successfully created!");
    }

    private static void viewAllHabits() {
        Map<String, Habit> habits = HABIT_SERVICE.getAllHabits(currentUser);
        if (habits.isEmpty()) {
            System.out.println("You have no habits yet.");
        } else {
            habits.forEach((title, habit) -> {
                System.out.println(title  + ": " + habit.getDescription());
            });
        }
    }

    private static void editHabit() {
        System.out.println("Enter the title of the habit you want to edit:");
        String oldTitle = SCANNER.nextLine();
        System.out.println("Enter new title:");
        String newTitle = SCANNER.nextLine();
        System.out.println("Enter new description:");
        String newDescription = SCANNER.nextLine();
        System.out.println("Enter new frequency (DAILY/WEEKLY):");
        Frequency newFrequency = Frequency.valueOf((SCANNER.nextLine().toUpperCase()));

        HABIT_SERVICE.editHabit(currentUser, oldTitle, newTitle, newDescription, newFrequency);
    }

    private static void deleteHabit() {
        System.out.println("Enter the title of the habit you want to delete:");
        String title = SCANNER.nextLine();

        HABIT_SERVICE.deleteHabit(currentUser, title);
    }

    private static void trackHabitCompletion() {
        System.out.println("Enter the title of the habit you want to track:");
        String title = SCANNER.nextLine();
        System.out.println("Enter the date (yyyy-MM-dd):");
        LocalDate date = LocalDate.parse(SCANNER.nextLine());
        System.out.println("Was the habit completed? (true/false):");
        boolean completed = Boolean.parseBoolean(SCANNER.nextLine());

        Habit habit = HABIT_SERVICE.findByTitleAndUserId(currentUser, title);
        RECORD_SERVICE.createRecord(habit, date, completed);
    }

    private static void viewHabitStatistics() {
        System.out.println("Enter the title of the habit for the report:");
        String habitTitle = SCANNER.nextLine();
        System.out.println("Enter the report start date (yyyy-MM-dd):");
        LocalDate startDate = LocalDate.parse(SCANNER.nextLine());
        System.out.println("Enter the report end date (yyyy-MM-dd):");
        LocalDate endDate = LocalDate.parse(SCANNER.nextLine());

        System.out.println(STATISTICS_SERVICE.generateProgressReport(
                currentUser, habitTitle, startDate, endDate));
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Logged out successfully.");
    }

    private static void exitApp() {
        System.out.println("Exiting the app. Goodbye!");
        System.exit(0);
    }

    public static void showAdminMenu() {
        if (!currentUser.getRole().equals(ADMIN)) {
            System.out.println("Access denied. Only admins can access this menu.");
            return;
        }

        System.out.println("Admin Menu:");
        System.out.println("1. View all users");
        System.out.println("2. View all habits of a user");
        System.out.println("3. Block a user");
        System.out.println("4. Unblock a user");
        System.out.println("5. Delete a user");
        System.out.println("0. Back to Main Menu");

        int choice = Integer.parseInt(SCANNER.nextLine());

        switch (choice) {
            case 1 -> viewAllUsers();
            case 2 -> viewAllHabitsOfUser();
            case 3 -> blockUser();
            case 4 -> unblockUser();
            case 5 -> deleteUser();
            case 0 -> showMainMenu();
            default -> System.out.println("Invalid option, please try again.");
        }
    }

    private static void viewAllUsers() {
        System.out.println("List of all users:");
        var users = USER_SERVICE.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
           users.forEach((email, user) -> {
                System.out.println(email  + ": " + user.getName());
            });
        }
    }

    private static void viewAllHabitsOfUser() {
        System.out.println("Enter user email:");
        String email = SCANNER.nextLine();
        User user = USER_SERVICE.findUserByEmail(email);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        var habits = HABIT_SERVICE.getAllHabits(user);
        if (habits.isEmpty()) {
            System.out.println("No habits found for this user.");
        } else {
            habits.forEach((title, habit) -> {
                System.out.println(title + ": " + habit.getDescription());
            });
        }
    }

    private static void blockUser() {
        System.out.println("Enter user email to block:");
        String email = SCANNER.nextLine();
        User user = USER_SERVICE.findUserByEmail(email);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        System.out.println(USER_SERVICE.blockUser(user));
    }

    private static void unblockUser() {
        System.out.println("Enter user email to unblock:");
        String email = SCANNER.nextLine();
        User user = USER_SERVICE.findUserByEmail(email);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        System.out.println(USER_SERVICE.unblockUser(user));
    }

    private static void deleteUser() {
        System.out.println("Enter user email to delete:");
        String email = SCANNER.nextLine();
        User user = USER_SERVICE.findUserByEmail(email);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        USER_SERVICE.deleteUser(user);
        System.out.println("User " + user.getName() + " has been deleted.");
    }
}
