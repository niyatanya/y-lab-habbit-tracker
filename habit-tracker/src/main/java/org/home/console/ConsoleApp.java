package org.home.console;

import lombok.NoArgsConstructor;
import org.home.model.Frequency;
import org.home.model.User;
import org.home.service.HabitService;
import org.home.service.StatisticsService;
import org.home.service.UserService;

import java.time.LocalDate;
import java.util.Scanner;

@NoArgsConstructor
public class ConsoleApp {
    private static final UserService userService = new UserService();
    private static final HabitService habitService = new HabitService();
    private static final StatisticsService statisticsService = new StatisticsService();
    private static User currentUser = null;
    private static final Scanner scanner = new Scanner(System.in);

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

        int choice = Integer.parseInt(scanner.nextLine());

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
        System.out.println("0. Logout");

        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 1 -> manageHabits();
            case 2 -> editProfile();
            case 3 -> deleteAccount();
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

        int choice = Integer.parseInt(scanner.nextLine());

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
        String name = scanner.nextLine();
        System.out.println("Enter your email:");
        String email = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        currentUser = userService.register(name, email, password);
        if (currentUser != null) {
            System.out.println("Registration successful.");
        } else {
            System.out.println("Account already exists. Please login using this email or register another account.");
        }
    }

    private static void login() {
        System.out.println("Enter your email:");
        String email = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        currentUser = userService.login(email, password);
        if (currentUser == null) {
            System.out.println("Invalid email or password. Try again.");
        }
    }

    private static void editProfile() {
        System.out.println("Enter new name:");
        String newName = scanner.nextLine();
        System.out.println("Enter new email:");
        String newEmail = scanner.nextLine();
        System.out.println("Enter new password:");
        String newPassword = scanner.nextLine();

        userService.editProfile(currentUser, newName, newEmail, newPassword);
    }

    private static void deleteAccount() {
        System.out.println("Are you sure you want to delete your account? (yes/no)");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            userService.deleteUser(currentUser);
            currentUser = null;
            System.out.println("Account deleted.");
        }
    }

    private static void createHabit() {
        System.out.println("Enter habit title:");
        String title = scanner.nextLine();
        System.out.println("Enter habit description:");
        String description = scanner.nextLine();
        System.out.println("Enter habit frequency (DAILY/WEEKLY):");
        Frequency frequency = Frequency.fromString(scanner.nextLine().toUpperCase());

        habitService.createHabit(currentUser, title, description, frequency);
        System.out.println("Habit \"" + title + "\" successfully created!");
    }

    private static void viewAllHabits() {
        var habits = currentUser.getHabits();
        if (habits.isEmpty()) {
            System.out.println("You have no habits yet.");
        } else {
            habits.forEach(habit -> System.out.println(habit.getTitle() + ": " + habit.getDescription()));
        }
    }

    private static void editHabit() {
        System.out.println("Enter the title of the habit you want to edit:");
        String oldTitle = scanner.nextLine();
        System.out.println("Enter new title:");
        String newTitle = scanner.nextLine();
        System.out.println("Enter new description:");
        String newDescription = scanner.nextLine();
        System.out.println("Enter new frequency (DAILY/WEEKLY):");
        Frequency newFrequency = Frequency.fromString(scanner.nextLine().toUpperCase());

        habitService.editHabit(currentUser, oldTitle, newTitle, newDescription, newFrequency);
    }

    private static void deleteHabit() {
        System.out.println("Enter the title of the habit you want to delete:");
        String title = scanner.nextLine();

        habitService.deleteHabit(currentUser, title);
    }

    private static void trackHabitCompletion() {
        System.out.println("Enter the title of the habit you want to track:");
        String title = scanner.nextLine();
        System.out.println("Enter the date (yyyy-MM-dd):");
        LocalDate date = LocalDate.parse(scanner.nextLine());
        System.out.println("Was the habit completed? (true/false):");
        boolean completed = Boolean.parseBoolean(scanner.nextLine());

        habitService.trackHabit(currentUser, title, date, completed);
    }

    private static void viewHabitStatistics() {
        System.out.println("Enter the title of the habit for the report:");
        String habitTitle = scanner.nextLine();
        System.out.println("Enter the report start date (yyyy-MM-dd):");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());
        System.out.println("Enter the report end date (yyyy-MM-dd):");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());

        statisticsService.generateProgressReport(currentUser, habitTitle, startDate, endDate);
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Logged out successfully.");
    }

    private static void exitApp() {
        System.out.println("Exiting the app. Goodbye!");
        System.exit(0);
    }
}
