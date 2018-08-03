package model;

import bitzero.util.common.business.CommonHandle;

import cmd.obj.map.Obs;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import util.database.DataModel;

import util.server.ServerConstant;


public class TroopInfo extends DataModel {
    public short size;
    public Map <String, Troop> troopMap = new HashMap<String, Troop>();
    public TroopInfo() {
        super();
        this.initListTroop();
    }

    public void initListTroop() {
        String path = System.getProperty("user.dir")+"/conf/";
        System.out.println("Working Directory = " + path);
        
        StringBuffer contents = new StringBuffer();
        
        try {
            File file = new File(path+"Config_json/TroopBase.json");
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
            ServerConstant.configTroopBase = new JSONObject(contents.toString());
            Iterator<?> keys = ServerConstant.configTroopBase.keys();
            while (keys.hasNext()){
                String key = (String) keys.next();
                this.troopMap.put(key, new Troop(key, (short) 0, (short) 1, (short) 0, "completed"));
                this.size++;
            }
        } catch (Exception e){
            CommonHandle.writeErrLog(e);
        }
    }
    public short getSize() {
        return this.size;
    }
}

