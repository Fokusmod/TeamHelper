package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.model.WowClient;
import ru.geekbrains.WowVendorTeamHelper.utils.emuns.WowClassEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OrderParser {

    /**
     * Метод stringParser на вход получает не обработанное кол-во информации из которой обрезает всё ненужное и возвращает список заказов.
     * Ключевые особенности:
     * - Информация о заказе не включает в себя всё что было написало до описания заказа путем проверки количества запятых в строчке.
     * Если в строке запятых больше 3 то информация о заказе будет начинаться именно с этой строки.
     * - Информация о заказе не включает в себя все что было написано после описания заказа путем проверки на символьный разделитель ===.
     * Если строка содержит разделитель - это будет означать конец описания заказа.
     * - Так как в одном описании заказа может присутствовать несколько заказов, было принято решение после нахождения строки,
     * с 3 и более запятыми, останавливать дальнейшую проверку и возвращать для дальнейшей обработки.
     * - В случае если в метод поступит более одного заказа, в котором только один из заказов имеет символьный разделитель ===,
     * а остальные либо не имеют его вовсе либо разделитель содержит неверное кол-во символов "=", метод всеравно корректно распарсит заказы.
     * - Заказы будут проверяться на формат. В случае если формат некорректный - будет произведён частичный
     * парсинг. В частности будет распарсирован код заказа, остальная информация о заказе будет добавлена в поле noParseInfo.
     * <p>
     * Пример строки на входе:
     * -------------------------------
     * "WoW Retail, Vault of the Incarnates Heroic Raid Boost, (eu-servers) (selfplayed) (standard-8of8-bosses)
     * (Restoration Druid), (Fri 17 Feb @ 20:00 CET), Vasya, emerald-dream, Alliance, Vasya#21758, &21233
     * https://worldofwarcraft.com/en-gb/character/eu/emeralddream/vasya
     * ==="
     * Не забудьте
     * ДОБИВКА
     * ""WoW Retail, Vault of the Incarnates Heroic Raid Boost , (eu-servers) (selfplayed) (standard 8 of 8 bosses)
     * Петя ревущий фьорд орда Petya777#2848 , &BY00133
     * ==="
     * -------------------------------
     * На выходе получится:
     * -------------------------------
     * WowClient(orderCode=&21233, battleTag=Vasya#21758, fraction=Alliance, realm=emerald-dream, nickname=Vasya,
     * orderDateTime=Fri 17 Feb @ 20:00 CET, characterClass=Restoration Druid, boostMode=standard-8of8-bosses, playingType=selfplayed, region=eu-servers,
     * service=Vault of the Incarnates Heroic Raid Boost, game=WoW Retail,
     * specificBosses=null, noParseInfo=null, OrderStatus=null, comments=null)
     * <p>
     * WowClient(orderCode=&BY0233, battleTag=null, fraction=null, realm=null, nickname=null, orderDateTime=null,
     * characterClass=null, boostMode=null, playingType=null, region=null, service=null, game=null, specificBosses=null,
     * noParseInfo=WoW Retail, Vault of the Incarnates Heroic Raid Boost , (eu-servers) (selfplayed)
     * (standard 8 of 8 bosses) Петя ревущий фьорд орда Petya777#2848 , &BY0233, OrderStatus=null, comments=null)
     * -------------------------------
     */

    private final static String BATTLE_TAG = "#";
    private final static String DELIMITER = "===";
    private final static String ORDER_CODE_SYMBOL = "&";
    private final static String PROTOCOL = "https";
    private final static String ORDER_CODE_FP_PATTERN = "&BY";
    private final static String DATE_TIME_DELIMITER = "@";
    private final static int CORRECT_FORMAT_COMMA = 7;
    private final static int MIN_COUNT_COMMA = 3;
    private final static int CLIENT_INFO_STEP = 5;
    private final static int GAME_AND_SERVICE_INFO = 1;
    private final static int ORDER_INFO = 2;
    private final static int MODE_SETTINGS = 2;

    public List<WowClient> stringParser(String message) {
        List<String> clientStringList = new ArrayList<>();
        String[] strings = message.replace("*", "").replace("&amp;", ORDER_CODE_SYMBOL).replace("\"", "").split("\n");

        StringBuilder client = new StringBuilder();
        for (String s : strings) {
            if (s.length() == 0) {
                continue;
            }
            client.append(s.replace("<", "").replace(">", ""));
            if (s.contains(DELIMITER)) {
                String correctlyStrings = checkUnnecessaryInfo(client.toString());
                clientStringList.add(correctlyStrings);
                client.setLength(0);
            } else {
                client.append("\n");
            }
        }
        client.setLength(0);
        return clientParser(clientStringList);
    }

    private String checkUnnecessaryInfo(String strings) {
        String[] parseStrings = strings.split("\n");
        int startPosition = 0;
        for (int i = 0; i < parseStrings.length; i++) {
            String[] checkCountComma = parseStrings[i].split(",");
            if (checkCountComma.length >= MIN_COUNT_COMMA) {
                startPosition = i;
                break;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = startPosition; i < parseStrings.length; i++) {
            if (i == parseStrings.length - 1) {
                stringBuilder.append(parseStrings[i]);
            } else {
                stringBuilder.append(parseStrings[i]).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private List<WowClient> clientParser(List<String> strings) {
        List<WowClient> wowClients = new ArrayList<>();
        for (String order : strings) {
            String[] string = order.split("\n");
            wowClients.addAll(parseRows(string));
        }
        return wowClients;
    }

    private List<WowClient> parseRows(String[] string) {
        List<WowClient> list = new ArrayList<>();
        WowClient wowClient;
        for (int i = 0; i < string.length; i++) {
            String[] fields = string[i].split(",");
            arrayToTrim(fields);
            if (string[i].contains(ORDER_CODE_FP_PATTERN) && fields.length < CORRECT_FORMAT_COMMA) {
                wowClient = noParseClient(string[i]);
                parseArmoryLink(string, wowClient);
                checkBundle(string[i], wowClient);
                checkTypeAndModeOrder(wowClient, string[i]);
                list.add(wowClient);
            } else if (string[i].contains(ORDER_CODE_SYMBOL) && fields.length > CORRECT_FORMAT_COMMA) {
                wowClient = new WowClient();
                try {
                    parseGameAndServiceInfo(fields, wowClient);
                    parseClientInfo(fields, wowClient);
                    parseOrderInfo(fields, wowClient);
                    parseArmoryLink(string, wowClient);
                    checkBundle(string[i], wowClient);
                    parseOriginInfo(string[i], wowClient);
                    checkTypeAndModeOrder(wowClient, string[i]);
                    list.add(wowClient);
                } catch (ArrayIndexOutOfBoundsException e) {
                    wowClient = noParseClient(string[i]);
                    parseArmoryLink(string, wowClient);
                    checkBundle(string[i], wowClient);
                    checkTypeAndModeOrder(wowClient, string[i]);
                    list.add(wowClient);
                }
            } else if (!string[i].contains(DELIMITER) && !string[i].contains(PROTOCOL)
                    && !string[i].contains(ORDER_CODE_FP_PATTERN) && !string[i].contains(ORDER_CODE_SYMBOL)) {
                for (WowClient client : list) {
                    client.setOrderComments("(Комментарий из слак канала: " + string[i] + ")");
                }
            } else if (!string[i].contains(DELIMITER) && !string[i].contains(PROTOCOL)) {
                wowClient = noParseClient(string[i]);
                parseArmoryLink(string, wowClient);
                checkBundle(string[i], wowClient);
                checkTypeAndModeOrder(wowClient, string[i]);
                list.add(wowClient);
            }
        }
        return list;
    }

    private void checkTypeAndModeOrder(WowClient wowClient, String info) {
        setMode(wowClient, info);
        setOrderType(wowClient, info);
        checkOrderCount(wowClient, info);
    }

    private void setMode(WowClient wowClient, String order) {
        String[] orderArray = order.split(",");
        String mode = null;
        for (int i = 0; i < orderArray.length; i++) {
            String orderInfo = orderArray[i].toLowerCase().replace(" ", "");
            if (orderInfo.contains("bundle")) {
                break;
            }
            if (orderInfo.contains("normal") | orderInfo.contains("normalraid")) {
                mode = "NM";
                break;
            } else if (orderInfo.contains("heroic") || orderInfo.contains("heroicraid") || orderInfo.contains("heroisch")) {
                mode = "HC";
                break;
            } else if (orderInfo.contains("mythic") || orderInfo.contains("mythicraid") || orderInfo.contains("mythisch")) {
                mode = "MYTH";
                break;
            }
        }
        wowClient.setMode(mode);
    }

    private void setOrderType(WowClient wowClient, String order) {
        String[] orderArray = order.split(",");
        String info = null;

        for (int i = 0; i < orderArray.length; i++) {
            String orderInfo = orderArray[i].toLowerCase().replace(" ", "");

            if (orderInfo.contains("advanced-pl") || orderInfo.contains("advanced")) {
                info = "Advanced";
                break;
            } else if (orderInfo.contains("premium-pl") || orderInfo.contains("premium")) {
                info = "Premium";
                break;
            } else if (orderInfo.contains("deluxe")) {
                info = "Deluxe";
                break;
            } else if (orderInfo.contains("last") || orderInfo.contains("lastboss")) {
                info = "Last";
                break;
            } else if (orderInfo.contains("single")) {
                info = "Single";
                break;
            } else if (orderInfo.contains("tier")) {
                info = "Tier-token";
                break;
            } else if (orderInfo.contains("full-gear") || orderInfo.contains("fullgear")) {
                info = "Full-gear";
                break;
            } else if (orderInfo.contains("glory")) {
                info = "Glory";
                break;
            } else if (orderInfo.contains("bundle") || orderInfo.contains("%")) {
                info = "Bundle";
                break;
            } else if (orderInfo.contains("weekly")) {
                info = "Weekly";
                break;
            } else if (orderInfo.contains("pvepackage")) {
                info = "Premium-package";
                break;
            } else if (orderInfo.contains("standard") || orderInfo.contains("bosses)")) {
                info = checkCountBosses(order);
                break;
            }
        }
        wowClient.setOrderType(info);
    }

    private String checkCountBosses(String order) {
        String[] orderInfo = order.split(",");
        String standardInfo = null;
        for (String s : orderInfo) {
            if (s.contains("standard") || s.contains("bosses)")) {
                standardInfo = s;
                break;
            }
        }
        if (standardInfo != null) {
            String[] correctString = standardInfo.split("[()]");
            for (int i = 0; i < correctString.length; i++) {
                if (correctString[i].contains("standard") || correctString[i].contains("bosses")) {
                    String regex = "\\d+";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(correctString[i]);
                    StringBuilder stringBuilder = new StringBuilder();
                    while (matcher.find()) {
                        int num = Integer.parseInt(matcher.group());
                        stringBuilder.append(num).append("/");
                    }
                    if (stringBuilder.length() != 0) {
                        return stringBuilder.toString().trim().substring(0, stringBuilder.length() - 1);
                    }
                }
            }
        }
        return null;
    }

    private void checkOrderCount(WowClient wowClient, String info) {
        Pattern pattern = Pattern.compile("x\\d+");
        String[] orderArray = info.split(",");
        for (String order : orderArray) {
            Matcher matcher = pattern.matcher(order);
            if (matcher.find()) {
                String value = matcher.group();
                System.out.println("Value " + value + " found");
                wowClient.setOrderCount(value);
            }
        }
    }


    private void parseOriginInfo(String strings, WowClient wowClient) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(strings).append("\n");
        if (wowClient.getArmoryLink() != null) {
            stringBuilder.append(wowClient.getArmoryLink()).append("\n");
        }
        stringBuilder.append(DELIMITER);
        wowClient.setOriginInfo(stringBuilder.toString());
    }

    private void checkBundle(String string, WowClient wowClient) {
        String bundle = "bundle";
        String percentage = "%";
        String str = string.toLowerCase();
        StringBuilder match = new StringBuilder();

        Pattern pattern = Pattern.compile("\\([^()]*?\\b\\w*?\\b\\s*\\([^()]*?\\d+%[^()]*?\\)[^()]*?\\)");
        Matcher matcher = pattern.matcher(string);

        while (matcher.find()) {
            match.append(matcher.group()).append(" ");

        }
        wowClient.setDiscountInfo(match.toString().trim());
        if (str.contains(bundle) || str.contains(percentage)) {
            wowClient.setBundle("true");
            return;
        }
        wowClient.setBundle("false");
    }

    private WowClient noParseClient(String string) {
        WowClient wowClient = null;
        if (!string.contains(DELIMITER) && !string.contains(PROTOCOL) && string.contains(",")) {
            String[] fields = string.split(",");
            wowClient = new WowClient();
            wowClient.setNoParseInfo(string);
            wowClient.setOrderCode(correctingOrderCode(fields));
            setBattleTag(wowClient, fields);
        }
        return wowClient;
    }

    private void setBattleTag(WowClient wowClient, String[] info) {
        int startPosition = 0;
        String battleTag = null;
        for (int i = 0; i < info.length; i++) {
            if (info[i].contains(BATTLE_TAG)) {
                startPosition = i;
                break;
            }
        }
        if (startPosition != 0) {
            String[] words = info[startPosition].split(" ");
            for (int i = 0; i < words.length; i++) {
                if (words[i].contains(BATTLE_TAG)) {
                    battleTag = words[i];
                    break;
                }
            }
        }
        if (battleTag != null) {
            StringBuilder stringBuilder = new StringBuilder();
            char[] battleTagChars = battleTag.toCharArray();
            for (int i = battleTagChars.length - 1; i >= 0; i--) {
                if (battleTagChars[i] != ' ') {
                    stringBuilder.insert(0, battleTagChars[i]);
                } else {
                    break;
                }
            }
            wowClient.setBattleTag(stringBuilder.toString());
        }

    }

    private String correctingOrderCode(String[] fields) {
        String orderCode = fields[fields.length - 1];
        char[] orderChars = orderCode.toCharArray();
        StringBuilder correctString = new StringBuilder();
        int startPosition = 0;

        for (int i = 0; i < orderChars.length; i++) {
            if (orderChars[i] == '&') {
                startPosition = i;
            }
        }

        for (int i = startPosition; i < orderChars.length; i++) {
            correctString.append(orderChars[i]);
        }
        return correctString.toString();
    }

    private void parseArmoryLink(String[] string, WowClient wowClient) {
        for (String s : string) {
            if (s.contains(PROTOCOL)) {
                wowClient.setArmoryLink(s.trim());
            }
        }

    }

    private String[] arrayToTrim(String[] fields) {
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }
        List<String> checked = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].length() != 0) {
                checked.add(fields[i]);
            }
        }
        return checked.toArray(String[]::new);
    }

    private void parseClientInfo(String[] fields, WowClient wowClient) {
        int currentStep = fields.length - CLIENT_INFO_STEP;
        wowClient.setNickname(fields[currentStep]);
        currentStep++;
        wowClient.setRealm(fields[currentStep]);
        currentStep++;
        wowClient.setFraction(fields[currentStep]);
        currentStep++;
        if (fields[currentStep].contains(BATTLE_TAG)) {
            wowClient.setBattleTag(fields[currentStep]);
            currentStep++;
        }
        if (fields[currentStep].contains(ORDER_CODE_SYMBOL)) {
            wowClient.setOrderCode(fields[currentStep]);
        }

    }

    private void parseGameAndServiceInfo(String[] fields, WowClient wowClient) {
        int currentStep = GAME_AND_SERVICE_INFO;
        wowClient.setService(fields[currentStep]);
        currentStep--;
        wowClient.setGame(fields[currentStep]);
    }

    private void parseOrderInfo(String[] fields, WowClient wowClient) {
        StringBuilder orderInfo = new StringBuilder();
        for (int i = ORDER_INFO; i < fields.length - CLIENT_INFO_STEP; i++) {
            orderInfo.append(fields[i]);
            if (i != fields.length - CLIENT_INFO_STEP) {
                orderInfo.append(" ");
            }
        }
        String[] rows = arrayToTrim(orderInfo.toString().replace("(", "").split("\\)"));

        int currentStep = MODE_SETTINGS;
        if (rows.length != 2) {
            wowClient.setBoostMode("(" + rows[currentStep] + ")");
        }
        currentStep--;
        wowClient.setPlayingType("(" + rows[currentStep] + ")");
        currentStep--;
        wowClient.setRegion("(" + rows[currentStep] + ")");

        setDateTime(rows, wowClient);
        setCharacterClass(rows, wowClient);
    }

    private void setSpecificBosses(String[] rows, WowClient wowClient) {
        if (!wowClient.getService().toLowerCase().contains("single")) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = MODE_SETTINGS + 1; i < rows.length; i++) {
            if (i == rows.length - 1) {
                stringBuilder.append(rows[i]);
            } else {
                stringBuilder.append(rows[i]).append(", ");
            }
        }
        if (stringBuilder.toString().length() != 0) {
            wowClient.setSpecificBosses(stringBuilder.toString());
        }
    }


    private void setDateTime(String[] rows, WowClient wowClient) {
        for (int i = MODE_SETTINGS; i < rows.length; i++) {
            if (rows[i].contains(DATE_TIME_DELIMITER)) {
                wowClient.setOrderDateTime("(" + rows[i] + ")");
                return;
            }
        }
        setSpecificBosses(rows, wowClient);
    }

    private void setCharacterClass(String[] rows, WowClient wowClient) {
        WowClassEnum[] enums = WowClassEnum.values();
        for (int i = MODE_SETTINGS; i < rows.length; i++) {
            for (WowClassEnum anEnum : enums) {
                if (rows[i].toLowerCase().replace(" ", "").contains(anEnum.value.toLowerCase().replace(" ", ""))) {
                    wowClient.setCharacterClass("(" + rows[i] + ")");
                    return;
                }
            }
        }
    }


}




