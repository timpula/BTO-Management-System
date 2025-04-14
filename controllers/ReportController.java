package controllers;

import models.Report;
import models.Application;
import models.Applicant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportController {

    private static List<Application> applications = new ArrayList<>(); // Simulating a database of applications
    private static Applicant applicant; // Simulating a database of applicants
    // Generate applicant report
    public Report generateApplicantReport() {
        Report report = new Report();
        report.setReportId("RPT" + System.currentTimeMillis());
        report.setReportType("Applicant");
        report.setGenerationDate(new Date());
        report.setApplications(new ArrayList<>(applications)); // Add all applications to the report
        report.addStatistic("Total Applicants", applications.size());
        return report;
    }

    // Generate booking report
    public Report generateBookingReport() {
        Report report = new Report();
        report.setReportId("RPT" + System.currentTimeMillis());
        report.setReportType("Booking");
        report.setGenerationDate(new Date());

        int successfulBookings = 0;
        for (Application application : applications) {
            if ("Successful".equals(application.getStatus())) {
                report.addApplication(application);
                successfulBookings++;
            }
        }
        report.addStatistic("Total Successful Bookings", successfulBookings);
        return report;
    }

    // Filter report by marital status
    public Report filterReportByMaritalStatus(String maritalStatus) {
        Report report = new Report();
        report.setReportId("RPT" + System.currentTimeMillis());
        report.setReportType("Applicant Filtered by Marital Status");
        report.setGenerationDate(new Date());

        int count = 0;
        for (Application application : applications) {
            if (applicant.getMaritalStatus().equalsIgnoreCase(maritalStatus)) {
                report.addApplication(application);
                count++;
            }
        }
        report.addStatistic("Filtered by Marital Status (" + maritalStatus + ")", count);
        return report;
    }

    // Filter report by flat type
    public Report filterReportByFlatType(String flatType) {
        Report report = new Report();
        report.setReportId("RPT" + System.currentTimeMillis());
        report.setReportType("Applicant Filtered by Flat Type");
        report.setGenerationDate(new Date());

        int count = 0;
        for (Application application : applications) {
            if (flatType.equalsIgnoreCase(application.getFlatType())) {
                report.addApplication(application);
                count++;
            }
        }
        report.addStatistic("Filtered by Flat Type (" + flatType + ")", count);
        return report;
    }

    // Filter report by age range
    public Report filterReportByAge(int minAge, int maxAge) {
        Report report = new Report();
        report.setReportId("RPT" + System.currentTimeMillis());
        report.setReportType("Applicant Filtered by Age");
        report.setGenerationDate(new Date());

        int count = 0;
        for (Application application : applications) {
            int age = applicant.getAge();
            if (age >= minAge && age <= maxAge) {
                report.addApplication(application);
                count++;
            }
        }
        report.addStatistic("Filtered by Age (" + minAge + "-" + maxAge + ")", count);
        return report;
    }

    // Filter report by project
    public Report filterReportByProject(String projectId) {
        Report report = new Report();
        report.setReportId("RPT" + System.currentTimeMillis());
        report.setReportType("Applicant Filtered by Project");
        report.setGenerationDate(new Date());

        int count = 0;
        for (Application application : applications) {
            if (projectId.equals(application.getProjectId())) {
                report.addApplication(application);
                count++;
            }
        }
        report.addStatistic("Filtered by Project (" + projectId + ")", count);
        return report;
    }
}