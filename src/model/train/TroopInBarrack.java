package model.train;

import bitzero.server.entities.User;

import bitzero.util.common.business.CommonHandle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.Iterator;

import model.Troop;

import org.json.JSONException;
import org.json.JSONObject;

import util.database.DataModel;

import util.server.ServerConstant;

public class TroopInBarrack extends DataModel {
    public String name;
//    public int level;                               //doc level tu TroopInfo tra ve
    public int amount;
    public int currentPosition;
    public int housingSpace;                                        //doc tu config
    public int trainingTime;                                        //doc tu config
    public int trainingDarkElixir;                                  //cost phu thuoc vao level
    public int trainingElixir;
    
    public TroopInBarrack(String _name) {
        super();
        name = _name;
//        level = _level;
        amount = 0;
        currentPosition = -1;
        initConfig();
    }
    
    
    public void initConfig() {
        JSONObject troopBaseConfig = ServerConstant.configTroopBase;
        
        try {
            Iterator<?> keys = troopBaseConfig.keys();
            while (keys.hasNext()){
                String key = (String) keys.next();
                JSONObject troopType = (JSONObject) troopBaseConfig.get(key);
                
                if (key.equals(name)){
                    housingSpace = troopType.getInt("housingSpace");
                    trainingTime = troopType.getInt("trainingTime");
                }
            }
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
}
