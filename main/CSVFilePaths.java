package main;

public class CSVFilePaths {

    private final String applicationFolderFilePath = "c:\\Users\\alien\\OneDrive\\Desktop\\BTO_System\\";

    private final String applicantListFilePath = applicationFolderFilePath + "ApplicantList.csv";
    private final String managerListFilePath = applicationFolderFilePath + "ManagerList.csv";
    private final String officerListFilePath = applicationFolderFilePath + "OfficerList.csv";
    private final String projectListFilePath = applicationFolderFilePath + "ProjectList.csv";

    public String getApplicantListFilePath() {
        return this.applicantListFilePath;
    }

    public String getManagerListFilePath() {
        return this.managerListFilePath;
    }

    public String getOfficerListFilePath() {
        return this.officerListFilePath;
    }

    public String getProjectListFilePath() {
        return this.projectListFilePath;
    }
}