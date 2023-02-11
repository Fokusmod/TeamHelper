package ru.geekbrains.WowVendorTeamHelper.exeptions;

public class SlackException extends RuntimeException{

    public SlackException(String message) {
        super(message);
    }
}
