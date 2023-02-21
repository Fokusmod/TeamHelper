package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.model.MyMessage;
import ru.geekbrains.WowVendorTeamHelper.model.WowClient;
import ru.geekbrains.WowVendorTeamHelper.utils.emuns.WowClassEnum;

import java.util.ArrayList;
import java.util.List;

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
    private final WowClientService wowClientService;
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

    public void stringParser(MyMessage message) {
        List<String> clientStringList = new ArrayList<>();
        String[] strings = message.getText().replace("*", "").replace("&amp;", ORDER_CODE_SYMBOL).replace("\"", "").split("\n");

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
        clientParser(clientStringList);
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

    private void clientParser(List<String> strings) {
        for (String order : strings) {
            String[] string = order.split("\n");
            parseRows(string);
        }
    }

    private void parseRows(String[] string) {
        List<WowClient> list = new ArrayList<>();
        WowClient wowClient;
        for (int i = 0; i < string.length; i++) {
            String[] fields = string[i].split(",");
            arrayToTrim(fields);
            if (string[i].contains(ORDER_CODE_FP_PATTERN) && fields.length < CORRECT_FORMAT_COMMA) {
                wowClient = noParseClient(string);
                list.add(wowClient);
            } else if (string[i].contains(ORDER_CODE_SYMBOL) && fields.length > CORRECT_FORMAT_COMMA) {
                wowClient = new WowClient();
                try {
                    parseGameAndServiceInfo(fields, wowClient);
                    parseClientInfo(fields, wowClient);
                    parseOrderInfo(fields, wowClient);
                    parseArmoryLink(string, wowClient);
                    list.add(wowClient);
                } catch (ArrayIndexOutOfBoundsException e) {
                    wowClient = noParseClient(string);
                    list.add(wowClient);
                }
            } else if (!string[i].contains(DELIMITER) && !string[i].contains(PROTOCOL)
                    && !string[i].contains(ORDER_CODE_FP_PATTERN) && !string[i].contains(ORDER_CODE_SYMBOL)) {
                list.get(0).setComments(string[i]);
            } else if (!string[i].contains(DELIMITER) && !string[i].contains(PROTOCOL)) {
                wowClient = noParseClient(string);
                list.add(wowClient);
            }
        }
        wowClientService.saveClient(list);
    }

    private WowClient noParseClient(String[] string) {
        WowClient wowClient = null;
        for (String s : string) {
            if (!s.contains(DELIMITER) && !s.contains(PROTOCOL)) {
                String[] fields = s.split(",");
                wowClient = new WowClient();
                wowClient.setNoParseInfo(s);
                wowClient.setOrderCode(correctingOrderCode(fields));
                parseArmoryLink(string, wowClient);
            }
        }
        return wowClient;
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
        wowClient.setBattleTag(fields[currentStep]);
        currentStep++;
        wowClient.setOrderCode(fields[currentStep]);
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
            wowClient.setBoostMode(rows[currentStep]);
        }
        currentStep--;
        wowClient.setPlayingType(rows[currentStep]);
        currentStep--;
        wowClient.setRegion(rows[currentStep]);

        setDateTime(rows, wowClient);
        setCharacterClass(rows, wowClient);
    }

    private void setSpecificBosses(String[] rows, WowClient wowClient) {
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
                wowClient.setOrderDateTime(rows[i]);
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
                    wowClient.setCharacterClass(rows[i]);
                    return;
                }
            }
        }
    }


}




