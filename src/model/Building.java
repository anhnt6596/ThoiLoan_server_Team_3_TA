package model;

import java.sql.Time;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;

import org.json.JSONObject;

import util.database.DataHandler;

import util.database.DataModel;

import util.server.ServerConstant;
import util.server.ServerUtil;

public class Building{
    public int id;
    public String status = "complete"; //complete, pending, upgrade
    public int timebuild =0;
    public long timeStart =0;
    public String type;
    public int level;
    public int posX;
    public int posY;
    
    public Building() {
        super();
        //this.timeStart = System.currentTimeMillis();
    }
    public Building(int _id,String _type, int _level_building, int _posX, int _posY, String _status ) {
        super();
        this.id = _id;
        this.type = _type;
        this.level = _level_building;
        this.posX = _posX;
        this.posY = _posY;
        this.timeStart = System.currentTimeMillis();        
        this.status = _status;
    }
    
    public int getId(){
        return this.id;
    }

    public void setPos(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    long getTimeConLai(String status) {
        long time_cur = System.currentTimeMillis();
        long time_da_chay = time_cur-this.timeStart;
        System.out.println("time_cur"+time_cur+" this.timeStart= "+this.timeStart+ " time can xay: "+this.getTimeBuild(status));
        return (this.getTimeBuild(status)-time_da_chay);
        //return 0L;
        
    }

    public void setStatus(String string) {
        this.status = string;
    }
    public long getTimeBuild(String status) {
        if (this.type.equals("BDH_1")){
            return 0;
        }
        try {
            int level = this.level;
            if (status.equals("upgrade")){
                level ++;
            }
            JSONObject construction = ServerConstant.config.getJSONObject(this.type).getJSONObject(Integer.toString(level));
            return ( (long) construction.getInt("buildTime")*1000);
        } catch (JSONException e){
            System.out.println("get buildTime error");
            return 0;
        }
    }

    public String getStatus() {
        return this.status;
    }
    public int getGtoQuickFinish(){
        int g = 0;
        long kq = this.getTimeConLai(this.status); 
        return timeToG(kq);
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

    public int getGtoCancle(String resource_type) {
        
        int level = this.level;
        if (status.equals("upgrade")){
            level ++;
        }
        try {
            JSONObject construction = ServerConstant.config.getJSONObject(this.type).getJSONObject(Integer.toString(level));
            return(construction.getInt(resource_type)/2);
            
        } catch (JSONException e){
            
            return 0;
        }        
    }
    public void setStartTime() {
            this.timeStart = System.currentTimeMillis();
    }
}


