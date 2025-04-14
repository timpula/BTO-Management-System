package views;

import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import models.User;

public class LoginView {

    public void redirectToUserDashboard(User user) {
        if (user == null) {
            System.out.println("Error: User is null. Cannot redirect.");
            return;
        }

        String userType = user.getUserType();
        System.out.println("User Type: " + userType);

        switch (userType) {
            case "Applicant":
                if (user instanceof Applicant) {
                    Applicant applicant = (Applicant) user;
                    System.out.println("Redirecting to Applicant Dashboard for " + applicant.getName());
                    ApplicantView applicantView = new ApplicantView();
                    applicantView.displayDashboard(applicant); // Pass the entire Applicant object
                } else {
                    System.out.println("Error: Applicant details not found.");
                }
                break;

            case "HDBOfficer":
                if (user instanceof HDBOfficer) {
                    HDBOfficer officer = (HDBOfficer) user;
                    System.out.println("Redirecting to HDB Officer Dashboard for " + officer.getName());
                    HDBOfficerView officerView = new HDBOfficerView();
                    officerView.displayDashboard(officer); // Pass the entire HDBOfficer object
                } else {
                    System.out.println("Error: HDB Officer details not found.");
                }
                break;

            case "HDBManager":
                if (user instanceof HDBManager) {
                    HDBManager manager = (HDBManager) user;
                    System.out.println("Redirecting to HDB Manager Dashboard for " + manager.getName());
                    HDBManagerView managerView = new HDBManagerView();
                    managerView.displayDashboard(manager); // Pass the entire HDBManager object
                } else {
                    System.out.println("Error: HDB Manager details not found.");
                }
                break;

            default:
                System.out.println("Error: Unknown user type. Cannot redirect.");
                break;
        }
    }
}