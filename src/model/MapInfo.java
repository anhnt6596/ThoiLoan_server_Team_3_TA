package model;

import bitzero.server.entities.User;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Random;

import java.util.TimeZone;

import model.train.BarrackQueue;
import model.train.BarrackQueueInfo;

import model.train.TroopInBarrack;

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
                if (key.equals("CLC_1")){
                    addBuilding(key,house_type.getInt("posX")-1, house_type.getInt("posY")-1,0,"complete");
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
        try {
            checkStatus(null);
        } catch (Exception e) {
        }
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

    public void releaseBuilding(User user) {
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
            
            if(this.listBuilding.get(kq).type.equals("BAR_1") && user != null){
                System.out.println("==============================RELEASE FINISH UPGRADE BAR_1===========================");
                this.changeBarrackQueueInfoWhenBarrackUpgraded(user, this.listBuilding.get(kq).id);
            }
        } 
        else if (this.listBuilding.get(kq).getStatus().equals("pending")){
            this.listBuilding.get(kq).setStatus("complete");
            
            if(this.listBuilding.get(kq).type.equals("BAR_1") && user != null){
                System.out.println("==============================RELEASE FINISH BUILD BAR_1===========================");
                this.changeBarrackQueueInfoWhenBarrackBuilt(user, this.listBuilding.get(kq).id);
            }
        }
    }

    public void checkStatus(User user) {
        System.out.println("***********checkbuilding **********************");
        for (Building building : this.listBuilding){
            if ( !building.status.equals(ServerConstant.destroy_status)){
                
                long time_cur = System.currentTimeMillis();
                long distance = time_cur - building.timeStart;
                long time_xay = building.getTimeBuild(building.status);
                
                System.out.println("distance = "+ distance+", time_can_xay="+time_xay);
                
                if ((!building.status.equals("complete")) &&(distance > time_xay) && building.timeStart!=-1){
                    if (building.status.equals("upgrade")){
                        //Check if BAR_1
                        if(building.type.equals("BAR_1") && user != null){
                            System.out.println("==============================CHECK STATUS FINISH UPGRADE BAR_1===========================");
                            this.changeBarrackQueueInfoWhenBarrackUpgraded(user, building.id);
                        }
                        
                        building.level ++;
                    }else{
                        //Check if BAR_1
                        if(building.type.equals("BAR_1") && user != null){
                            System.out.println("==============================CHECK STATUS FINISH BUILD BAR_1===========================");
                            this.changeBarrackQueueInfoWhenBarrackBuilt(user, building.id);
                        }
                    }
                    building.setStatus("complete");
                }
            }
            //System.out.println(building.type+" "+"time start: "+building.timeStart+" "+"distance: "+distance+"status "+building.status);
        }
    }
    
    private void changeBarrackQueueInfoWhenBarrackUpgraded(User user, int id) {
        BarrackQueueInfo barrackQueueInfo;
        try {
            barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
        } catch (Exception e) {
            System.out.println("======================= Khong get duoc BarrackQueueInfo tu MapInfo ======================");
            return;
        }

        BarrackQueue newBarrackQueue = (BarrackQueue) barrackQueueInfo.barrackQueueMap.get(id);
        newBarrackQueue.barrackLevel++;
        
        //Dat lai startTime cho barrack
        newBarrackQueue.startTime = System.currentTimeMillis() - newBarrackQueue.startTime;
        
        Map <String, TroopInBarrack> newTroopListMap = newBarrackQueue.troopListMap;
        
        JSONObject barrack_1Config;
        try {
            barrack_1Config = ServerConstant.configBarrack.getJSONObject("BAR_1");
        } catch (JSONException e) {
            return;
        }
        try {
            String troopType = barrack_1Config.getJSONObject(Integer.toString(newBarrackQueue.barrackLevel)).getString("unlockedUnit");
            TroopInBarrack troop = new TroopInBarrack(troopType);
            newTroopListMap.put(troopType, troop);                         
        } catch (JSONException e) {
            
        }
        
        barrackQueueInfo.barrackQueueMap.put(id, newBarrackQueue);
        try{
            barrackQueueInfo.saveModel(user.getId());
        }catch (Exception e) {
            System.out.println("======================= Khong save duoc Model BarrackQueueInfo tu MapInfo ======================");
        }
        barrackQueueInfo.print();
    }
    
    private void changeBarrackQueueInfoWhenBarrackBuilt(User user, int id) {
        BarrackQueueInfo barrackQueueInfo;
        try {
            barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
        } catch (Exception e) {
            System.out.println("======================= Khong get duoc BarrackQueueInfo tu MapInfo ======================");
            System.out.println("======================= Loi la " + e);
            return;
        }

        if(barrackQueueInfo == null){
            System.out.println("======================= BarrackQueueInfo null ======================");
            barrackQueueInfo = new BarrackQueueInfo();
        }
        BarrackQueue barrackQueue = new BarrackQueue(1);
        barrackQueueInfo.barrackQueueMap.put(id, barrackQueue);
        try{
            barrackQueueInfo.saveModel(user.getId());
        }catch (Exception e) {
            System.out.println("======================= Khong save duoc Model BarrackQueueInfo tu MapInfo ======================");
            System.out.println("======================= Loi la " + e);
        }
        barrackQueueInfo.print();
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

    public int getRequire(String capacity_type, String sto_type) {
        int ans =0;
        for (Building building: this.listBuilding){
            if (building.type.equals(ServerConstant.town)){
                try {
                    JSONObject town = ServerConstant.config.getJSONObject(ServerConstant.town).getJSONObject(Integer.toString(building.level));
                    ans +=(town.getInt(capacity_type));
                    
                } catch (JSONException e){
                    
                    return 0;
                }     
                
            }
            if (building.type.equals(sto_type)&& (building.status.equals(ServerConstant.complete_status) || building.status.equals(ServerConstant.upgrade_status)) ){
                try {
                    JSONObject town = ServerConstant.config.getJSONObject(sto_type).getJSONObject(Integer.toString(building.level));
                    ans +=(town.getInt("capacity"));
                    
                } catch (JSONException e){
                    
                    return 0;
                }     
                
            }
        }
        return ans;
        
    }
}
