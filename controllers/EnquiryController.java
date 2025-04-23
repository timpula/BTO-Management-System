package controllers;

import models.Enquiry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EnquiryController {
    private static List<Enquiry> enquiries = new ArrayList<>(); // Simulating a database

    // Create an enquiry
    public boolean createEnquiry(String userNRIC, String projectId, String content) {
        if (userNRIC == null || projectId == null || content == null || content.isEmpty()) {
            return false; // Validation: Ensure all fields are provided
        }
    
        String enquiryId = "ENQ" + (enquiries.size() + 1); // Generate a unique enquiry ID
        Enquiry enquiry = new Enquiry(enquiryId, userNRIC, projectId, content, new Date());
        enquiries.add(enquiry);
        return true;
    }

    // Edit an enquiry
    public boolean editEnquiry(String enquiryId, String newContent) {
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getEnquiryId().equals(enquiryId)) {
                enquiry.setContent(newContent);
                return true;
            }
        }
        return false; // Enquiry not found
    }

    // Delete an enquiry
    public boolean deleteEnquiry(String enquiryId) {
        return enquiries.removeIf(enquiry -> enquiry.getEnquiryId().equals(enquiryId));
    }

    // View enquiries by user NRIC
    public List<Enquiry> viewEnquiries(String userNRIC) {
        List<Enquiry> userEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getUserNRIC().equals(userNRIC)) {
                userEnquiries.add(enquiry);
            }
        }
        return userEnquiries;
    }

    // View enquiries by project ID
    public List<Enquiry> viewEnquiriesByProject(String projectId) {
        List<Enquiry> projectEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getProjectId().equals(projectId)) {
                projectEnquiries.add(enquiry);
            }
        }
        return projectEnquiries;
    }

    // Reply to an enquiry
    public boolean replyToEnquiry(String enquiryId, String reply) {
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getEnquiryId().equals(enquiryId)) {
                enquiry.setReply(reply);
                enquiry.setReplyDate(new Date()); // Set the reply date
                return true;
            }
        }
        return false; // Enquiry not found
    }

    public List<Enquiry> findAllEnquiries() {
        return new ArrayList<>(enquiries);
      }
}