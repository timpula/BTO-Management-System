package controllers;

import models.Flat;
import models.Applicant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FlatBookingController {
    private static List<Flat> flats = new ArrayList<>(); // Simulating a database of flats

    // Book a flat
    public boolean bookFlat(String applicationId, String flatId) {
        for (Flat flat : flats) {
            if (flat.getFlatId().equals(flatId) && !flat.isBooked()) {
                flat.setBooked(true); // Mark the flat as booked
                System.out.println("Flat " + flatId + " has been successfully booked for application " + applicationId);
                return true;
            }
        }
        System.out.println("Flat " + flatId + " is already booked or does not exist.");
        return false; // Flat is already booked or not found
    }

    // Update flat inventory for a project and flat type
    public boolean updateFlatInventory(String projectId, String flatType, int newCount) {
        int currentCount = 0;
        for (Flat flat : flats) {
            if (flat.getProjectId().equals(projectId) && flat.getFlatType().equals(flatType)) {
                currentCount++;
            }
        }

        if (newCount > currentCount) {
            // Add new flats to meet the new count
            for (int i = 0; i < (newCount - currentCount); i++) {
                String newFlatId = "FLAT" + System.currentTimeMillis() + i;
                flats.add(new Flat(newFlatId, projectId, flatType));
            }
            System.out.println("Flat inventory updated. Added " + (newCount - currentCount) + " new flats.");
            return true;
        } else if (newCount < currentCount) {
            // Remove flats to meet the new count
            AtomicInteger flatsToRemove = new AtomicInteger(currentCount - newCount);
            flats.removeIf(flat -> flat.getProjectId().equals(projectId) && flat.getFlatType().equals(flatType) && !flat.isBooked() && flatsToRemove.getAndDecrement() > 0);
            System.out.println("Flat inventory updated. Removed " + (currentCount - newCount) + " flats.");
            return true;
        }
        System.out.println("Flat inventory remains unchanged.");
        return true; // No change needed
    }

    // Validate flat type eligibility for an applicant
    public boolean validateFlatTypeEligibility(Applicant applicant, String flatType) {
        int age = applicant.getAge();
        String maritalStatus = applicant.getMaritalStatus();

        if (flatType.equals("2-Room") && age >= 35 && maritalStatus.equals("Single")) {
            return true; // Eligible for 2-room flats
        } else if (!flatType.equals("2-Room") && age >= 21 && maritalStatus.equals("Married")) {
            return true; // Eligible for other flat types
        }
        return false; // Not eligible
    }

    // Update applicant's flat details
    public boolean updateApplicantFlatDetails(String applicantNRIC, String flatType, String projectId) {
        for (Flat flat : flats) {
            if (flat.getProjectId().equals(projectId) && flat.getFlatType().equals(flatType) && !flat.isBooked()) {
                flat.setBooked(true); // Mark the flat as booked
                System.out.println("Flat " + flat.getFlatId() + " has been assigned to applicant " + applicantNRIC);
                return true;
            }
        }
        System.out.println("No available flats of type " + flatType + " in project " + projectId);
        return false; // No available flats
    }

    // Get flat availability by type
    public int getFlatAvailabilityByType(String projectId, String flatType) {
        int availableCount = 0;
        for (Flat flat : flats) {
            if (flat.getProjectId().equals(projectId) && flat.getFlatType().equals(flatType) && !flat.isBooked()) {
                availableCount++;
            }
        }
        return availableCount;
    }
}