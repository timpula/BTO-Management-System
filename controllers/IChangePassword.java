package controllers;

public interface IChangePassword {
    boolean changePassword(String nric, String currentPassword, String newPassword);
}
