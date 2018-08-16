package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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

    
    

    public Guild(int id_user, String _name, int _logo_id) {
        super();
        this.id = id_user;
        this.name = _name;
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
    //bang hoi them nguoi
    public void addMember(int _id_user, short _position) {
        this.list_require.remove(_id_user);
        this.list_member.put(_id_user, _position);
    }
    //bang hoi loai bo member
    public void removeMember(int _id_user){
        this.list_member.remove(_id_user);
    }
    public void addRequestMember(int _id_user, String _name){
        this.list_require.put(_id_user, _name);
    }
    public void removeRequestMember(int _id_user){
        this.list_require.remove(_id_user);
    }
    public boolean checkListRequire(int id){
        return true;
    }
    public boolean checkListmember(int id){
        return true;
    }
}
