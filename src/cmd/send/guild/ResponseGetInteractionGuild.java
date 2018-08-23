package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import java.util.Iterator;
import java.util.LinkedList;

import java.util.List;

import java.util.Map;

import model.GuildBuilding;
import model.MessageGuild;
import model.TroopGuild;

import model.train.TroopInBarrack;


public class ResponseGetInteractionGuild extends BaseMsg {
    GuildBuilding guildBuilding;
    List<MessageGuild> list_message;
    Map <Integer, Short> list_member_online;
    
    public ResponseGetInteractionGuild(GuildBuilding _guildBuilding, List<MessageGuild> _list_message, Map <Integer, Short> _list_member_online) {
        super(CmdDefine.GET_INTERACTION_GUILD);
        guildBuilding = _guildBuilding;
        list_message = _list_message;
        list_member_online = _list_member_online;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putLong(guildBuilding.lastRequestTroopTimeStamp);
        
        int sizeTroopGuildList = guildBuilding.troopGuildList.size();
        bf.putInt(sizeTroopGuildList);
        
        TroopGuild troop;
        Iterator<TroopGuild> i = guildBuilding.troopGuildList.iterator();
        while (i.hasNext()) {
            troop = i.next();
            putStr(bf, troop.name);
            bf.putShort(troop.level);
        }
        
        int sizeMessageList = list_message.size();
        bf.putInt(sizeMessageList);
        
        MessageGuild mess;
        Iterator<MessageGuild> a = list_message.iterator();
        while (a.hasNext()) {
            mess = a.next();
            bf.putShort(mess.type);
            bf.putInt(mess.id_user);
            putStr(bf, "Fresher_" + Integer.toString(mess.id_user));
            putStr(bf, mess.content);
            bf.putLong(mess.timeStamp);
            bf.putInt(mess.currentCapacityTroop);
            bf.putInt(mess.guildCapacityAtTime);
        }
        
        int sizeMemberList = list_member_online.size();
        bf.putInt(sizeMemberList);
        for (int idUser : list_member_online.keySet()) {
            short valueOnline = list_member_online.get(idUser);
            bf.putInt(idUser);
            putStr(bf, "Fresher_" + Integer.toString(idUser));
            bf.putShort(valueOnline);
        }
        
        return packBuffer(bf);
    }
}
