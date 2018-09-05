package model;

import bitzero.server.entities.User;

import bitzero.util.ExtensionUtility;

import cmd.send.guild.ResponseSearchGuild;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import java.util.Random;

import util.database.DataModel;

import util.server.ServerConstant;


public class Guild extends DataModel implements Comparable<Guild> {    
    public int id;
    public String name;
    public int uranium = 0;
    public int danh_vong;
    public int danh_vong_require;
    public String description = "";
    public Short status; //open 0, close 1, confirm 2;
    public int exp = 0;
    public int level;
    public int logo_id;
    public List<MessageGuild> list_message = new LinkedList<MessageGuild>();
    public Map <Integer, String> list_require = new HashMap<Integer, String>(); //id, name
    public Map <Integer, Short> list_member = new HashMap<Integer, Short>(); 

    public int getId() {
        return id;
    }

    public void setDanh_vong_require(int danh_vong_require) {
        this.danh_vong_require = danh_vong_require;
    }

    public int getDanh_vong_require() {
        return danh_vong_require;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Short getStatus() {
        return status;
    }
    
    public Guild(int id_user, int guild_id, String _name, int _logo_id, short _status, int _danh_vong_require, String _description) {
        super();
        this.id = guild_id;
        this.name = _name;
        this.level = 1;
        this.exp = 0;
        this.uranium = 0;
        this.status = _status;
        //this.danh_vong = 0;
        Random rand = new Random();
        this.danh_vong = rand.nextInt(5000) + 1;
        
        this.danh_vong_require = _danh_vong_require;
        this.description = _description;
        this.logo_id = _logo_id;
        addMember(id_user, ServerConstant.guild_leader);
        
    }
    
    public String toString() {
        return String.format("%s|%s", new Object[] { id, name });
    }
    //bang hoi them nguoi
    public void addMember(int _id_user, short _position) {
        String position = this.list_require.get(_id_user);
        if (position!= null){
            this.list_require.remove(_id_user);
        }
        System.out.println("them nguoi co id: "+_id_user + " guild_status_open, id= "+ this.id);
        this.list_member.put(_id_user, _position);    
        System.out.println("so mem ber la: "+ this.list_member.size());
    }
    
    public void addMessage(MessageGuild message){
        //Neu la type ask troop thi xoa tin nhan ask troop trc do neu co
        if(message.type == ServerConstant.ASK_TROOP){
            removeMessageRequestTroop(message.id_user);
            updateLastAskTroopTimeStamp(message.id_user);
        }  
        if(list_message.size() >= ServerConstant.MAX_MESSAGES_QUEUE){
            list_message.remove(0);
        }
        list_message.add(message);
    }
    
    //Xoa message xin quan cu neu co request xin quan moi hoac full quan
    public void removeMessageRequestTroop(int userId) {
        MessageGuild mess;
        Iterator<MessageGuild> i = list_message.iterator();
        while (i.hasNext()) {
            mess = i.next();
            if(mess.type == ServerConstant.ASK_TROOP && mess.id_user == userId){
                int index = list_message.indexOf(mess);
                list_message.remove(index);
                break;
            }
        }         
    }

    
    //Cap nhat lai current troop capacity trong message xin quan
    public void updateRequestTroopMessage(int userId, int increaseCapacity) {
        MessageGuild mess;
        Iterator<MessageGuild> i = list_message.iterator();
        while (i.hasNext()) {
            mess = i.next();
            if(mess.type == ServerConstant.ASK_TROOP && mess.id_user == userId){
                mess.currentCapacityTroop += increaseCapacity;
                break;
            }
        }         
    }
    
    private void updateLastAskTroopTimeStamp(int userId) {
        ZPUserInfo userInfo;
        try {
            userInfo = (ZPUserInfo) ZPUserInfo.getModel(userId, ZPUserInfo.class);
        } catch (Exception e) {
            return;
        }
        
        userInfo.setLast_time_ask_for_troops(System.currentTimeMillis());

        try {
            userInfo.saveModel(userId);
        } catch (Exception e) {
        }
    }
    
    //bang hoi loai bo member
    public void removeMember(int _id_user){
        this.list_member.remove(_id_user);
    }
    public void addRequestMember(int _id_user, String _name){
        this.list_require.put(_id_user, _name);       
         
    }
    public short getPosition(int id){
        return this.list_member.get(id);
    }
    public void removeRequestMember(int _id_user){
        this.list_require.remove(_id_user);
    }
    public boolean checkListRequire(int id){
        //return true;       
        for(Map.Entry<Integer, String> member : this.list_require.entrySet()) {
            Integer id_member = member.getKey();
            String position = member.getValue();   
            if (id_member == id){
                return false;
            }
                }
        return true;
    }
//    public boolean checkListmember(int id){
//        return true;
//    }

    public int getIdLeader() {
        for(Map.Entry<Integer, Short> member : list_member.entrySet()) {
            Integer id_member = member.getKey();
            Short position = member.getValue();
            if (position == ServerConstant.guild_leader){
                return id_member;
            }
        }
        return -1;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUranium(int uranium) {
        this.uranium = uranium;
    }

    public int getUranium() {
        return uranium;
    }

    public void setDanh_vong(int danh_vong) {
        this.danh_vong = danh_vong;
    }

    public int getDanh_vong() {
        return danh_vong;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getExp() {
        return exp;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLogo_id(int logo_id) {
        this.logo_id = logo_id;
    }

    public int getLogo_id() {
        return logo_id;
    }


    public void setList_require(Map<Integer, String> list_require) {
        this.list_require = list_require;
    }

    public Map<Integer, String> getList_require() {
        return list_require;
    }

    public void setList_member(Map<Integer, Short> list_member) {
        this.list_member = list_member;
    }

    public Map<Integer, Short> getList_member() {
        return list_member;
    }

    @Override
    public int compareTo(Guild guild) {
        if (this.danh_vong < guild.danh_vong) {
            return 1;
        } else if (this.danh_vong > guild.danh_vong) {
            return -1;
        } else {
            return 0;
        }
    }
    public void chuyenBangChu(int member_id){
        //chuyen bang chu thanh bang pho
        int leader_id = this.getIdLeader();
        this.list_member.put(leader_id, ServerConstant.guild_moderator);
        
        //chuyen nguoi duoc chon thanh bang chu
        this.list_member.put(member_id, ServerConstant.guild_leader);
    }
    public void chuyenBangPho (int member_id){
        this.list_member.put(member_id, ServerConstant.guild_moderator);
    }
    public void chuyenThanhVien(int member_id){
        this.list_member.put(member_id, ServerConstant.guild_member);
    }    
    public void BangchuOutBang(){
        //truong hop bang chi co 1 thanh vien
        if (this.list_member.size()==1){
            try {
                ListGuild listGuild = (ListGuild) ListGuild.getModel(1, ListGuild.class);
                listGuild.list_guild.remove(this.id);
            } catch (Exception e) {
                }
        }
        else { //truong hop bang co 2 thanh vien tro len
            int id_nguoiduocchon = 0 ;
            short chucvu_nguoiduocchon = 0;
            int danhvong_nguoiduocchon = 0;
            //chon ra bang pho co danh vong cao nhat    
            for(Map.Entry<Integer, Short> member : list_member.entrySet()) {
                Integer id_member = member.getKey();
                Short position = member.getValue();
                
                try {
                    ZPUserInfo memberInfo = (ZPUserInfo) ZPUserInfo.getModel(id_member, ZPUserInfo.class);
                    int danhvong_member = memberInfo.getDanhVong();
                    if (position > chucvu_nguoiduocchon){
                        id_nguoiduocchon = id_member;
                        chucvu_nguoiduocchon = position;                        
                        danhvong_nguoiduocchon = danhvong_member;
                    }
                    else if (position == chucvu_nguoiduocchon){
                        if (danhvong_nguoiduocchon < danhvong_member){
                            id_nguoiduocchon = id_member;
                            danhvong_nguoiduocchon = danhvong_member;
                        }
                    }
                } catch (Exception e) {
                }
            }
            int id_bangchu_cu = getIdLeader();
            chuyenBangChu(id_nguoiduocchon);
            this.list_member.remove(id_bangchu_cu);
            
        }        
        
        
    }

    public boolean checkIdMod(int id) {
        if (this.list_member.get(id)== ServerConstant.guild_moderator){
            return true;
        }
        return false;
    }
}
