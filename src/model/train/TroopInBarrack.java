package model.train;

import org.json.JSONException;

import util.database.DataModel;

import util.server.ServerConstant;

public class TroopInBarrack extends DataModel {
    private String name;
    public int amount;
    
    public TroopInBarrack(String name) {
        super();
        this.name = name;
        this.amount = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public int getTrainingTime() {
        try {
            return ServerConstant.configTroopBase.getJSONObject(this.name).getInt("trainingTime");
        } catch (JSONException e) {
            return 0;
        }
    }
    
    public int getHousingSpace() {
        try {
            return ServerConstant.configTroopBase.getJSONObject(this.name).getInt("housingSpace");
        } catch (JSONException e) {
            return 0;
        }
    }
    
    public int getTrainingElixir(int levelTroop) {
        try {
            return ServerConstant.configTroop.getJSONObject(this.name).getJSONObject(Integer.toString(levelTroop)).getInt("trainingElixir");
        } catch (JSONException e) {
            return 0;
        }
    }
    
    public int getTrainingDarkElixir(int levelTroop) {
        try {
            return ServerConstant.configTroop.getJSONObject(this.name).getJSONObject(Integer.toString(levelTroop)).getInt("trainingDarkElixir");
        } catch (JSONException e) {
            return 0;
        }
    }
                                       
}
