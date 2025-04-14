package main;

import models.User;
import models.Project;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CSVWriter {
    private static final CSVFilePaths filePaths = new CSVFilePaths();

    /**
     * Saves users to a CSV file.
     *
     * @param filePath The path to the CSV file.
     * @param users    The list of users to save.
     * @throws IOException If an error occurs while writing to the file.
     */
    public static void saveUsers(String filePath, List<User> users) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write("Name,NRIC,Age,Marital Status,Password\n"); // Write header
        for (User user : users) {
            writer.write(user.getName() + "," + user.getNric() + ","
                    + user.getAge() + "," + user.getMaritalStatus() + ","
                    + user.getPassword() + "\n");
        }
        writer.close();
        System.out.println("Saved users to: " + filePath);
    }

    /**
     * Saves projects to a CSV file.
     *
     * @param filePath The path to the CSV file.
     * @param projects The list of projects to save.
     * @throws IOException If an error occurs while writing to the file.
     */
    public static void saveProjects(String filePath, List<Project> projects) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write("Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1,Type 2,Number of units for Type 2,Selling price for Type 2,Application opening date,Application closing date,Manager,Officer Slot\n"); // Write header
        for (Project project : projects) {
            Map<String, Integer> flatTypeUnits = project.getFlatTypeUnits();
            String[] flatTypes = flatTypeUnits.keySet().toArray(new String[0]);
            writer.write(project.getProjectName() + "," + project.getNeighborhood() + ","
                    + flatTypes[0] + "," + flatTypeUnits.get(flatTypes[0]) + ","
                    + flatTypes[1] + "," + flatTypeUnits.get(flatTypes[1]) + ","
                    + project.getApplicationOpeningDate() + "," + project.getApplicationClosingDate() + ","
                    + project.getTotalOfficerSlots() + "\n");
        }
        writer.close();
        System.out.println("Saved projects to: " + filePath);
    }
}