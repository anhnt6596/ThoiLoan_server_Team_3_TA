package model;

import bitzero.util.common.business.CommonHandle;




import java.awt.Point;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

import java.io.UnsupportedEncodingException;

import java.io.Writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import util.database.DataModel;

import util.server.ServerConstant;


public class ZPUserInfo extends DataModel {
    // Zing me
    public int id;
    public String name;
    public long exp = 0L;
    //public int level = 0;
    public int gold = 0;
    public int coin = 1000;
    public int elixir = 0;
    public int darkElixir = 0; 
    public int builderNumber ;

    public boolean is_in_guild = false;
    public int id_guild = -1;
    public String name_guild = "";
    public int id_logo_guild = -1;
    public int last_time_ask_for_troops = 0;
    
    public ZPUserInfo(int _id, String _name) {
        super();
        id = _id;
        name = _name;        
        InitJsonData();        
    }
    
    public String toString() {
        return String.format("%s|%s", new Object[] { id, name });
    }
    
    private void InitJsonData() {
        String path = System.getProperty("user.dir")+"/conf/";
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
                   
            JSONObject player = ServerConstant.configInitGame.getJSONObject("player");
            JSONObject obs = ServerConstant.configInitGame.getJSONObject("obs");
            System.out.println("NUGGGGGGGGGGGGGGG");    
            this.gold = player.getInt("gold");
            System.out.println(this.gold); 
            this.coin = player.getInt("coin");
            System.out.println(this.coin); 
            this.elixir = player.getInt("elixir");
            System.out.println(this.elixir); 
            this.darkElixir = player.getInt("darkElixir");
            System.out.println(this.darkElixir); 
            this.builderNumber = player.getInt("builderNumber"); 
            System.out.println(this.builderNumber);             
            
            
            
            Writer w = new OutputStreamWriter(new FileOutputStream(path+"configplayerInitGame.txt"),"UTF-8");
            BufferedWriter fout = new BufferedWriter(w);
            fout.write(ServerConstant.configInitGame.toString());
            fout.close();
            
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    
    public void reduceUserResources(int gold, int elixir, int darkElixir, int coin, String type, boolean isAdd ){
        //tru gold
        if (this.gold < gold){
            this.gold = 0;
            System.out.println("het gold");
        }
        else {
            this.gold = this.gold - gold;
            System.out.println("gold bi tru di " + gold+ ", user con lai +"+ this.gold+" gold");
        }
        //tru elixir
        if (this.elixir < elixir){
            this.elixir = 0;
            System.out.println("het elixir");
            
        }
        else {
            this.elixir = this.elixir - elixir;
            System.out.println("elixir bi tru di " + elixir + ", user con lai +"+ this.elixir+" elixir");
        }
        if (this.darkElixir < darkElixir){
            
            this.darkElixir = 0;
            System.out.println("het darkElixir");
        }
        else {
            this.darkElixir = this.darkElixir - darkElixir;
            System.out.println("darkElixir bi tru di " + darkElixir + ", user con lai +"+ this.darkElixir+" darkElixir");
        }
        
        this.coin = this.coin - coin;
        System.out.println("coin bi tru di " + coin + ", user con lai +"+ this.coin+" coin");
        
        if (type.equals("BDH_1") ){
            this.builderNumber = this.builderNumber + 1;
        }
        
    }


    public void addResource(int _gold, int _elixir, int _darkElixir, int _coin, int gold_rq, int elx_rq, int dElx_rq) {
        this.gold = this.gold + _gold;
        if (this.gold>gold_rq) {
            this.gold = gold_rq;
        }
        this.elixir = this.elixir + _elixir;
        if (this.elixir>elx_rq) {
            this.elixir = elx_rq;
        }
        this.darkElixir = this.darkElixir + _darkElixir;
        if (this.darkElixir>dElx_rq){
            this.darkElixir = dElx_rq;
        }
        this.coin = this.coin + _coin;
    }
    
    public void addGuildInfo(int _id_guild, String _name_guild, int _id_logo_guild){
        this.is_in_guild = true;
        this.id_guild = _id_guild;
        this.name_guild = _name_guild;
        this.id_logo_guild = _id_logo_guild;
    }
    public void leftGuild(){
        this.is_in_guild = false;
    }

    public void getId() {
    }
}
