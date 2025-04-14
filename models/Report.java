package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Report {
    private String reportId;
    private String reportType; // "Applicant", "Booking"
    private Date generationDate;
    private List<Application> applications;
    private Map<String, Integer> statistics; // For storing statistical data
    
    // Constructors
    public Report() {
        this.applications = new ArrayList<>();
        this.statistics = new HashMap<>();
    }
    
    public Report(String reportId, String reportType, Date generationDate) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.generationDate = generationDate;
        this.applications = new ArrayList<>();
        this.statistics = new HashMap<>();
    }
    
    // Methods
    public void addApplication(Application application) {
        applications.add(application);
    }
    
    public void addStatistic(String key, int value) {
        statistics.put(key, value);
    }
    public String getAppliedFilters() {
        // Example: If filters are stored in the statistics map
        if (statistics.isEmpty()) {
            return "No filters applied.";
        }
    
        StringBuilder filterSummary = new StringBuilder("Applied Filters:\n");
        for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
            filterSummary.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return filterSummary.toString();
    }
    
    // Getters and setters
    public String getReportId() {
        return reportId;
    }
    
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
    
    public String getReportType() {
        return reportType;
    }
    
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    
    public Date getGenerationDate() {
        return generationDate;
    }
    
    public void setGenerationDate(Date generationDate) {
        this.generationDate = generationDate;
    }
    
    public List<Application> getApplications() {
        return applications;
    }
    
    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
    
    public Map<String, Integer> getStatistics() {
        return statistics;
    }
    
    public void setStatistics(Map<String, Integer> statistics) {
        this.statistics = statistics;
    }
    public int getTotalRecords() {
        // Calculate total records based on applications
        return applications.size();  
    } 
}
