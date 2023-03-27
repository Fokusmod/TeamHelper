package ru.geekbrains.WowVendorTeamHelper.utils;

public class RequestParserFactory {
    public static RequestParser createRequestParser() {
        return new RequestParserImpl();
    }

}
