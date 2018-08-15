package model;

import bitzero.util.common.business.CommonHandle;
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
import java.util.LinkedList;
import java.util.List;

import java.util.Map;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONObject;

import util.database.DataModel;

import util.server.ServerConstant;


public class Guild extends DataModel {    
    public int id;
    public String name;
    public int uranium;
    public String description;
    public int exp;
    public int level;
    public int logo_id;
    public Queue<MessageGuild> list_message = new LinkedList<MessageGuild>();
    public Map <Integer, String> list_require = new HashMap<Integer, String>();
    public Map <Integer, Short> list_member = new HashMap<Integer, Short>();

    
    

    public Guild(int id_user, String _name, int _id, int _logo_id) {
        super();
        this.id = _id;
        this.level = 1;
        this.exp = 0;
        this.uranium = 0;
        this.description = "";
        this.logo_id = _logo_id;
        addMember(id_user, ServerConstant.guild_leader);
        
        
    }
    
    public String toString() {
        return String.format("%s|%s", new Object[] { id, name });
    }

    private void addMember(int _id_user, short _position) {
        this.list_member.put(_id_user, _position);
        ZPUserInfo user;
        try {
            user = (ZPUserInfo) ZPUserInfo.getModel(_id_user, ZPUserInfo.class);
            if (user == null) {
                //send response error
            }
        } catch (Exception e) {
            
        }
        
        
    }
}
