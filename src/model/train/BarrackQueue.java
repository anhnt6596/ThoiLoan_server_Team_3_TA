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
    private int barrackLevel;           
    private int amountItemInQueue;                //So loai troop dang train
    private int totalTroopCapacity;               //Tong so capacity hien tai cua barrack <= queuelength
    public long startTime;
    public Map<String, TroopInBarrack> troopListMap = new HashMap<String, TroopInBarrack>();


    public BarrackQueue(int barrackLevel) {
        super();
        this.setBarrackLevel(barrackLevel);
        amountItemInQueue = 0;
        totalTroopCapacity = 0;
        startTime = 0L;
        try {
            initTroopList();
        } catch (Exception e) {
            System.out.println("======================= Khong the khoi tao troopList tu BarrackQueue ======================");
        }
    }
    
    
    public void doReset() {
        amountItemInQueue = 0;
        totalTroopCapacity = 0;
        startTime = 0L;
        
        TroopInBarrack troopInBarrack;
        for (String troopType : troopListMap.keySet()) {
            troopInBarrack = troopListMap.get(troopType);
            troopInBarrack.setAmount(0);
            troopInBarrack.setCurrentPosition(-1);
        }
    }
    
    public void updateQueue(int whoDropId) {
        TroopInBarrack troopInBarrack;
        for (String troopType : troopListMap.keySet()) {
            troopInBarrack = troopListMap.get(troopType);
            if(troopInBarrack.getCurrentPosition() > whoDropId){
                troopInBarrack.setCurrentPosition(troopInBarrack.getCurrentPosition() - 1);
                if(troopInBarrack.getCurrentPosition() == 0){
                    this.startTime = System.currentTimeMillis();
                }
            }
        }
    }
    
    
    public TroopInBarrack getTroopByPosition(int position) {
        TroopInBarrack troopInBarrack;
        for (String troopType : troopListMap.keySet()) {
            troopInBarrack = troopListMap.get(troopType);
            if(troopInBarrack.getAmount() > 0 && troopInBarrack.getCurrentPosition() == position){
                return troopInBarrack;
            }
        }
        return null;
    }
    
    public int getQueueLength() {
        try {
            return ServerConstant.configBarrack.getJSONObject("BAR_1").getJSONObject(Integer.toString(barrackLevel)).getInt("queueLength");
        } catch (JSONException e) {
            return 0;
        }
    }
    
    public void initTroopList() {
        //Dua vao level cua barrack ma put troop vao
        try {
            String troopType = ServerConstant.configBarrack.getJSONObject("BAR_1").getJSONObject(Integer.toString(barrackLevel)).getString("unlockedUnit");
            TroopInBarrack troop = new TroopInBarrack(troopType);
            troopListMap.put(troopType, troop);
        } catch (JSONException e) {
            
        }
    }
    
    public void setBarrackLevel(int barrackLevel) {
        this.barrackLevel = barrackLevel;
    }

    public int getBarrackLevel() {
        return barrackLevel;
    }
    
    public void setAmountItemInQueue(int amountItemInQueue) {
        this.amountItemInQueue = amountItemInQueue;
    }

    public int getAmountItemInQueue() {
        return amountItemInQueue;
    }

    public void setTotalTroopCapacity(int totalTroopCapacity) {
        this.totalTroopCapacity = totalTroopCapacity;
    }

    public int getTotalTroopCapacity() {
        return totalTroopCapacity;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }
}
