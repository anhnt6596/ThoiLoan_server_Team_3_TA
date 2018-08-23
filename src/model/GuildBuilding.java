package model;

import bitzero.server.entities.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
    
    public int getGuildCapacity(ZPUserInfo user) {
        MapInfo mapInfo;
        try {
            mapInfo = (MapInfo) MapInfo.getModel(user.id, MapInfo.class);
        } catch (Exception e) {
            return 0;
        }
        if (mapInfo == null) {         
           return 0;
        }
        int level = mapInfo.listBuilding.get(ServerConstant.ID_CLC_BUILDING).level;

        String type = mapInfo.listBuilding.get(ServerConstant.ID_CLC_BUILDING).type;
        
        JSONObject guildConfig;
        try {
            guildConfig = ServerConstant.configClanCastle.getJSONObject("CLC_1");
        } catch (JSONException e) {
            return 0;
        }
        int guildCapacity;
        try {
            guildCapacity = guildConfig.getJSONObject(Integer.toString(level)).getInt("troopCapacity");
        } catch (JSONException e) {
            return 0;
        }
        
        return guildCapacity;
    }
    
    public int getCurrentTroopCapacityGuild() {
        int total = 0;
        
        JSONObject troopConfig = ServerConstant.configTroopBase;
        int space;
        
        Iterator<TroopGuild> i = troopGuildList.iterator();
        while (i.hasNext()) {
            TroopGuild troop = i.next();
            try {
                space = troopConfig.getJSONObject(troop.name).getInt("housingSpace");
                total += space;
            } catch (JSONException e) {
                return 0;
            }
        }
        return total;
    }
    
    public void resetUserGaveMap() {
        userGaveMap.clear();
    }
}
