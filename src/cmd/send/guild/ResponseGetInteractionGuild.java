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

import util.server.ServerConstant;

public class ResponseGetInteractionGuild extends BaseMsg {
    private GuildBuilding guildBuilding;
    private List<MessageGuild> listMessage;
    private Map<Integer, Short> listMemberOnline;
    
    public ResponseGetInteractionGuild(GuildBuilding guildBuilding, List<MessageGuild> listMessage, Map<Integer, Short> listMemberOnline) {
        super(CmdDefine.GET_INTERACTION_GUILD);
        this.guildBuilding = guildBuilding;
        this.listMessage = listMessage;
        this.listMemberOnline = listMemberOnline;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        //List troop guild
        int sizeTroopGuildList = this.guildBuilding.troopGuildList.size();
        bf.putInt(sizeTroopGuildList);
        TroopGuild troop;
        Iterator<TroopGuild> i = this.guildBuilding.troopGuildList.iterator();
        while (i.hasNext()) {
            troop = i.next();
            putStr(bf, troop.name);
            bf.putShort(troop.level);
        }
        
        //List message
        int sizeMessageList = this.listMessage.size();
        bf.putInt(sizeMessageList);
        MessageGuild mess;
        Iterator<MessageGuild> a = this.listMessage.iterator();
        while (a.hasNext()) {
            mess = a.next();
            bf.putShort(mess.type);
            bf.putInt(mess.id_user);
            if(mess.id_user == ServerConstant.ID_SYSTEM){
                putStr(bf, "SYSTEM");
            }else{
                putStr(bf, "Fresher_" + Integer.toString(mess.id_user));
            }
            putStr(bf, mess.content);
            bf.putLong(mess.timeStamp);
            bf.putInt(mess.currentCapacityTroop);
            bf.putInt(mess.guildCapacityAtTime);
        }
        
        //List member
        int sizeMemberList = this.listMemberOnline.size();
        bf.putInt(sizeMemberList);
        for (int idUser : this.listMemberOnline.keySet()) {
            short valueOnline = this.listMemberOnline.get(idUser);
            bf.putInt(idUser);
            putStr(bf, "Fresher_" + Integer.toString(idUser));
            bf.putShort(valueOnline);
        }
        
        //userGotMap
        int sizeUserGotMap = this.guildBuilding.userGotMap.size();
        bf.putInt(sizeUserGotMap);
        for (Integer idUser : this.guildBuilding.userGotMap.keySet()) {
            bf.putInt(idUser);
            bf.putInt(this.guildBuilding.userGotMap.get(idUser));
        }
        
        return packBuffer(bf);
    }
}
