package cmd.obj.map;

import java.util.Arrays;

import java.util.List;

import model.Building;
import model.MapInfo;

import org.json.JSONException;
import org.json.JSONObject;

import util.server.ServerConstant;

public class MapArray {    //vi tri id cua cac building trong Mapinfo_listbuilding
    public int[][] arr = new int[50][50];

    public MapArray() {
        for (int[] row: arr){
            Arrays.fill(row, -1);
        }
            
        System.out.println("Da fillchar map array");
    }
    public boolean check_addBuilding(MapInfo mapinfo, int id, int x, int y){
        //System.out.println(id+" "+mapinfo.listBuilding.get(id).type+" "+x + " "+y);
        int height = getHeight((mapinfo.listBuilding.get(id)));
        int weight = getWeight((mapinfo.listBuilding.get(id)));
        
        //System.out.println("id, x, y = "+mapinfo.listBuilding.get(id).type+ " "+mapinfo.listBuilding.get(id).id);
        if ((hasId(id, mapinfo.listBuilding)) ) { //neu id chua ton tai
            return false;
        }
        if (!checkPos(x,y,height,weight)){ //neu check position bi false
            return false;
        }
        
        return true;
    }
    
    public boolean check_addObs(MapInfo mapinfo, int id, int x, int y){
        //System.out.println(id+" "+mapinfo.listBuilding.get(id).type+" "+x + " "+y);
        int height = getHeight_obs((mapinfo.listObs.get(id)));
        int weight = getWeight_obs((mapinfo.listObs.get(id)));
        
        //System.out.println("id, x, y = "+mapinfo.listBuilding.get(id).type+ " "+mapinfo.listBuilding.get(id).id);
//        if ((hasId(id, mapinfo.listObs)) ) { //neu id chua ton tai
//            return false;
//        }
        if (!checkPos(x,y,height,weight)){ //neu check position bi false
            return false;
        }
        
        return true;
    }
    
    public boolean check_addBuilding(MapInfo mapinfo, String type, int x, int y){
        System.out.println("LOG_ADDBUILDING: type+" + type + "x=+"+x+ "+y = "+y);
        int height = getHeight(type);
        int weight = getWeight(type);
        
        //System.out.println("id, x, y = "+type+ " "+x+" "+y);
        
        if (!checkPos(x,y,height,weight)){ //neu check position bi false
            return false;
        }
        
        return true;
    }
    
    public boolean addBuilding(MapInfo mapinfo, int id, int x, int y ){
        //System.out.println(id+" "+mapinfo.listBuilding.get(id).type+" "+x + " "+y);
        int height = getHeight((mapinfo.listBuilding.get(id)));
        int weight = getWeight((mapinfo.listBuilding.get(id)));
        
        if (! check_addBuilding(mapinfo,id,x,y)){
            return false;
        }   
        
        
        //System.out.println("*****addbuilding in map array**************************");
        
       // add bulding in map array
            for(int i=x;i<x+weight;i++){
                for (int j = y;j<y+height;j++){
                    this.arr[i][j] = id;
                }
            }
        //khau tru tai nguyen resource
        
        return true;
    }
    
    public boolean addObs(MapInfo mapinfo, int id, int x, int y ){
        //System.out.println(id+" "+mapinfo.listBuilding.get(id).type+" "+x + " "+y);
        int height = getHeight_obs((mapinfo.listObs.get(id)));
        int weight = getWeight_obs((mapinfo.listObs.get(id)));
        
        if (! check_addObs(mapinfo,id,x,y)){
            return false;
        }   
        
        
        //System.out.println("*****addbuilding in map array**************************");
        
       // add bulding in map array
            for(int i=x;i<x+weight;i++){
                for (int j = y;j<y+height;j++){
                    this.arr[i][j] = id;
                }
            }
        //khau tru tai nguyen resource
        
        return true;
    }
    public void print(){
        for (int[] row: arr){
            
        }
    }
   

    private boolean checkPos(int x, int y, int height, int weight) {
        if (x<0 || y<0 ) {
            System.out.println("vi tri nhap vao sai, x,y= "+ x+","+y);
            return false;
            
        };
        if (x+weight >ServerConstant.WEIGHT_MAP || y+height>ServerConstant.HEIGHT_MAP){
            System.out.println("vi tri nhap vao sai , x+weight,y+height= "+ x+","+y+height);
            return false;
        }
        //System.out.println("*****check new pos***** = "+ x +" +"+y+" "+height+" "+weight);
        for(int i=x;i<x+height;i++){
            for(int j=y;j<y+weight;j++){
                if (this.arr[i][j]!= -1 ){ //neu da co nha noi ay
                    System.out.println("*****SAI ROI, da co nha hoac cay co id la"+ this.arr[i][j]);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkRes(int id) {
        return false;
    }
    private int getHeight(Building building){
        try {
            return ServerConstant.config.getJSONObject(building.type).getJSONObject(Integer.toString(building.level)).getInt("height");
        } catch (JSONException e) {
            return -1;
        }
        
    }
    private int getWeight(Building building){
        //System.out.println("type = " + building.type );
        
        JSONObject config = ServerConstant.config;

        try {
            return config.getJSONObject(building.type).getJSONObject(Integer.toString(building.level)).getInt("height");
        } catch (JSONException e) {
            return -1;
        }
        
    }

    public boolean moveBuilding(MapInfo mapinfo, int id, int x, int y) {
        System.out.println("move Building"+ id +" "+x+" "+y);
        if (!hasId(id,mapinfo.listBuilding)){ // neu id chua ton tai
            return false;            
        }
        int height = getHeight((mapinfo.listBuilding.get(id)));
        int weight = getWeight((mapinfo.listBuilding.get(id)));
        
        System.out.println("move Building2"+ id +" "+x+" "+y);
        if (!checkPos(x,y,height,weight)){ //
            System.out.println("SAI VI TRI DA CO NHA O");
            
            return false;
        }
        System.out.println("move Building3"+ id +" "+x+" "+y);
//        if (!checkRes(id)){
//            return false;
//        }
        System.out.println("move Building4"+ id +" "+x+" "+y);
        
        int x_cur = mapinfo.listBuilding.get(id).posX;
        int y_cur = mapinfo.listBuilding.get(id).posY;
        //thay doi vi tri tren MapInfo-listBuilding
        mapinfo.listBuilding.get(id).setPos(x,y);
        
        // xoa vi tri cu tren maparray
        for(int i=x_cur;i<x_cur+weight;i++){
            for (int j=y_cur;j<y_cur+height;j++){
                this.arr[i][j] = -1;
            }
        }
        // them vi tri moi tren maparray
        for(int i=x;i<x+weight;i++){
            for (int j = y;j<y+height;j++){
                this.arr[i][j] = id;
            }
        }
        return true;
    }

    private boolean hasId(int id, List<Building> listbuilding) {
        if (listbuilding.isEmpty()) return false;
        for(Building building:listbuilding){
            if (building.getId()==id && !building.status.equals(ServerConstant.destroy_status)) return true;
        }
        return false;
    }

    private int getHeight(String type) {
        
        try {
            return ServerConstant.config.getJSONObject(type).getJSONObject("1").getInt("height");
        } catch (JSONException e) {
            return -1;
        }
    }

    private int getWeight(String type) {
        

        try {
            return ServerConstant.config.getJSONObject(type).getJSONObject("1").getInt("height");
        } catch (JSONException e) {
            return -1;
        }
    }

    private int getHeight_obs(Obs obs) {
        try {
            return ServerConstant.config.getJSONObject(obs.type).getJSONObject("1").getInt("height");
        } catch (JSONException e) {
            return -1;
        }
    }

    private int getWeight_obs(Obs obs) {
        try {
            return ServerConstant.config.getJSONObject(obs.type).getJSONObject("1").getInt("height");
        } catch (JSONException e) {
            return -1;
        }
    }
}


