package ru.geekbrains.WowVendorTeamHelper.exeptions;

public class TeamNotFoundException extends RuntimeException {

    public TeamNotFoundException(String message) {
        super(message);
    }
}
