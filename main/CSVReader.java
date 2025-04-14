package main;

import controllers.UserController;
import controllers.ProjectController;
import models.User;
import models.Project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class CSVReader {
    private static final SimpleDateFormat excelDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final CSVFilePaths filePaths = new CSVFilePaths();

    /**
     * Initializes the system by reading data from CSV files.
     *
     * @return true if initialization is successful, false otherwise.
     */
    public static boolean initializeSystem() {
        try {
            // Load users
            loadUsers(filePaths.getApplicantListFilePath(), "Applicant");
            loadUsers(filePaths.getOfficerListFilePath(), "HDBOfficer");
            loadUsers(filePaths.getManagerListFilePath(), "HDBManager");

            // Load projects
            loadProjects(filePaths.getProjectListFilePath());

            System.out.println("System initialized successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Error initializing system: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads users from a CSV file and adds them to the UserController.
     *
     * @param filePath The path to the CSV file.
     * @param userType The type of user ("Applicant", "HDBOfficer", "HDBManager").
     * @throws IOException If an error occurs while reading the file.
     */
    private static void loadUsers(String filePath, String userType) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine(); // Skip the header
        UserController userController = new UserController();
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            String name = data[0];
            String nric = data[1];
            int age = Integer.parseInt(data[2]);
            String maritalStatus = data[3];
            String password = data[4];

            User user = new User(nric, name, password, userType, age, maritalStatus);
            userController.addUser(user);
            System.out.println("Loaded user: " + name + " (" + userType + ")");
            System.out.println("User NRIC: " + nric);
            System.out.println("User Age: " + age);
            System.out.println("User Marital Status: " + maritalStatus);
            System.out.println("User Password: " + password);
        }
        reader.close();
        System.out.println("Loaded users from: " + filePath);
    }

    /**
     * Loads projects from a CSV file and adds them to the ProjectController.
     *
     * @param filePath The path to the CSV file.
     * @throws IOException If an error occurs while reading the file.
     */
    private static void loadProjects(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine(); // Skip the header
        ProjectController projectController = new ProjectController();
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            String projectName = data[0];
            String neighborhood = data[1];
            String type1 = data[2];
            int unitsType1 = Integer.parseInt(data[3]);
            int priceType1 = Integer.parseInt(data[4]);
            String type2 = data[5];
            int unitsType2 = Integer.parseInt(data[6]);
            int priceType2 = Integer.parseInt(data[7]);
            String openingDate = data[8];
            String closingDate = data[9];
            String manager = data[10];
            int officerSlots = Integer.parseInt(data[11]);

            // Create and add projects for both types
            createAndAddProject(projectController, projectName, neighborhood, type1, unitsType1, openingDate, closingDate, officerSlots);
            createAndAddProject(projectController, projectName, neighborhood, type2, unitsType2, openingDate, closingDate, officerSlots);
        }
        reader.close();
        System.out.println("Loaded projects from: " + filePath);
    }

    /**
     * Helper method to create and add a project to the ProjectController.
     *
     * @param projectController The ProjectController instance.
     * @param projectName       The name of the project.
     * @param neighborhood      The neighborhood of the project.
     * @param flatType          The flat type (e.g., "2-Room").
     * @param units             The number of units for the flat type.
     * @param openingDate       The application opening date.
     * @param closingDate       The application closing date.
     * @param officerSlots      The number of officer slots for the project.
     */
    private static void createAndAddProject(ProjectController projectController, String projectName, String neighborhood,
                                            String flatType, int units, String openingDate, String closingDate, int officerSlots) {
        Project project = new Project();
        project.setProjectName(projectName + " - " + flatType); // Append flat type to distinguish
        project.setNeighborhood(neighborhood);

        // Use a mutable map for flat type units
        Map<String, Integer> flatTypeUnits = new HashMap<>();
        flatTypeUnits.put(flatType, units);
        project.setFlatTypeUnits(flatTypeUnits);

        try {
            project.setApplicationOpeningDate(excelDateFormat.parse(openingDate));
            project.setApplicationClosingDate(excelDateFormat.parse(closingDate));
        } catch (ParseException e) {
            System.err.println("Error parsing dates for project: " + projectName + " - " + flatType);
            return; // Skip this project if dates are invalid
        }

        project.setTotalOfficerSlots(officerSlots);

        // Add the project to the ProjectController
        projectController.createProject(project);
    }
}