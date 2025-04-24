package main;

import controllers.UserController;
import controllers.ProjectController;
import controllers.HDBOfficerController;
import models.User;
import models.Project;
import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads data from CSV files to initialize the system.
 */
public class CSVReader {
    private static final SimpleDateFormat excelDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final CSVFilePaths filePaths = new CSVFilePaths();

    // ✅ Shared instances
    private static final UserController userController = new UserController();
    private static final ProjectController projectController = new ProjectController();
    private static final HDBOfficerController officerController = new HDBOfficerController();

    /**
     * Initializes the system by reading data from CSV files.
     *
     * @return true if initialization is successful, false otherwise.
     */
    public static boolean initializeSystem() {
        try {
            loadUsers(filePaths.getApplicantListFilePath(), "Applicant");
            loadUsers(filePaths.getOfficerListFilePath(), "HDBOfficer");
            loadUsers(filePaths.getManagerListFilePath(), "HDBManager");

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
     */
    private static void loadUsers(String filePath, String userType) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine(); // Skip header

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            String name = data[0].trim();
            String nric = data[1].trim();
            int age = Integer.parseInt(data[2].trim());
            String maritalStatus = data[3].trim();
            String password = data[4].trim();

            // When loading officers, check if they already exist as applicants
            if (userType.equals("HDBOfficer")) {
                User existingUser = userController.getUserByNRIC(nric);
                if (existingUser != null && existingUser instanceof Applicant) {
                    // Create new officer instance but keep applicant role
                    HDBOfficer officer = new HDBOfficer(nric, name, password, age, maritalStatus);
                    userController.addUser(officer);
                    continue;
                }
            }

            // Normal user creation
            User user;
            switch (userType) {
                case "Applicant":
                    user = new Applicant(nric, name, password, age, maritalStatus);
                    break;
                case "HDBOfficer":
                    user = new HDBOfficer(nric, name, password, age, maritalStatus);
                    break;
                case "HDBManager":
                    user = new HDBManager(nric, name, password, age, maritalStatus);
                    break;
                default:
                    user = new User(nric, name, password, userType, age, maritalStatus);
            }

            userController.addUser(user);
        }
        reader.close();
    }

    /**
     * Loads projects from a CSV file and assigns officers.
     */
    private static void loadProjects(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine(); // Skip header

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
            // ✅ Collect officer names from index 12 onward
            StringBuilder officerListBuilder = new StringBuilder();
            for (int i = 12; i < data.length; i++) {
                String officer = data[i].replace("\"", "").trim();
                if (!officer.isEmpty()) {
                    officerListBuilder.append(officer).append(",");
                }
            }
            String officerList = officerListBuilder.toString().replaceAll(",$", "");

            String[] flatTypes = {type1, type2};
            int[] unitCounts = {unitsType1, unitsType2};

            // Store created projects for officer assignment
            List<Project> createdProjects = new ArrayList<>();

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
                //project.setCreatorNRIC(manager);
                User managerUser = userController.viewUserDetails(manager);
                if (managerUser != null && managerUser instanceof HDBManager) {
                    project.setCreatorNRIC(managerUser.getNric());
                } else {
                    System.err.println("Manager not found or is not an HDBManager: " + manager);
                    continue; // Skip this project if the manager is invalid
                }

                boolean created = projectController.createProject(project);
                if (created) {
                    createdProjects.add(project);
                }
            }

            // Assign officers to all flat types of the same project
            if (!createdProjects.isEmpty() && officerList != null && !officerList.trim().isEmpty()) {
                for (String officerName : officerList.split(",")) {
                    officerName = officerName.trim().replace("\"", "");

                    if (!officerName.isEmpty()) {


                        for (User user : userController.getAllUsers()) {
                            if (user.getName().equalsIgnoreCase(officerName)) {
                                User officerUser = userController.getUserByNRICAndRole(user.getNric(), "HDBOfficer");
                                if (officerUser != null && officerUser instanceof HDBOfficer) {
                                    // For each project, create a separate officer instance
                                    for (Project proj : createdProjects) {
                                        // Update project slots
                                        int currentSlots = proj.getAvailableOfficerSlots();
                                        proj.setAvailableOfficerSlots(Math.max(0, currentSlots - 1));
                                        
                                        // Create new officer instance for this project
                                        HDBOfficer projectOfficer = new HDBOfficer(
                                            ((HDBOfficer) officerUser).getNric(),
                                            ((HDBOfficer) officerUser).getName(),
                                            ((HDBOfficer) officerUser).getPassword(),
                                            ((HDBOfficer) officerUser).getAge(),
                                            ((HDBOfficer) officerUser).getMaritalStatus()
                                        );
                                        
                                        // Set project-specific details
                                        projectOfficer.setAssignedProjectId(proj.getProjectId());
                                        projectOfficer.setRegistrationStatus("Approved");
                                        
                                        // Add to officer controller
                                        officerController.addOfficer(projectOfficer);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // After assigning officers
            for (Project proj : createdProjects) {
                projectController.updateProject(proj);
            }
        }

        reader.close();
        System.out.println("Loaded projects from: " + filePath);
    }
}
