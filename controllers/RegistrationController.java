package controllers;

import models.Registration;

import java.util.ArrayList;
import java.util.List;

public class RegistrationController {

    private static List<Registration> registrations = new ArrayList<>(); // Simulating a database of registrations

    // Submit officer registration
    public boolean submitOfficerRegistration(Registration registration) {
        if (registration != null) {
            registrations.add(registration);
            return true;
        }
        return false; // Registration submission failed
    }

    // Validate registration eligibility
    public boolean validateRegistrationEligibility(String officerNRIC, String projectId) {
        for (Registration registration : registrations) {
            if (registration.getOfficerNRIC().equals(officerNRIC) && registration.getProjectId().equals(projectId)) {
                return false; // Officer already registered for this project
            }
        }
        return true; // Officer is eligible to register
    }

    // Get registrations by project
    public List<Registration> getRegistrationsByProject(String projectId) {
        List<Registration> projectRegistrations = new ArrayList<>();
        for (Registration registration : registrations) {
            if (registration.getProjectId().equals(projectId)) {
                projectRegistrations.add(registration);
            }
        }
        return projectRegistrations;
    }

    // Get registrations by status
    public List<Registration> getRegistrationsByStatus(String status) {
        List<Registration> statusRegistrations = new ArrayList<>();
        for (Registration registration : registrations) {
            if (registration.getStatus().equalsIgnoreCase(status)) {
                statusRegistrations.add(registration);
            }
        }
        return statusRegistrations;
    }

    // Update registration status
    public boolean updateRegistrationStatus(String registrationId, String newStatus) {
        for (Registration registration : registrations) {
            if (registration.getRegistrationId().equals(registrationId)) {
                registration.setStatus(newStatus);
                return true;
            }
        }
        return false; // Registration not found
    }

    // Check officer availability
    public boolean checkOfficerAvailability(String officerNRIC, String projectId) {
        for (Registration registration : registrations) {
            if (registration.getOfficerNRIC().equals(officerNRIC) && registration.getProjectId().equals(projectId)) {
                return false; // Officer is already registered for this project
            }
        }
        return true; // Officer is available
    }
}