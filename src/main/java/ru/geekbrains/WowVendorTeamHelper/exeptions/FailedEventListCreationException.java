package ru.geekbrains.WowVendorTeamHelper.exeptions;

public class FailedEventListCreationException extends RuntimeException{
    public FailedEventListCreationException(String message) {
        super(message);
    }
}
