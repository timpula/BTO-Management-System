package main;

import controllers.UserController;
import controllers.ProjectController;
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

/**
 * Reads data from CSV files to initialize the system.
 */
public class CSVReader {
    private static final SimpleDateFormat excelDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final CSVFilePaths filePaths = new CSVFilePaths();

    // ✅ Shared instances
    private static final UserController userController = new UserController();
    private static final ProjectController projectController = new ProjectController();

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
            System.out.println("DEBUG: Loaded user: " + name + " (" + userType + ")");
        }

        reader.close();
        System.out.println("Loaded users from: " + filePath);
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
            System.out.println("DEBUG (Raw CSV): officerList = [" + officerList + "]");

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

                if (created && officerList != null && !officerList.trim().isEmpty()) {
                    for (String officerName : officerList.split(",")) {
                        officerName = officerName.trim().replace("\"", "");
                
                        if (!officerName.isEmpty()) {
                            System.out.println("DEBUG: Looking for officer: " + officerName);
                
                            User user = userController.viewUserDetails(officerName);
                            if (user != null) {
                                System.out.println("? Matched user: " + user.getName() + " [" + user.getNric() + "]");
                
                                if (user instanceof HDBOfficer) {
                                    HDBOfficer officer = (HDBOfficer) user;
                                    officer.setAssignedProjectId(project.getProjectId());
                                    officer.setRegistrationStatus("Approved");
                
                                    int currentSlots = project.getAvailableOfficerSlots();
                                    project.setAvailableOfficerSlots(Math.max(0, currentSlots - 1));
                
                                    System.out.println("✅ Auto-assigned officer " + officer.getName() + " to " + project.getProjectName());
                                } else {
                                    System.err.println("❌ User matched but is not an HDBOfficer: " + user.getName());
                                }
                            } else {
                                System.out.println("❌ No user matched for: [" + officerName + "]");
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
