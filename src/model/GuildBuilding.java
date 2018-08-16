package model;

import bitzero.server.entities.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.database.DataModel;

import util.server.ServerConstant;

public class GuildBuilding extends DataModel {
    public long lastRequestTroopTimeStamp = 0L;
    public List<TroopGuild> troopGuildList = new ArrayList<TroopGuild>();
    //Danh sach cac user da cho linh: userId, amount
    public Map <Integer, Integer> userGaveMap = new HashMap<Integer, Integer>();
    
    public GuildBuilding() {
        super();
    }
    
    //Ai cho, cho troop nao, chi cho 1 unit
    private void addTroopGuild(int userId, TroopGuild troop) {
        //Check capacity cua guildBuilding - phu thuoc vao level cua GuiBuilding
        int amount = userGaveMap.get(userId);
        if ((Integer) amount != null) {
            if(amount >= ServerConstant.MAX_TROOP_AMOUNT_USER_CAN_GIVE){
                //Response ERROR
                return;
            }
            int newAmount = amount++;
            userGaveMap.put(userId, newAmount);
        } else {
            userGaveMap.put(userId, 1);
        }
        troopGuildList.add(troop);
        
        int capacityGuild;
//        if(troopGuildList.size() >= ServerConstant)

        //Response SUCCESS Give Troop
        //Response to all User
    }
    
    private int getLevelGuildBuilding(User user, int idGuildBuilding) {
        MapInfo mapInfo;
        try {
            mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
        } catch (Exception e) {
            return 0;
        }
        if (mapInfo == null) {         
           return 0;
        }
        int level = mapInfo.listBuilding.get(idGuildBuilding).level;
        return level;
    }
}
