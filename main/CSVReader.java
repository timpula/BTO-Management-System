package main;

import controllers.UserController;
import controllers.ProjectController;
import models.User;
import models.Project;
import models.HDBOfficer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads data from CSV files to initialize the system.
 */
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
        }
        reader.close();
        System.out.println("Loaded users from: " + filePath);
    }

    /**
     * Loads projects from a CSV file and adds them to the ProjectController.
     * Also auto-assigns officers (by name) from the Officer column and decrements available slots.
     *
     * @param filePath The path to the CSV file.
     * @throws IOException If an error occurs while reading the file.
     */
    private static void loadProjects(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine(); // Skip the header
        ProjectController projectController = new ProjectController();
        UserController userController = new UserController();

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");

            if (data.length < 13) {
                System.err.println("Skipping malformed project line: " + line);
                continue;
            }

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
            String officerList = data[12]; // Officer names (comma-separated)

            // Add 2 flat types as separate projects
            String[] flatTypes = {type1, type2};
            int[] unitCounts = {unitsType1, unitsType2};

            for (int i = 0; i < flatTypes.length; i++) {
                Project project = new Project();
                project.setProjectName(projectName + " - " + flatTypes[i]);
                project.setNeighborhood(neighborhood);

                Map<String, Integer> flatTypeUnits = new HashMap<>();
                flatTypeUnits.put(flatTypes[i], unitCounts[i]);
                project.setFlatTypeUnits(flatTypeUnits);

                try {
                    project.setApplicationOpeningDate(excelDateFormat.parse(openingDate));
                    project.setApplicationClosingDate(excelDateFormat.parse(closingDate));
                } catch (ParseException e) {
                    System.err.println("Invalid date format for project: " + projectName);
                    continue;
                }

                project.setTotalOfficerSlots(officerSlots);
                project.setAvailableOfficerSlots(officerSlots);
                project.setCreatorNRIC(manager);

                boolean created = projectController.createProject(project);

                if (created) {
                    // Auto-assign officers based on names
                    for (String officerName : officerList.split(",")) {
                        officerName = officerName.trim();
                        if (!officerName.isEmpty()) {
                            User user = userController.viewUserDetails(officerName);
                            if (user instanceof HDBOfficer) {
                                HDBOfficer officer = (HDBOfficer) user;
                                officer.setAssignedProjectId(project.getProjectId());
                                officer.setRegistrationStatus("Approved");

                                // Decrease available officer slot
                                int currentSlots = project.getAvailableOfficerSlots();
                                project.setAvailableOfficerSlots(Math.max(0, currentSlots - 1));

                                System.out.println("Auto-assigned officer " + officerName + " to " + project.getProjectName());
                            } else {
                                System.err.println("Officer not found or wrong type: " + officerName);
                            }
                        }
                    }
                }
            }
        }

        reader.close();
        System.out.println("Loaded projects from: " + filePath);
    }
}