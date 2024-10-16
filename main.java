import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

class Task {
    private String name;
    private int priority;
    private boolean isCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Task(String name, int priority, LocalDateTime deadline) {
        this.name = name;
        this.priority = priority;
        this.isCompleted = false;
        this.createdAt = LocalDateTime.now();
        this.deadline = deadline;
    }

    public Task(String name, int priority, boolean isCompleted, LocalDateTime createdAt, LocalDateTime deadline) {
        this.name = name;
        this.priority = priority;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
        this.deadline = deadline;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void markAsCompleted() {
        this.isCompleted = true;
    }

    @Override
    public String toString() {
        String status = isCompleted ? "✔" : "✘";
        return "Task: " + name +
                " - Priority: " + priority +
                " - Completed: " + (isCompleted ? "Yes" : "No") +
                " - Created At: " + createdAt.format(formatter) +
                " - Deadline: " + deadline.format(formatter) +
                " " + status;
    }

    public static Task fromReadableString(String line) {
        try {
            String[] parts = line.split(" - ");
            if (parts.length != 5) {
                throw new IllegalArgumentException("Invalid task format.");
            }

            String name = parts[0].substring(6);
            int priority = Integer.parseInt(parts[1].substring(10));
            boolean isCompleted = parts[2].substring(12).equals("Yes");
            LocalDateTime createdAt = LocalDateTime.parse(parts[3].substring(12), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime deadline = LocalDateTime.parse(parts[4].substring(10, parts[4].length() - 2), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            return new Task(name, priority, isCompleted, createdAt, deadline);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing task: " + e.getMessage());
        }
    }
}

class TaskManager {
    private ArrayList<Task> tasks;
    private static Scanner scanner = new Scanner(System.in);

    public TaskManager() {
        tasks = new ArrayList<>();
    }

    public void loadTasksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Task task = Task.fromReadableString(line);
                    tasks.add(task);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error parsing task from file: " + e.getMessage());
                }
            }
            System.out.println("Tasks loaded from file.");
        } catch (IOException e) {
            System.out.println("Error loading tasks from file: " + e.getMessage());
        }
    }

    public void addTask() {
        System.out.println("Enter the task name: ");
        String name = scanner.nextLine();
        System.out.println("Enter the priority (1-5): ");
        int priority = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter the deadline of the task (YYYY-MM-DD HH:mm): ");
        String deadlineIn = scanner.nextLine();
        LocalDateTime deadline = LocalDateTime.parse(deadlineIn, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        LocalDateTime createdAt = LocalDateTime.now();
        if (deadline.isBefore(createdAt)) {
            System.out.println("Deadline must be after the creation date.");
            return;
        }

        tasks.add(new Task(name, priority, deadline));
        System.out.println("Task ADDED!");
    }

    public void saveTasksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.txt"))) {
            for (Task task : tasks) {
                writer.write(task.toString());
                writer.newLine();
            }
            System.out.println("Tasks saved to file.");
        } catch (IOException e) {
            System.out.println("Error saving tasks to file: " + e.getMessage());
        }
    }

    public void setTaskPriority() {
        System.out.println("These are your tasks: ");
        displayTasks(true);
        System.out.println("Enter the task number to set the priority: ");
        int taskNumber = scanner.nextInt();
        if (taskNumber > 0 && taskNumber <= tasks.size()) {
            System.out.println("Enter the desired priority: ");
            int wantedPriority = scanner.nextInt();
            tasks.get(taskNumber - 1).setPriority(wantedPriority);
            System.out.println("Priority has been set.");
        } else {
            System.out.println("Invalid task number.");
        }
    }

    public void markAsCompleted() {
        System.out.println("These are your tasks: ");
        displayTasks(false);
        System.out.println("Enter the task number to mark as completed: ");
        int taskNumber = scanner.nextInt();
        if (taskNumber > 0 && taskNumber <= tasks.size()) {
            tasks.get(taskNumber - 1).markAsCompleted();
            System.out.println("Task has been marked as completed.");
        } else {
            System.out.println("Invalid task number.");
        }
    }

    void displayTasks(boolean showAll) {
        if (tasks.isEmpty()) {
            System.out.println("You have no tasks!");
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (showAll || !task.isCompleted()) {
                System.out.println((i + 1) + ". " + task);
            }
        }
        System.out.println();
    }

    public static void showMenu() {
        System.out.println("1. Add Task");
        System.out.println("2. Set Task Priority");
        System.out.println("3. Mark as Completed");
        System.out.println("4. Display All Tasks");
        System.out.println("5. Display Incomplete Tasks");
        System.out.println("6. Save Tasks to File");
        System.out.println("7. Exit");
    }
}

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        taskManager.loadTasksFromFile();

        int choice = 0;
        Scanner scanner = new Scanner(System.in);

        while (choice != 7) {
            TaskManager.showMenu();
            System.out.println("Enter Your Choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    taskManager.addTask();
                    break;
                case 2:
                    taskManager.setTaskPriority();
                    break;
                case 3:
                    taskManager.markAsCompleted();
                    break;
                case 4:
                    taskManager.displayTasks(true);
                    break;
                case 5:
                    taskManager.displayTasks(false);
                    break;
                case 6:
                    taskManager.saveTasksToFile();
                    break;
                case 7:
                    System.out.println("Exiting program...");
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
        scanner.close();
    }
}
