package controllers;

import models.Report;
import models.Application;
import models.Applicant;
import models.Project;
import models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles generating and filtering reports for applicant flat bookings.
 */
public class ReportController {
    private ApplicationController applicationController;
    private UserController userController;
    private ProjectController projectController;

    // Stores the last generated report to enable incremental filtering
    private Report currentReport;

    /**
     * Default constructor wiring default controllers.
     */
    public ReportController() {
        this(new ApplicationController(),
             new UserController(),
             new ProjectController());
    }

    /**
     * Constructor for dependency injection.
     */
    public ReportController(ApplicationController ac,
                            UserController uc,
                            ProjectController pc) {
        this.applicationController = ac;
        this.userController = uc;
        this.projectController = pc;
    }

    /**
     * Generates the base report of all approved/Booked applications.
     */
    public Report generateApplicantReport() {
        Report report = new Report();
        report.setReportId("RPT" + System.currentTimeMillis());
        report.setReportType("Applicant");
        report.setGenerationDate(new Date());

        // Fetch all applications with status "Approved" or "Booked"
        List<Application> allApps = new ArrayList<>();
        allApps.addAll(applicationController.getApplicationsByStatus("SUCCESSFUL"));
        allApps.addAll(applicationController.getApplicationsByStatus("BOOKED"));

        // Populate report
        for (Application app : allApps) {
            report.addApplication(app);
        }
        report.addStatistic("Total Records", report.getTotalRecords());

        currentReport = report;
        return report;
    }

    /**
     * Generates and filters the applicant report in one call.
     */
    public Report generateApplicantReport(String projectId,
                                          String flatType,
                                          String maritalStatus,
                                          int minAge,
                                          int maxAge) {
        // Start with full report
        Report report = generateApplicantReport();
        currentReport = report;

        // Apply filters in sequence
        if (projectId != null) {
            report = filterReportByProject(projectId);
        }
        if (flatType != null) {
            report = filterReportByFlatType(flatType);
        }
        if (maritalStatus != null) {
            report = filterReportByMaritalStatus(maritalStatus);
        }
        if (minAge > 0 || maxAge < Integer.MAX_VALUE) {
            report = filterReportByAge(minAge, maxAge);
        }
        return report;
    }

    /**
     * Filters the current report by marital status.
     */
    public Report filterReportByMaritalStatus(String status) {
        List<Application> filtered = currentReport.getApplications().stream()
            .filter(app -> {
                User u = userController.viewUserDetails(app.getApplicantNRIC());
                return u instanceof Applicant && ((Applicant)u).getMaritalStatus().equalsIgnoreCase(status);
            })
            .collect(Collectors.toList());
        currentReport.setApplications(filtered);
        currentReport.addStatistic("Filtered by Marital Status = " + status, filtered.size());
        return currentReport;
    }

    /**
     * Filters the current report by flat type.
     */
    public Report filterReportByFlatType(String flatType) {
        List<Application> filtered = currentReport.getApplications().stream()
            .filter(app -> flatType.equalsIgnoreCase(app.getFlatType()))
            .collect(Collectors.toList());
        currentReport.setApplications(filtered);
        currentReport.addStatistic("Filtered by Flat Type = " + flatType, filtered.size());
        return currentReport;
    }

    /**
     * Filters the current report by age range.
     */
    public Report filterReportByAge(int minAge, int maxAge) {
        List<Application> filtered = currentReport.getApplications().stream()
            .filter(app -> {
                User u = userController.viewUserDetails(app.getApplicantNRIC());
                return u instanceof Applicant && ((Applicant)u).getAge() >= minAge && ((Applicant)u).getAge() <= maxAge;
            })
            .collect(Collectors.toList());
        currentReport.setApplications(filtered);
        currentReport.addStatistic("Filtered by Age between " + minAge + " and " + maxAge, filtered.size());
        return currentReport;
    }

    /**
     * Filters the current report by project ID.
     */
    public Report filterReportByProject(String projectId) {
        List<Application> filtered = currentReport.getApplications().stream()
            .filter(app -> projectId.equals(app.getProjectId()))
            .collect(Collectors.toList());
        currentReport.setApplications(filtered);
        currentReport.addStatistic("Filtered by Project = " + projectId, filtered.size());
        return currentReport;
    }
}
