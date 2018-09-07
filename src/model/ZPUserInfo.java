package model;

import bitzero.server.entities.User;

import bitzero.util.common.business.CommonHandle;




import java.awt.Point;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

import java.io.UnsupportedEncodingException;

import java.io.Writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import util.database.DataModel;

import util.server.ServerConstant;


public class ZPUserInfo extends DataModel {
    // Zing me
    public int id;
    public String name;
    public long exp = 0L;
    public int danhvong = 0;
    //public int level = 0;
    public int gold = 0;
    public int coin = 1000;
    public int elixir = 0;
    public int darkElixir = 0; 
    public int builderNumber ;

    public boolean is_in_guild = false;
    public int id_guild = -1;
    public String name_guild = "";
    public int id_logo_guild = -1;
    public long last_time_ask_for_troops = 0;
    public long last_time_left_guild = 0;
    public short donate_troop = 0;
    public short request_troop = 0;
    
    public ZPUserInfo(int _id, String _name) {
        super();
        id = _id;
        name = _name;        
        InitJsonData();        
    }

    public void setDanhVong(int danhvong) {
        this.danhvong = danhvong;
    }

    public int getDanhVong() {
        return danhvong;
    }

    public String toString() {
        return String.format("%s|%s", new Object[] { id, name });
    }
    
    private void InitJsonData() {
        this.danhvong = new Random().nextInt(2500) + 1;
        String path = System.getProperty("user.dir")+"/conf/";
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"init.json");
            Reader r = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader reader = new BufferedReader(r);
            String text = null;
            
            while ((text = reader.readLine()) != null){
                contents.append(text).append(System.getProperty("line.separator"));
            }
        } catch (Exception e) {
            CommonHandle.writeErrLog(e);
        }
        
        try {
            ServerConstant.configInitGame = new JSONObject(contents.toString());
                   
            JSONObject player = ServerConstant.configInitGame.getJSONObject("player");
            JSONObject obs = ServerConstant.configInitGame.getJSONObject("obs");
            System.out.println("NUGGGGGGGGGGGGGGG");    
            this.gold = player.getInt("gold");
            System.out.println(this.gold); 
            this.coin = player.getInt("coin");
            System.out.println(this.coin); 
            this.elixir = player.getInt("elixir");
            System.out.println(this.elixir); 
            this.darkElixir = player.getInt("darkElixir");
            System.out.println(this.darkElixir); 
            this.builderNumber = player.getInt("builderNumber"); 
            System.out.println(this.builderNumber);             
            
            
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configplayerInitGame.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configInitGame.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId1() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getExp() {
        return exp;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getGold() {
        return gold;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getCoin() {
        return coin;
    }

    public void setElixir(int elixir) {
        this.elixir = elixir;
    }

    public int getElixir() {
        return elixir;
    }

    public void setDarkElixir(int darkElixir) {
        this.darkElixir = darkElixir;
    }

    public int getDarkElixir() {
        return darkElixir;
    }

    public void setBuilderNumber(int builderNumber) {
        this.builderNumber = builderNumber;
    }

    public int getBuilderNumber() {
        return builderNumber;
    }

    public void setIs_in_guild(boolean is_in_guild) {
        this.is_in_guild = is_in_guild;
    }

    public boolean isIs_in_guild() {
        return is_in_guild;
    }

    public void setId_guild(int id_guild) {
        this.id_guild = id_guild;
    }

    public int getId_guild() {
        return id_guild;
    }

    public void setName_guild(String name_guild) {
        this.name_guild = name_guild;
    }

    public String getName_guild() {
        return name_guild;
    }

    public void setId_logo_guild(int id_logo_guild) {
        this.id_logo_guild = id_logo_guild;
    }

    public int getId_logo_guild() {
        return id_logo_guild;
    }

    public void setLast_time_ask_for_troops(long last_time_ask_for_troops) {
        this.last_time_ask_for_troops = last_time_ask_for_troops;
    }

    public long getLast_time_ask_for_troops() {
        return last_time_ask_for_troops;
    }
    
    public int getTroopLevel(String troopType) {
        int level = 1;
        try {
            TroopInfo troopInfo = (TroopInfo) TroopInfo.getModel(this.getId(), TroopInfo.class);
            Troop troop = troopInfo.troopMap.get(troopType);
            level = troop.level;
        } catch (Exception e) {
            return 0;
        }
        return level;
    }
    
    public int getTotalCapacityAMCs() {
        int total = 0;
        MapInfo mapInfo;
        try {
            mapInfo = (MapInfo) MapInfo.getModel(this.getId(), MapInfo.class);
        } catch (Exception e) {
            System.out.println("=============== KHONG GET DC MAP INFO =================");
            return 0;
        }
        
        JSONObject amcConfig;
        try {
            amcConfig = ServerConstant.configArmyCamp.getJSONObject("AMC_1");
        } catch (JSONException e) {
            System.out.println("=============== KHONG GET DC ARMY CONFIG =================");
            return 0;
        }

        List<Building> listBuilding = mapInfo.listBuilding;
        Iterator<Building> i = listBuilding.iterator();
        while (i.hasNext()) {
            Building build = i.next();

            if((build.status.equals("complete") || build.status.equals("upgrade")) && (build.type.equals("AMC_1"))){
                int capacity;

                try {
                    capacity = amcConfig.getJSONObject(Integer.toString(build.level)).getInt("capacity");
                    System.out.println("=============== capacity:  " + capacity);
                } catch (JSONException e) {
                    return 0;
                }
                total += capacity;
            }
        }
        return total;
    }
    
    public int getCurrentCapacityTroop() {
        int total = 0;
        TroopInfo troopInfo;
        try {
            troopInfo = (TroopInfo) TroopInfo.getModel(this.getId(), TroopInfo.class);
        } catch (Exception e) {
            return 0;
        }
        
        JSONObject troopBaseConfig = ServerConstant.configTroopBase;
        
        int space;
        Troop troop;
        for (String troopType : troopInfo.troopMap.keySet()) {
            troop = troopInfo.troopMap.get(troopType);
            try {
                space = troopBaseConfig.getJSONObject(troopType).getInt("housingSpace");
            } catch (JSONException e) {
                return 0;
            }
            total += space * troop.population;
        }
        
        return total;
    }

    public void increaseAmountTroop(String typeTroop, int amount) {
        System.out.println("Linh " + typeTroop + " tang " + amount);
        
        //Tang so luong loai troop nay
        TroopInfo troopInfo;
        try {
            troopInfo = (TroopInfo) TroopInfo.getModel(this.getId(), TroopInfo.class);
        } catch (Exception e) {
            return;
        }
        
        Troop troopObj = troopInfo.troopMap.get(typeTroop);
        troopObj.population += amount;
        troopInfo.troopMap.put(typeTroop, troopObj);
        try {
            troopInfo.saveModel(this.getId());
        } catch (Exception e) {
        }
    }

    public void reduceUserResources(int gold, int elixir, int darkElixir, int coin, String type, boolean isAdd ){
        //tru gold
        if (this.gold < gold){
            this.gold = 0;
            System.out.println("het gold");
        }
        else {
            this.gold = this.gold - gold;
            System.out.println("gold bi tru di " + gold+ ", user con lai ="+ this.gold+" gold");
        }
        //tru elixir
        if (this.elixir < elixir){
            this.elixir = 0;
            System.out.println("het elixir");
            
        }
        else {
            this.elixir = this.elixir - elixir;
            System.out.println("elixir bi tru di " + elixir + ", user con lai = "+ this.elixir+" elixir");
        }
        if (this.darkElixir < darkElixir){
            
            this.darkElixir = 0;
            System.out.println("het darkElixir");
        }
        else {
            this.darkElixir = this.darkElixir - darkElixir;
            System.out.println("darkElixir bi tru di " + darkElixir + ", user con lai ="+ this.darkElixir+" darkElixir");
        }
        
        this.coin = this.coin - coin;
        System.out.println("coin bi tru di " + coin + ", user con lai ="+ this.coin+" coin");
        
        if (type.equals("BDH_1") ){
            this.builderNumber = this.builderNumber + 1;
        }
        
    }


    public void addResource(int _gold, int _elixir, int _darkElixir, int _coin, int gold_rq, int elx_rq, int dElx_rq) {
        this.gold = this.gold + _gold;
        if (this.gold>gold_rq) {
            this.gold = gold_rq;
        }
        this.elixir = this.elixir + _elixir;
        if (this.elixir>elx_rq) {
            this.elixir = elx_rq;
        }
        this.darkElixir = this.darkElixir + _darkElixir;
        if (this.darkElixir>dElx_rq){
            this.darkElixir = dElx_rq;
        }
        this.coin = this.coin + _coin;
    }

    public void setLast_time_left_guild(long last_time_left_guild) {
        this.last_time_left_guild = last_time_left_guild;
    }

    public long getLast_time_left_guild() {
        return this.last_time_left_guild;
    }

    public void setDonate_troop(short donate_troop) {
        this.donate_troop = donate_troop;
    }

    public short getDonate_troop() {
        return donate_troop;
    }

    public void setRequest_troop(short request_troop) {
        this.request_troop = request_troop;
    }

    public short getRequest_troop() {
        return request_troop;
    }

    public void addGuildInfo(int _id_guild, String _name_guild, int _id_logo_guild){
        this.is_in_guild = true;
        this.id_guild = _id_guild;
        this.name_guild = _name_guild;
        this.id_logo_guild = _id_logo_guild;
    }
    public void leftGuild(){
        this.is_in_guild = false;
        this.last_time_left_guild = System.currentTimeMillis();
        this.last_time_ask_for_troops = 0;
        System.out.println("Nguoi vua ra khoi bang, id = "+this.id+", time roi bang = "+ this.last_time_left_guild);
    }

    public int getId() {
        return this.id;
    }

    public boolean canJoinGuild() {
        System.out.println("Thio gian hien tai: " + System.currentTimeMillis());
        System.out.println("Thio gian roi bang truoc do: " + this.getLast_time_left_guild());
        System.out.println("Thio gian chenh nhau: " + (System.currentTimeMillis() - this.getLast_time_left_guild()));
        if (System.currentTimeMillis() - this.getLast_time_left_guild() >= 7200000) // 2 tieng
        {
            return true;
        }
        return false;
    }
}
