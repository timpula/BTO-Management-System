package controllers;

import models.*;
import java.util.*;
import java.util.stream.Collectors;

public class HDBOfficerController implements IChangePassword, IFilter {

    private RegistrationController registrationController = new RegistrationController();
    private ProjectController       projectController      = new ProjectController();

    // Your in‐memory “databases”
    private static List<HDBOfficer> officers    = new ArrayList<>();
    private static List<Application> applications = new ArrayList<>();
    private static List<Applicant>  applicants  = new ArrayList<>();
    private static List<Receipt>    receipts    = new ArrayList<>();

    /**
     * Officer registers for a project:
     *  - validates no overlapping, no active application
     *  - updates the HDBOfficer model
     *  - creates & submits a Registration record (Pending)
     */
    public boolean registerForProject(String officerNRIC, String projectId) {
        // 1) Prevent active application in same project
        for (Application app : applications) {
            if (app.getApplicantNRIC().equals(officerNRIC)
             && app.getProjectId().equals(projectId)
             && !app.getStatus().equalsIgnoreCase("Withdrawn")) {
                System.out.println("Cannot register: You have an active application for this project");
                return false;
            }
        }
        // 2) Prevent overlapping assignment periods
        Project targetProject = projectController.getProjectDetails(projectId);
        for (HDBOfficer off : officers) {
            if (off.getNric().equals(officerNRIC) && off.getAssignedProjectId() != null) {
                Project assigned = projectController.getProjectDetails(off.getAssignedProjectId());
                if (hasOverlappingPeriod(targetProject, assigned)) {
                    System.out.println("Cannot register: Already registered for another project in same period");
                    return false;
                }
            }
        }

        // 3) Update officer’s own record
        for (HDBOfficer off : officers) {
            if (off.getNric().equals(officerNRIC)) {
                off.setAssignedProjectId(projectId);
                off.setRegistrationStatus("Pending");
                break;
            }
        }

        // 4) Create & submit a Registration for manager approval
        Registration reg = new Registration();
        reg.setRegistrationId("REG" + System.currentTimeMillis());
        reg.setOfficerNRIC(officerNRIC);
        reg.setProjectId(projectId);
        reg.setRegistrationDate(new Date());
        reg.setStatus("Pending");

        return registrationController.submitOfficerRegistration(reg);
    }

    private boolean hasOverlappingPeriod(Project p1, Project p2) {
        return !(p1.getApplicationClosingDate().before(p2.getApplicationOpeningDate())
              || p2.getApplicationClosingDate().before(p1.getApplicationOpeningDate()));
    }

    /** Look up current registration status for this officer/project */
    public String viewRegistrationStatus(String officerNRIC, String projectId) {
        UserController uc = new UserController();
        User u = uc.viewUserDetails(officerNRIC);
        if (u instanceof HDBOfficer) {
            HDBOfficer off = (HDBOfficer) u;
            if (projectId.equals(off.getAssignedProjectId())) {
                return off.getRegistrationStatus();
            }
        }
        return "Not Registered";
    }

    /** Return the first assigned project (for single‐project officers) */
    public Project viewAssignedProject(String officerNRIC) {
        for (HDBOfficer off : officers) {
            if (off.getNric().equals(officerNRIC) && off.getAssignedProjectId() != null) {
                return projectController.getProjectDetails(off.getAssignedProjectId());
            }
        }
        return null;
    }

    /** Return list of all APPROVED projects assigned to this officer */
    public List<Project> getAllAssignedProjects(String officerNRIC) {
        Set<String> seen = new HashSet<>();
        List<Project> result = new ArrayList<>();
        for (HDBOfficer off : officers) {
            if (off.getNric().equals(officerNRIC)
             && "Approved".equalsIgnoreCase(off.getRegistrationStatus())
             && off.getAssignedProjectId() != null
             && seen.add(off.getAssignedProjectId())) {
                Project p = projectController.getProjectDetails(off.getAssignedProjectId());
                if (p != null) result.add(p);
            }
        }
        return result;
    }

    /** Look up an Applicant object by NRIC */
    public Applicant retrieveApplicantByNRIC(String applicantNRIC) {
        UserController uc = new UserController();
        User u = uc.viewUserDetails(applicantNRIC);
        return (u instanceof Applicant) ? (Applicant) u : null;
    }

    /** Adjust remaining flats in a project */
    public boolean updateFlatRemaining(String projectId, String flatType, int newCount) {
        Project p = projectController.getProjectDetails(projectId);
        if (p != null) {
            p.getFlatTypeUnits().put(flatType, newCount);
            return true;
        }
        return false;
    }

    /** Change status on an existing Application */
    public boolean updateApplicationStatus(String applicationId, String newStatus) {
        for (Application a : applications) {
            if (a.getApplicationId().equals(applicationId)) {
                a.setStatus(newStatus);
                return true;
            }
        }
        return false;
    }

    /** Generate and store a Receipt when booking succeeds */
    public Receipt generateBookingReceipt(String applicationId) {
        for (Application a : applications) {
            if (a.getApplicationId().equals(applicationId)
             && (a.getStatus().equalsIgnoreCase("SUCCESSFUL")
              || a.getStatus().equalsIgnoreCase("BOOKED"))) {

                Applicant ap = retrieveApplicantByNRIC(a.getApplicantNRIC());
                Project pr = projectController.getProjectDetails(a.getProjectId());
                if (ap == null || pr == null) return null;

                Receipt r = new Receipt();
                r.setReceiptId("REC" + System.currentTimeMillis());
                r.setApplicationId(applicationId);
                r.setApplicantNRIC(ap.getNric());
                r.setApplicantName(ap.getName());
                r.setApplicantAge(ap.getAge());
                r.setMaritalStatus(ap.getMaritalStatus());
                r.setProjectName(pr.getProjectName());
                r.setFlatType(a.getFlatType());
                r.setBookingDate(new Date());

                receipts.add(r);
                return r;
            }
        }
        return null;
    }

    /** Update an existing Application with chosen flatType */
    public boolean updateFlatSelection(String applicationId, String flatType) {
        for (Application a : applications) {
            if (a.getApplicationId().equals(applicationId)) {
                a.setFlatType(flatType);
                return true;
            }
        }
        return false;
    }

    /** Update an Applicant’s profile after booking */
    public boolean updateApplicantProfile(String applicantNRIC, String projectId, String flatType) {
        Applicant ap = retrieveApplicantByNRIC(applicantNRIC);
        if (ap == null) return false;
        ap.setBookedProjectId(projectId);
        ap.setBookedFlatType(flatType);
        return true;
    }

    /** Check if applicant has a given-status application */
    public boolean checkApplicantApplicationStatus(String applicantNRIC, String projectId, String status) {
        return applications.stream().anyMatch(a ->
            a.getApplicantNRIC().equals(applicantNRIC) &&
            a.getProjectId().equals(projectId) &&
            a.getStatus().equalsIgnoreCase(status)
        );
    }

    /** Find an existing Receipt by applicationId */
    public Receipt getReceiptByApplicationId(String applicationId) {
        return receipts.stream()
                       .filter(r -> r.getApplicationId().equals(applicationId))
                       .findFirst()
                       .orElse(null);
    }

    // ────────────────────────────────────────────────────────────────────────────

    /** IChangePassword */
    @Override
    public boolean changePassword(String nric, String currentPassword, String newPassword) {
        for (HDBOfficer off : officers) {
            if (off.getNric().equals(nric)
             && off.getPassword().equals(currentPassword)) {
                off.setPassword(newPassword);
                return true;
            }
        }
        return false;
    }

    /** IFilter: filter the global project list by this officer’s saved filters */
    @Override
    public List<Project> filterProjects(String nric) {
        HDBOfficer target = officers.stream()
            .filter(o -> o.getNric().equals(nric))
            .findFirst()
            .orElse(null);
        if (target == null) return Collections.emptyList();

        return projectController.viewAllProjects().stream()
            .filter(p -> (target.getFilterNeighborhood() == null
                       || p.getNeighborhood().equalsIgnoreCase(target.getFilterNeighborhood()))
                      && (target.getFilterFlatType() == null
                       || p.getFlatTypeUnits().containsKey(target.getFilterFlatType())))
            .collect(Collectors.toList());
    }

    /** Update this officer’s project‐listing filters */
    public void setFilters(String officerNRIC, String neighborhood, String flatType) {
        for (HDBOfficer off : officers) {
            if (off.getNric().equals(officerNRIC)) {
                off.setFilterNeighborhood(neighborhood.isEmpty() ? null : neighborhood);
                off.setFilterFlatType(flatType.isEmpty() ? null : flatType);
                break;
            }
        }
    }

    /** Add an already‐approved officer assignment (e.g. at startup) */
    public void addOfficer(HDBOfficer officer) {
        if (officer == null || officer.getAssignedProjectId() == null) return;
        boolean exists = officers.stream().anyMatch(o ->
            o.getNric().equals(officer.getNric())
         && o.getAssignedProjectId().equals(officer.getAssignedProjectId()));
        if (!exists) {
            HDBOfficer copy = new HDBOfficer(
                officer.getNric(),
                officer.getName(),
                officer.getPassword(),
                officer.getAge(),
                officer.getMaritalStatus()
            );
            copy.setAssignedProjectId(officer.getAssignedProjectId());
            copy.setRegistrationStatus("Approved");
            officers.add(copy);
        }
    }

    /** Debug helper */
    public void debugPrintOfficers() {
        System.out.println("\nDEBUG: Current officers in system:");
        for (HDBOfficer off : officers) {
            System.out.println("- " + off.getName()
                             + " [" + off.getNric() + "] "
                             + "Project: " + off.getAssignedProjectId()
                             + " Status: " + off.getRegistrationStatus());
        }
    }

    /** Test helpers */
    public void addDummyOfficer(HDBOfficer officer)   { officers.add(officer); }
    public void addDummyApplication(Application a)    { applications.add(a); }
    public void addDummyApplicant(Applicant applicant){ applicants.add(applicant); }

}
