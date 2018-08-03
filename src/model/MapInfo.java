package model;

import bitzero.util.common.business.CommonHandle;
import bitzero.util.socialcontroller.bean.UserInfo;

import cmd.obj.map.Army;
import cmd.obj.map.MapArray;
import cmd.obj.map.Obs;

import com.sun.jmx.remote.internal.ServerCommunicatorAdmin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import java.util.Random;

import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import util.database.DataModel;

import util.server.ServerConstant;

public class MapInfo extends DataModel{
    
    //public long id;
   
    public String lastUpdate;
    
    //public String apk = "";
    public int size_building = 0;
    public List<Building> listBuilding = new ArrayList<Building>();

    public int size_army = 0;
    public List<Army> listArmy = new ArrayList<Army>();
    
    public int size_obs = 0;
    public List<Obs> listObs = new ArrayList<Obs>();
    
    public MapInfo() {
            super();
            this.InitJsonData();
        }
    private void InitJsonData() {
        int number_obs =57;
        List<Integer> list_obs = new ArrayList<Integer>();
        String path = System.getProperty("user.dir")+"/conf/";
//        System.out.println("Working Directory = " + );
          
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
            
            
            JSONObject map = ServerConstant.configInitGame.getJSONObject("map");
            JSONObject player = ServerConstant.configInitGame.getJSONObject("player");
            JSONObject obs = ServerConstant.configInitGame.getJSONObject("obs");
            //du lieu nha   
            Iterator<?> keys = map.keys();
            while (keys.hasNext()){
                String key = (String) keys.next();
                JSONObject house_type = (JSONObject) map.get(key);
//                
//                System.out.println("typehouse "+ key);    
//                System.out.println("posX "+ house_type.getInt("posX")); 
//                System.out.println("posy "+ house_type.getInt("posY")); 
                if (key.equals("TOW_1")){
                    addBuilding(key,house_type.getInt("posX")-1, house_type.getInt("posY")-1,1,"complete");
                }else{
                    addBuilding(key,house_type.getInt("posX")-1, house_type.getInt("posY")-1,1,"complete");
                }
                    
            }
            
            System.out.println("listbuilding "+ this.listBuilding.size()+ " and size = "+this.size_building); 
            
            //du lieu co cay hoa la
                        
//            for(int id_obs=0;id_obs<number_obs;id_obs++){  
//                list_obs.add(id_obs);
               // while (true){
                    //int id_obs = new Random().nextInt(57)+1; 
                    
                  //  if (check(id_obs,list_obs)){      
                        
                        
                        //break;
                   // }
              //  }
            //}
            for(int i=0;i<number_obs;i++){
                
                String num = Integer.toString(i+1);
                //System.out.println("num =" +num);
                JSONObject obs_type = (JSONObject) obs.get(num);
                //System.out.println("obs_type =" +obs_type);
                Obs _obs = new Obs(this.size_obs,obs_type.getString("type"),obs_type.getInt("posX")-1, obs_type.getInt("posY")-1);
                this.listObs.add(_obs);
                this.size_obs++;
                
            }
            
            
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configInitGame.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configInitGame.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    public void printValues() {
//        System.out.printf("id=%s|name=%s|ver=%s|ctnver=%s|lastUpdate=%s|pId=%s|des=%s|publisher=%s|type=%s|category=%s|icon_large=%s|icon_medium=%s|icon_small=%s|apk=%s|size=%s\n", new Object[] {
//                          id, name, ver, contentversion, lastUpdate, pId, des, publisher, type, category, icon_large,
//                          icon_medium, icon_small, apk, size
//        });
//        System.out.printf("lastUpdate=%s|size=%s\n|listBuilding=%", new Object[] {
//                          id, name, ver, contentversion, lastUpdate, pId, des, publisher, type, category, icon_large,
//                          icon_medium, icon_small, apk, size
// });
    }

    public String toString() {
//        return String.format("id=%s|name=%s|ver=%s|ctnver=%s|lastUpdate=%s|pId=%s|des=%s|publisher=%s|type=%s|category=%s|icon_large=%s|icon_medium=%s|icon_small=%s|apk=%s|size=%s\n", new Object[] {
//                             id, name, ver, contentversion, lastUpdate, pId, des, publisher, type, category, icon_large,
//                             icon_medium, icon_small, apk, size
//    });
//    }
           return "";                      
}
    public void addBuilding(String type, int posX, int posY, int level,String status) throws Exception {
        
        Building building = new Building(this.size_building,type,level,posX,posY, status);                
        this.size_building++;
        this.listBuilding.add(building);
        System.out.println("add them building "+ building.type+", time start: "+building.timeStart+", status ="+building.status);
    }
    public MapArray getMapArray(){
        MapArray mapArray = new MapArray();
        //System.out.println(">>>>>listBuilding:"+ this.listBuilding.toString());
        
        
        for (Building building : this.listBuilding) {
            if (! building.status.equals(ServerConstant.destroy_status)){
                mapArray.addBuilding(this,building.id,building.posX,building.posY);
            }
            //System.out.println(">>>>>mamama:"+ building.type);
            
            System.out.println(building.id+" "+building.posX + " "+building.posY);
        }
        
        for (Obs obs : this.listObs) {
            if (! obs.status.equals(ServerConstant.destroy_status)){
            //System.out.println(">>>>>mamama:"+ obs.type);
                mapArray.addObs(this,obs.id,obs.posX,obs.posY);
            }
            //System.out.println(building.id+" "+building.posX + " "+building.posY);
        }
        
//        System.out.println(">>>>>MAP ARRAY:");
//        for (int i=0;i<40;i++){
//            for(int j=0;j<40;j++){
//                System.out.print(mapArray.arr[i][j]+"-");   
//            }
//            System.out.println("----------------");  
//        }
        
        return mapArray;
    }
    private boolean check(int id,List<Integer> list){
        if (list.size()==0) return true;
        for(int i:list){
//            System.out.println("id="+id);
//            System.out.println("i="+i);
//            System.out.println("list.get(i)="+list.get(i));
            if (id==i){
                return false;
            }
        }
        return true;
    }


    public int getBuilderNotFree() {
        checkStatus();
        int kq = 0;
        for (Building building : this.listBuilding){
            System.out.println("check status - building"+building.type+" , status = "+building.status+ "time_Start = "+ building.timeStart);
            if (building.status.equals("pending")|| building.status.equals("upgrade")){
                kq++;
            }
        }
        System.out.println("check status - so tho xay dang lam la:"+kq);
        return kq;
    }

    public int getGToReleaseBuilder() {
        short dd = 0;
        long kq = 999999999;
        for (Building building : this.listBuilding){
            if (building.status.equals("pending")|| building.status.equals("upgrade")){
                if (dd==0){
                    dd++;
                    kq = building.getTimeConLai(building.status);
                }
                else {
                    kq = Math.min(kq,building.getTimeConLai(building.status));
                }
            }
            System.out.println("kq nha giai phong = "+kq);
        }
        
        if (kq ==999999999) {
            return -1;
        }
        else {
            
            return timeToG(kq);
        }
    }

    private int timeToG(long time) { 
        System.out.println(">>>>>Thoi gian release la : "+ time/60000);
        Date date = new Date(time);
            // formattter
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            // Pass date object
            String formatted = formatter.format(date);
            System.out.println("Result: " + formatted);
        
            long minute = (long) Math.floor(time / 60000);
            
        System.out.println("time1="+minute);
        System.out.println("time2="+time % 60000);
            if ( time%60000>0){
                minute++;
            }
        
        
        return (int)(minute);
    }

    public void releaseBuilding() {
        System.out.println(">>>>>releaseBuilding");
        long time = 999999999;
        int kq =-1;
        for (Building building : this.listBuilding){
                if (building.status.equals("pending")|| building.status.equals("upgrade")){
                    //linhrafa neu dang upgrade thi tang level
                    if (time>building.getTimeConLai(building.status)){
                        time = building.getTimeConLai(building.status);
                        kq = building.id;
                    }
                }
        }
        System.out.println(".Building ddc release la: "+ this.listBuilding.get(kq).type+ "time_con_lai= "+ time);
        if (this.listBuilding.get(kq).getStatus().equals("upgrade")){
            this.listBuilding.get(kq).level = this.listBuilding.get(kq).level +1;
            this.listBuilding.get(kq).setStatus("complete");
        } 
        else if (this.listBuilding.get(kq).getStatus().equals("pending")){
            this.listBuilding.get(kq).setStatus("complete");
        }
    }

    public void checkStatus() {
        System.out.println("***********checkbuilding **********************");
        for (Building building : this.listBuilding){
            if ( !building.status.equals(ServerConstant.destroy_status)){
                
                long time_cur = System.currentTimeMillis();
                long distance = time_cur - building.timeStart;
                long time_xay = building.getTimeBuild(building.status);
                
                System.out.println("distance = "+ distance+", time_can_xay="+time_xay);
                
                if ((!building.status.equals("complete")) &&(distance > time_xay) && building.timeStart!=-1){
                    if (building.status.equals("upgrade")){
                        building.level ++;
                    }
                    building.setStatus("complete");
                    
                }
            }
            //System.out.println(building.type+" "+"time start: "+building.timeStart+" "+"distance: "+distance+"status "+building.status);
        }
    }
    
    public void upgradeBuilding(int _id){
        this.listBuilding.get(_id).status = "upgrade";
        this.listBuilding.get(_id).timeStart = System.currentTimeMillis();
    }
    
    public void print(){
        System.out.println("***********in list building **********************");
        for (Building building : this.listBuilding){
            System.out.println(building.type+" "+"time start: "+building.timeStart+" "+"status "+building.status+"level: "+building.level);
        }
    }

    public int getRequire(String capacity_type) {
        for (Building building: this.listBuilding){
            if (building.type.equals(ServerConstant.town)){
                try {
                    JSONObject town = ServerConstant.config.getJSONObject(ServerConstant.town).getJSONObject(Integer.toString(building.level));
                    return(town.getInt(capacity_type));
                    
                } catch (JSONException e){
                    
                    return 0;
                }     
                
            }
        }
        return 0;
        
    }
}
