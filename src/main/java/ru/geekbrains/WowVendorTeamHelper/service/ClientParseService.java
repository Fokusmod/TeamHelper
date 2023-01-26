package ru.geekbrains.WowVendorTeamHelper.service;

import ru.geekbrains.WowVendorTeamHelper.utils.RequestParser;
import ru.geekbrains.WowVendorTeamHelper.utils.RequestParserFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ClientParseService {


//    private final RequestParser parser = RequestParserFactory.createRequestParser();
//
//
//    public void getClientList(String clients) {
//
//        //Список детализированной информации по каждому клиенту. Заготовка для следующего этапа.
//        List<DataClient> clientDataList = new ArrayList<>();
//
//        //Строковое представление клиентов.
//        List<String> clientList = parser.parseString(clients);
//
//        LinkedList<String> rowsString = new LinkedList<>();
//
//        for (int i = 0; i < clientList.size(); i++) {
//            String clientInfo = clientList.get(i);
//            String[] raidInfoRows = clientInfo.split(",");
//
//            for (int j = raidInfoRows.length; j > 0; j--) {
//                rowsString.addLast(raidInfoRows[j - 1].trim());
//            }
//
//
//            String s = rowsString.get(0).substring(0, 7);
//            rowsString.remove(0);
//            rowsString.addFirst(s);
//
//            DataClient client = DataClient.builder()
//                    .ordersCode(rowsString.get(0))
//                    .BTag(rowsString.get(1))
//                    .realm(rowsString.get(3))
//                    .clientNickname(rowsString.get(4))
//                    .orderTime(rowsString.get(5))
//                    .build();
//
//            String[] data = parseRegionAndClientTypeAndCountBosses(rowsString.get(6));
//
//            client.setRegion(data[0]);
//            client.setServiceType(data[2]);
//            client.setClientBoostType(data[1]);
//            client.setRaidType(rowsString.get(7));
//
//            clientDataList.add(client);
//            rowsString.clear();
//        }
//
//        for (DataClient client : clientDataList) {
//            System.out.println("=====================");
//            System.out.println(client);
//            System.out.println("=====================");
//        }
//
//
//    }
//
//    private String[] parseRegionAndClientTypeAndCountBosses (String string){
//        String[] dataAfterParse = string.split(" ");
//        for (int i = 0; i < dataAfterParse.length; i++) {
//            dataAfterParse[i] = dataAfterParse[i].substring(1, dataAfterParse[i].length()-1);
//        }
//        dataAfterParse[0] = dataAfterParse[0].substring(0,2);
//        if(dataAfterParse[0].contains("-")){
//            dataAfterParse[0] = dataAfterParse[0].substring(0,1);
//        }
//        return dataAfterParse;
//    }
}

//Протестить шару
//"WoW Retail, Vault of the Incarnates Heroic Raid Boost, (eu-servers) (ac-sharing) (standard-8of8-bosses) (VPN - Switzerland, Lucerne), (Sat 14 Jan @ 21:00 CET), Tirilya, silvermoon, Alliance, Pikaryu#2201, &92301F
//        https://worldofwarcraft.com/en-gb/character/eu/silvermoon/Tirilya
//==="

//Протестить фанпей
//"WoW Retail, Vault of the Incarnates Heroic Raid Boost , (eu-servers) (selfplayed) (standard 8 of 8 bosses) Викингтут-РФ, орда ,Медоед#21778 , &BY0078
//==="

//протестить сингл
//WoW Retail, Vault of the Incarnates Single Bosses Boost, (eu-servers) (selfplayed) (normal-mode) (Broodkeeper Diurna), maddisôn, thrall, Horde, Kronez#21548, &025093
//        https://worldofwarcraft.com/en-gb/character/eu/thrall/maddisôn
//        ===

//Протестить ласта
//+ласт
//"WoW Retail, Vault Last Boss Raszageth the Storm-Eater boost, (eu-servers) (selfplayed) (heroic-mode), (Sat 14 Jan @ 18:30 CET), Solidschneck, tarren-mill, Horde, Sadd#2727, &83E0A4
//https://worldofwarcraft.com/en-gb/character/eu/tarrenmill/Solidschneck
//===" (отредактировано)


