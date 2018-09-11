package cmd.obj.map;

import org.json.JSONException;
import org.json.JSONObject;

import util.server.ServerConstant;

public class Obs {
    public int id;
    public String type;
    public long timeStart = -1;
    public int posX;
    public int posY;
    public String status;
    public int level =1;
    public Obs() {
        super();
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public Obs(int _id,String _type,int x, int y) {
        super();
        this.id = _id;
        this.type = _type;
        this.posX = x;
        this.posY = y;
        this.status = ServerConstant.complete_status;
    }

    public int getGtoRemove(String resource_type) {
        try {
            JSONObject construction = ServerConstant.configObstacle.getJSONObject(this.type).getJSONObject(Integer.toString(1));
            return(construction.getInt(resource_type));
            
        } catch (JSONException e){
            
            return 0;
        } 
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getPosY() {
        return posY;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public long getTimeConLai() {
        long time_cur = System.currentTimeMillis();
        long time_da_chay = time_cur-this.timeStart;
        System.out.println("time_cur"+time_cur+" this.timeStart= "+this.timeStart+ " time can xay: "+this.getTimeBuild());
        return (this.getTimeBuild()-time_da_chay);
    }

    public long getTimeBuild() {
        try {
            int level = this.level;            
            JSONObject construction = ServerConstant.config.getJSONObject(this.type).getJSONObject(Integer.toString(level));
            return ( (long) construction.getInt("buildTime")*1000);
        } catch (JSONException e){
            System.out.println("get buildTime error");
            return 0;
        }
    }

    public int getElixirReward() {
        try {
            JSONObject construction = ServerConstant.configObstacle.getJSONObject(this.type).getJSONObject(Integer.toString(1));
            return(construction.getInt("rewardElixir"));
            
        } catch (JSONException e){
            
            return 0;
        } 
    }

    public int getDarkElixirReward() {
        try {
            JSONObject construction = ServerConstant.configObstacle.getJSONObject(this.type).getJSONObject(Integer.toString(1));
            return(construction.getInt("rewardDarkElixir"));
            
        } catch (JSONException e){
            
            return 0;
        } 
    }

    public void onRemove() {
        setStatus(ServerConstant.pending_status);
        setTimeStart(System.currentTimeMillis());
    }
    public void finishRemove() {
        setStatus(ServerConstant.destroy_status);        
    }
}
