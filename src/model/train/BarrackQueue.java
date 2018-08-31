package model.train;

import bitzero.server.entities.User;

import bitzero.util.common.business.CommonHandle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.TroopInfo;

import org.json.JSONException;
import org.json.JSONObject;

import util.database.DataModel;

import util.server.ServerConstant;

public class BarrackQueue extends DataModel {
    private int id;
    private int barrackLevel;           
    public long startTime;
    public List<TroopInBarrack> trainTroopList = new LinkedList<TroopInBarrack>();

    public BarrackQueue(int id, int barrackLevel) {
        this.setId(id);
        this.setBarrackLevel(barrackLevel);
        startTime = 0L;
    }

    public TroopInBarrack getTroopInBarrackByName(String troopType) {
        TroopInBarrack troop;
        for(int i = 0; i < trainTroopList.size(); i++) {
            troop = trainTroopList.get(i);
            if(troop.getName().equals(troopType)) return troop;
        }
        return null;
    }
    
    public void doReset() {
        startTime = 0L;
        trainTroopList.clear();
    }
    
    public void updateQueue(int dropedPosition) {
        trainTroopList.remove(dropedPosition);
    }
    
    public int getQueueLength() {
        try {
            return ServerConstant.configBarrack.getJSONObject(ServerConstant.BARRACK_TYPE).getJSONObject(Integer.toString(barrackLevel)).getInt("queueLength");
        } catch (JSONException e) {
            return 0;
        }
    }
    
    public int getTotalTroopCapacity() {
        int total = 0;
        for(int i = 0; i < trainTroopList.size(); i++) {
            TroopInBarrack troop = trainTroopList.get(i);
            total += troop.getAmount() * troop.getHousingSpace();
        }
        return total;
    }

    public int getAmountItemInQueue() {
        return trainTroopList.size();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }
    
    public void setBarrackLevel(int barrackLevel) {
        this.barrackLevel = barrackLevel;
    }

    public int getBarrackLevel() {
        return barrackLevel;
    }
}
