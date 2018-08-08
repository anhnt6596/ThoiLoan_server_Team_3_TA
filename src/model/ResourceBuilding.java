package model;

import bitzero.util.common.business.CommonHandle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import util.database.DataModel;

import util.server.ServerConstant;


public class ResourceBuilding extends DataModel {
    public short size;    
    public ResourceBuilding() {
        super();
        this.initresourceBuildingMap();
    }

    public void initresourceBuildingMap() {
        try {            
            Iterator<?> keys = ServerConstant.configResource.keys();
            while (keys.hasNext()){
                String key = (String) keys.next();                
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


