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


public class Troop extends DataModel {
    public String type;
    public short isUnlock;
    public short level;
    public short population;
    public long startTime;
    public String status;
    public Troop(String _type, short _isUnlock, short _level, short _population, String _status) {
        super();
        this.type = _type;
//        System.out.println("this.type sau khi: " + this.type);
        this.isUnlock = _isUnlock;
        this.level = _level;
        this.population = _population;
        this.startTime = 0L;
        this.status = _status;
    }
    public void levelUp() {
        this.level = (short) (this.level + 1);
        this.setStatus("completed");
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}

