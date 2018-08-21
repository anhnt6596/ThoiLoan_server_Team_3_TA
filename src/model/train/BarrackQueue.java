package model.train;

import bitzero.server.entities.User;

import bitzero.util.common.business.CommonHandle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.HashMap;
import java.util.Map;

import model.TroopInfo;

import org.json.JSONException;
import org.json.JSONObject;

import util.database.DataModel;

import util.server.ServerConstant;

public class BarrackQueue extends DataModel {
    public int barrackLevel;                     //Dua vào id cua barrack
    public int queueLength;                      //Doc tu config, dua vào level cua barrack             
    public int amountItemInQueue;                //So loai troop dang train
    public int totalTroopCapacity;               //Tong so capacity hien tai cua barrack <= queuelength
    public long startTime;
    public Map <String, TroopInBarrack> troopListMap = new HashMap<String, TroopInBarrack>();
    
    
    public BarrackQueue(int _barrackLevel) {
        super();
        barrackLevel = _barrackLevel;
        amountItemInQueue = 0;
        totalTroopCapacity = 0;
        startTime = 0L;
        queueLength = getQueueLength(barrackLevel);
        try {
            initTroopList();
        } catch (Exception e) {
            System.out.println("======================= Khong the khoi tao troopList tu BarrackQueue ======================");
            System.out.println("======================= Loi la: " + e);
        }
    }
    
    public void doReset() {
        amountItemInQueue = 0;
        totalTroopCapacity = 0;
        startTime = 0L;
        
        TroopInBarrack troopInBarrack;
        for (String troopType : troopListMap.keySet()) {
            troopInBarrack = troopListMap.get(troopType);
            troopInBarrack.amount = 0;
            troopInBarrack.currentPosition = -1;
        }
    }
    
    public void updateQueue(int whoDropId) {
        TroopInBarrack troopInBarrack;
        for (String troopType : troopListMap.keySet()) {
            troopInBarrack = troopListMap.get(troopType);
            if(troopInBarrack.currentPosition > whoDropId){
                troopInBarrack.currentPosition--;
                if(troopInBarrack.currentPosition == 0){
                    this.startTime = System.currentTimeMillis();
                }
            }
        }
    }
    
    
    public TroopInBarrack getTroopByPosition(int position) {
        TroopInBarrack troopInBarrack;
        for (String troopType : troopListMap.keySet()) {
            troopInBarrack = troopListMap.get(troopType);
            if(troopInBarrack.amount > 0 && troopInBarrack.currentPosition == position){
                return troopInBarrack;
            }
        }
        return null;
    }
    
    public int getQueueLength(int level) {
        int length = 0;
        String path = System.getProperty("user.dir")+"/conf/";
        StringBuffer contents = new StringBuffer();
        try {
            File file = new File(path+"Config_json/Barrack.json");
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
            JSONObject barr = new JSONObject(contents.toString());
            length = barr.getJSONObject("BAR_1").getJSONObject(Integer.toString(level)).getInt("queueLength");
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
        return length;
    }
    
    public void initTroopList() {
        //Dua vao level cua barrack ma put troop vao
        //Tu 1->10 la mo khoa ARM tuong ung, 11 mo khoa ARM_16, 12 mo khoa ARM_17
        JSONObject barrack_1Config;
        try {
            barrack_1Config = ServerConstant.configBarrack.getJSONObject("BAR_1");
        } catch (JSONException e) {
            return;
        }
        try {
//            for(int i = 1; i <= barrackLevel; i++){
//                String troopType = barrack_1Config.getJSONObject(Integer.toString(i)).getString("unlockedUnit");
//                TroopInBarrack troop = new TroopInBarrack(troopType);
//                troopListMap.put(troopType, troop);
//            }
                String troopType = barrack_1Config.getJSONObject(Integer.toString(barrackLevel)).getString("unlockedUnit");
                TroopInBarrack troop = new TroopInBarrack(troopType);
                troopListMap.put(troopType, troop);
        } catch (JSONException e) {
            
        }
    }
}
