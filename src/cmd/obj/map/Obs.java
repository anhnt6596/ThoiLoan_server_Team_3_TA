package cmd.obj.map;

import org.json.JSONException;
import org.json.JSONObject;

import util.server.ServerConstant;

public class Obs {
    public int id;
    public String type;
    public int posX;
    public int posY;
    public String status;
    public int level =1;
    public Obs() {
        super();
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
}
