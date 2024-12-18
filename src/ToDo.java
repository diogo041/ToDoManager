import java.io.*;
import java.util.*;

public class ToDo {
    private static final String FILE_NAME = "data.txt";
    private static final List<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        loadTasks();
        System.out.println("Welcome to ToDo. Enter help for instructions.");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split(" ", 2);
            String command = parts[0].toLowerCase();
            String argument = parts.length > 1 ? parts[1] : "";

            switch (command) {
                case "add":
                    addTask(argument);
                    break;
                case "view":
                    viewTasks();
                    break;
                case "delete":
                    deleteTask(argument);
                    break;
                case "complete":
                    updateTaskStatus(argument, true);
                    break;
                case "incomplete":
                    updateTaskStatus(argument, false);
                    break;
                case "help":
                    printHelp();
                    break;
                case "exit":
                    saveTasks();
                    System.out.println("Exiting ToDo. Goodbye!");
                    return;
                default:
                    System.out.println("Unknown command. Type 'help' for instructions.");
            }
        }
    }

    private static void loadTasks() {
        File file = new File(FILE_NAME);
        try {
            if (file.createNewFile()) {
                System.out.println("Created new data file.");
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\\\|", 2);
                    if (parts.length == 2) {
                        boolean isComplete = parts[0].equals("complete");
                        String description = parts[1];
                        tasks.add(new Task(description, isComplete));
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
    }

    private static void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Task task : tasks) {
                writer.write((task.isComplete() ? "complete" : "incomplete") + "|" + task.getDescription());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    private static void addTask(String description) {
        if (description.isEmpty()) {
            System.out.println("Task description cannot be empty.");
            return;
        }
        tasks.add(new Task(description, false));
        saveTasks();
        System.out.println("Task \"" + description + "\" has been added to the list.");
    }

    private static void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("There are no tasks to display.");
            return;
        }
        System.out.println(String.format("%-5s %-15s %s", "#", "Status", "Description"));
        int index = 1;
        for (Task task : tasks) {
            String status = task.isComplete() ? "complete" : "incomplete";
            System.out.println(String.format("%-5d %-15s %s", index, status, task.getDescription()));
            index++;
        }
    }

    private static void deleteTask(String position) {
        try {
            int index = Integer.parseInt(position) - 1;
            if (index >= 0 && index < tasks.size()) {
                Task removedTask = tasks.remove(index);
                saveTasks();
                System.out.println("Task \"" + removedTask.getDescription() + "\" has been deleted.");
            } else {
                System.out.println("Invalid task position.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Task position must be a number.");
        }
    }

    private static void updateTaskStatus(String position, boolean isComplete) {
        try {
            int index = Integer.parseInt(position) - 1;
            if (index >= 0 && index < tasks.size()) {
                tasks.get(index).setComplete(isComplete);
                saveTasks();
                String status = isComplete ? "complete" : "incomplete";
                System.out.println("Task " + (index + 1) + " has been marked as " + status + ".");
            } else {
                System.out.println("Invalid task position.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Task position must be a number.");
        }
    }

    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("add <task_description>   - Add a new task");
        System.out.println("view                     - View all tasks");
        System.out.println("delete <task_position>   - Delete a task by position");
        System.out.println("complete <task_position> - Mark task as complete");
        System.out.println("incomplete <task_position> - Mark task as incomplete");
        System.out.println("help                     - Show instructions");
        System.out.println("exit                     - Exit the application");
    }

    private static class Task {
        private String description;
        private boolean isComplete;

        public Task(String description, boolean isComplete) {
            this.description = description;
            this.isComplete = isComplete;
        }

        public String getDescription() {
            return description;
        }

        public boolean isComplete() {
            return isComplete;
        }

        public void setComplete(boolean complete) {
            isComplete = complete;
        }
    }
}
