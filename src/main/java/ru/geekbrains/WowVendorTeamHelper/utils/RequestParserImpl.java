package ru.geekbrains.WowVendorTeamHelper.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestParserImpl implements RequestParser{
    @Override
    public List<String> parseString(String s) {
        String strings = s.trim();
        String[] client = strings.split("===");
        return new ArrayList<>(Arrays.asList(client));
    }
}
