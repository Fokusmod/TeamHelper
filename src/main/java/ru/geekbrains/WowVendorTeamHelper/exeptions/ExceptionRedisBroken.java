package ru.geekbrains.WowVendorTeamHelper.exeptions;

public class ExceptionRedisBroken extends RuntimeException{

    public ExceptionRedisBroken(String message) {
        super(message);
    }
}