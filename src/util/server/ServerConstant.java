package util.server;

import bitzero.server.config.ConfigHandle;

import bitzero.util.common.business.CommonHandle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import java.util.Iterator;

import model.ZPUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerConstant {
    public static final int HEIGHT_MAP = 40;
    public static final int WEIGHT_MAP = 40;
    
    public static final String town="TOW_1";
    public static final String gold_sto="STO_1";
    public static final String elixir_sto="STO_2";
    public static final String darkElixir_sto="STO_3";
    
    public static final String complete_status = "complete"; //nha ranh
    public static final String pending_status = "pending"; // nha dang xay
    public static final String upgrade_status = "upgrade"; //nha dang upgrade
    public static final String destroy_status = "destroy"; //nha dang upgrade
    
    public static final String gold_resource = "gold"; 
    public static final String elixir_resource = "elixir"; 
    public static final String darkElixir_resource = "darkElixir"; 
    public static final String coin_resource = "coin"; 
    
    public static final String gold_capacity = "capacityGold";
    public static final String elixir_capacity = "capacityElixir"; 
    public static final String darkElixir_capacity = "capacityDarkElixir";
    
    public static final String BARRACK_TYPE = "BAR_1";
    
    public static final short guild_leader = 2;
    public static final short guild_moderator = 1;
    public static final short guild_member = 0;
    public static final short guild_status_open = 0;
    public static final short guild_status_close = 1;
    
    public static final short guild_status_confirm = 2;
        
    public static JSONObject configInitGame ;
    public static JSONObject configArmyCamp ;
    public static JSONObject configBarrack ;
    public static JSONObject configBuilderHut ;
    public static JSONObject configLaboratory ;
    public static JSONObject configResource ;
    public static JSONObject configStorage ;
    public static JSONObject configTownHall;
    public static JSONObject configTroop;
    public static JSONObject configTroopBase;
    public static JSONObject configDefence;
    public static JSONObject configObstacle;
    public static JSONObject configWall;
    public static JSONObject configClanCastle;
    public static JSONObject config;
    
    public static final int CREATE_GUILD_COST = 40000;
        
    public static final short SUCCESS = 1;
    public static final short ERROR = 0;
    public static final short GIVE_OVER_MAX = 2;
    
    public static final short SEARCH_ID = 0;
    public static final short SEARCH_NAME = 1;
    
    public static final int ID_SYSTEM = -1;
    public static final int MAX_MESSAGES_QUEUE = 100;
    public static final int MAX_TROOP_AMOUNT_USER_CAN_GIVE = 5;
    public static final int TIME_REQUEST_TROOP = 1200;              //second
    public static final int ID_CLC_BUILDING = 0;              //second
    public static final short ONLINE = 1;
    public static final short OFFLINE = 2;

    
    //Type messageGuild
    public static final short NORMAL = 1;    
    public static final short ASK_TROOP = 2;
    
    //Type response Message
    public static final short VALIDATE = 1;
    public static final short TO_ALL = 2;
    
    public static final String PLAYER_INFO = "player_info";
    public static final String PLAYER_TRANSIENT = "player_transient";
    public static final String MACHINE_TRANSIENT = "machine_transient";

    public static final boolean IS_CHEAT = (ConfigHandle.instance().getLong("isCheat") == 1);
    //public static final boolean IS_USE_SECOND_DATACONTROLLER = (ConfigHandle.instance().getLong("useSecondDataController") == 1);
    public static final boolean IS_PURCHASE = (ConfigHandle.instance().getLong("isPurchase") == 1);
    public static final boolean IS_METRICLOG = (ConfigHandle.instance().getLong("isMetriclog") == 1);
    public static final boolean ZME_ENABLE = (ConfigHandle.instance().getLong("zme_enable") == 1);

    public static final String GAME_DATA_KEY_PREFIX = ConfigHandle.instance().get("game_data_key_prefix").trim();
    public static final String USER_INFO_KEY = ConfigHandle.instance().get("user_info_key").trim();
    public static final int FARM_ID_COUNT_FROM = Integer.valueOf(ConfigHandle.instance().get("farm_id_count_from"));
    public static final String FARM_ID_KEY = ConfigHandle.instance().get("farm_id_key").trim();
    public static final String LAST_SNAPSHOT_KEY = ConfigHandle.instance().get("last_snapshot_key").trim();
    public static final String SEPERATOR = ConfigHandle.instance().get("key_name_seperator").trim();
    public static final String SERVER_ID = ConfigHandle.instance().get("serverId").trim();
    public static final boolean ENABLE_PAYMENT = (ConfigHandle.instance().getLong("enable_payment") == 1);
    public static final boolean ENABLE_ADMIN_PROMO = (ConfigHandle.instance().getLong("enable_admin_promo") == 1);
    public static final String CLIENT_VER = ConfigHandle.instance().get("clientVer").trim();
    public static final boolean DEV_ENVIRONMENT = (ConfigHandle.instance().getLong("devEnvironment") == 1);
    public static final String GG_STORE = ConfigHandle.instance().get("gg_store_url").trim();
    public static final String SS_STORE = ConfigHandle.instance().get("ss_store_url").trim();
    
    public static final int CUSTOM_LOGIN = ConfigHandle.instance().getLong("custom_login").intValue();
    

    public static void readConfigArmyCamp(){
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>aaaaaaaaaaaaaaaaaaaaaaa");
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"ArmyCamp.json");
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
            ServerConstant.configArmyCamp = new JSONObject(contents.toString());
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configArmyCamp.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configArmyCamp.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    
    public static void readConfigBarrack(){
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"Barrack.json");
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
            ServerConstant.configBarrack = new JSONObject(contents.toString());
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configBarrack.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configBarrack.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    
    public static void readConfigBuilder(){
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"BuilderHut.json");
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
            ServerConstant.configBuilderHut = new JSONObject(contents.toString());
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configBuilderHut.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configBuilderHut.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    
    public static void readConfigLaboratory(){
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"Laboratory.json");
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
            ServerConstant.configLaboratory = new JSONObject(contents.toString());
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configLaboratory.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configLaboratory.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    
    public static void readConfigResource(){
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"Resource.json");
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
            ServerConstant.configResource = new JSONObject(contents.toString());
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configResource.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configResource.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    
    public static void readConfigStorage(){
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"Storage.json");
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
            ServerConstant.configStorage = new JSONObject(contents.toString());
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configStorage.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configStorage.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    public static void readConfigTownHall(){
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"TownHall.json");
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
            ServerConstant.configTownHall = new JSONObject(contents.toString());
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configTownHall.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configTownHall.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    
    public static void readConfigDefence(){
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"Defence.json");
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
            ServerConstant.configDefence = new JSONObject(contents.toString());
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configDefence.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configDefence.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    
    public static void readConfigTroop(){
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        StringBuffer contents = new StringBuffer();
        System.out.println("Bat dau doc config Troop");
        try {
            File file = new File(path+"Troop.json");
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
            ServerConstant.configTroop = new JSONObject(contents.toString());
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configTroop.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configTroop.toString());
            fout.close();
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    
    public static void readConfigTroopBase(){
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"TroopBase.json");
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
            ServerConstant.configTroopBase = new JSONObject(contents.toString());
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configTroopBase.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configTroopBase.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    
    public static void readConfigObstacle(){
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"Obstacle.json");
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
            ServerConstant.configObstacle = new JSONObject(contents.toString());
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configObstacle.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configObstacle.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    public static void readConfigWall(){
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"Wall.json");
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
            ServerConstant.configWall = new JSONObject(contents.toString());
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configWall.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configWall.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    public static void readConfigClanCastle(){
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"ClanCastle.json");
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
            ServerConstant.configClanCastle = new JSONObject(contents.toString());
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configClanCastle.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configClanCastle.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    
    public static void readConfig(){        
        readConfigArmyCamp();
        readConfigBarrack();
        readConfigBuilder();
        readConfigLaboratory();
        readConfigResource();
        readConfigStorage();
        readConfigTownHall();
        readConfigTroopBase();
        readConfigTroop();
        readConfigDefence();
        readConfigObstacle();
        readConfigWall();
        readConfigClanCastle();
        
        
        String path = System.getProperty("user.dir")+"/conf/Config_json/";
        ServerConstant.config = new JSONObject();
        
        try {
            ServerConstant.config.put("AMC_1", configArmyCamp.getJSONObject("AMC_1"));
            
            ServerConstant.config.put("BAR_1", configBarrack.getJSONObject("BAR_1"));
            ServerConstant.config.put("BAR_2", configBarrack.getJSONObject("BAR_2"));
            
            ServerConstant.config.put("BDH_1", configBuilderHut.getJSONObject("BDH_1"));
            
            ServerConstant.config.put("LAB_1", configLaboratory.getJSONObject("LAB_1"));
            
            ServerConstant.config.put("RES_1", configResource.getJSONObject("RES_1"));
            ServerConstant.config.put("RES_2", configResource.getJSONObject("RES_2"));
            ServerConstant.config.put("RES_3", configResource.getJSONObject("RES_3"));
            
            ServerConstant.config.put("STO_1", configStorage.getJSONObject("STO_1"));
            ServerConstant.config.put("STO_2", configStorage.getJSONObject("STO_2"));
            ServerConstant.config.put("STO_3", configStorage.getJSONObject("STO_3"));
            
            ServerConstant.config.put("TOW_1", configTownHall.getJSONObject("TOW_1"));
            
            ServerConstant.config.put("DEF_1", configDefence.getJSONObject("DEF_1"));
            ServerConstant.config.put("DEF_2", configDefence.getJSONObject("DEF_2"));
            ServerConstant.config.put("DEF_3", configDefence.getJSONObject("DEF_3"));
            ServerConstant.config.put("DEF_4", configDefence.getJSONObject("DEF_4"));
            ServerConstant.config.put("DEF_5", configDefence.getJSONObject("DEF_5"));
            ServerConstant.config.put("DEF_6", configDefence.getJSONObject("DEF_6"));
            ServerConstant.config.put("DEF_7", configDefence.getJSONObject("DEF_7"));
            ServerConstant.config.put("DEF_8", configDefence.getJSONObject("DEF_8"));
            ServerConstant.config.put("DEF_9", configDefence.getJSONObject("DEF_9"));
            ServerConstant.config.put("DEF_10", configDefence.getJSONObject("DEF_10"));
            ServerConstant.config.put("DEF_11", configDefence.getJSONObject("DEF_11"));
            ServerConstant.config.put("DEF_12", configDefence.getJSONObject("DEF_12"));
            ServerConstant.config.put("DEF_13", configDefence.getJSONObject("DEF_13"));
            
            ServerConstant.config.put("WAL_1", configWall.getJSONObject("WAL_1"));
            ServerConstant.config.put("CLC_1", configClanCastle.getJSONObject("CLC_1"));
            
            Iterator<?> keys = ServerConstant.configObstacle.keys();
            while (keys.hasNext()){
                String key = (String) keys.next();
                JSONObject house_type = (JSONObject) ServerConstant.configObstacle.get(key);
                
                ServerConstant.config.put(key, configObstacle.getJSONObject(key));
            //
            //                System.out.println("typehouse "+ key);
            //                System.out.println("posX "+ house_type.getInt("posX"));
            //                System.out.println("posy "+ house_type.getInt("posY"));              
                    
            }
//            //ServerConstant.config.put("TOW_1", configArmyCamp.getJSONObject("TOW_1"));
            System.out.println(">>>>>>>>>b = " + config.toString());
        } catch (JSONException e) {
        }
        try {
            
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"config.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.config.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }

    }
    private static int goldToG(int gold_bd) {
        return gold_bd;
    }
        
    private static int elixirToG(int elixir_bd) {
        return elixir_bd;
    }

    private static int darkElixirToG(int darkElixir_bd) {
        return darkElixir_bd;
    }
    public static int getGold(String type, int level){
        int g = 0;
        System.out.println("type 788888888888888 = "+ type);
        System.out.println("level 788888888888888 = "+ level);
        try {
            
            JSONObject construction = ServerConstant.config.getJSONObject(type).getJSONObject(String.valueOf(level));
            //System.out.println(">>>>>>>>>>>> construction.hitpoints = "+ type+ ":"+construction.getInt("hitpoints"));
            //Object checkObj = construction.opt("gold");   
            //if (construction.getJSONObject("gold")!= null ){ //neu nha co ton vang
                
               
                //System.out.println("user.gold = " + user.gold);
                //System.out.println("gold = " + construction.getInt("gold"));
                g = construction.getInt("gold");
            //}
        } catch (JSONException e) {
            System.out.println("getGold khong ton' tai nguyen");
        }
        return g;
    } 
    public static int getDarkElixir(String type, int level){
        int g = 0;
        try {
            JSONObject construction = ServerConstant.config.getJSONObject(type).getJSONObject(String.valueOf(level));
            g = construction.getInt("darkElixir");
    //            Object checkObj = construction.opt("darkElixir");
    //            if (checkObj instanceof JSONObject){ //neu nha co ton vang
    //                JSONObject Obj = (JSONObject) checkObj;
    //                //System.out.println("user.gold = " + user.gold);
    //                System.out.println("Obj.getInt(\"darkElixir\") = " + Obj.getInt("darkElixir"));
    //
    //            }
        } catch (JSONException e) {            
            System.out.println("get darkElixir khong ton tai nguyen");
        }
        return g;
    }
    public static int getElixir(String type, int level){
        int g = 0;
        try {
            JSONObject construction = ServerConstant.config.getJSONObject(type).getJSONObject(String.valueOf(level));
            g = construction.getInt("elixir");
    //            Object checkObj = construction.opt("elixir");
    //            if (checkObj instanceof JSONObject){ //neu nha co ton vang
    //                JSONObject Obj = (JSONObject) checkObj;
    //                //System.out.println("user.gold = " + user.gold);
    //                System.out.println("Obj.getInt(\"elixir\") = " + Obj.getInt("elixir"));
    //                g = Obj.getInt("elixir");
    //            }
        } catch (JSONException e) {
            System.out.println("getElixir khong ton tai nguyen");
        }
        return g;
    }
    public static int getCoin(String type, int level){
        System.out.println("level in getCoin: "+ level);
        int g = 0;
        try {
            JSONObject construction = ServerConstant.config.getJSONObject(type).getJSONObject(String.valueOf(level));
            System.out.println("coin>>>>>>> construction.hitpoints = "+ type+ ":"+construction.getInt("hitpoints"));
            g = construction.getInt("coin");
    //            Object checkObj = construction.opt("coin");
    //            if (checkObj instanceof JSONObject){ //neu nha co ton vang
    //                JSONObject Obj = (JSONObject) checkObj;
    //                //System.out.println("user.gold = " + user.gold);
    //                System.out.println("Obj.getInt(\"coin\") = " + Obj.getInt("coin"));
    //                g = Obj.getInt("coin");
    //            }
            System.out.println("getCoin  ton: "+g);
        } catch (JSONException e) {
            System.out.println("getCoin khong ton tai nguyen");
        }
        return g;
    }
    public static int checkResourceBasedOnType(ZPUserInfo userInfo, String type, int level) {
        int gold_bd = getGold(type,level);
        int elixir_bd = getElixir(type,level);
        int darkElixir_bd = getDarkElixir(type,level);
        
        return checkResource(userInfo, gold_bd, elixir_bd, darkElixir_bd );
    }
        
    public static int checkResource(ZPUserInfo userInfo, int gold, int elixir, int darkElixir) {
        int g = 0;
        
        if (userInfo.gold < gold){
            g += goldToG(gold - userInfo.gold);                    
        };
        
        if (userInfo.elixir < elixir){
            g += elixirToG(elixir - userInfo.elixir);                    
        };
        
        if (userInfo.darkElixir < darkElixir){
            g += darkElixirToG(darkElixir - userInfo.darkElixir);                    
        };
        
        return g;
    }
    
    
}
