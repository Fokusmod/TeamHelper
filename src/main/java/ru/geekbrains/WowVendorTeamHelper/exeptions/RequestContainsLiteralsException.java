package ru.geekbrains.WowVendorTeamHelper.exeptions;

public class RequestContainsLiteralsException extends RuntimeException{
    public RequestContainsLiteralsException(String message){
        super(message);
    }
}
