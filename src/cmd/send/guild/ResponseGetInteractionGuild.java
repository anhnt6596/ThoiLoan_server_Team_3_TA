package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import java.util.Iterator;
import java.util.LinkedList;

import java.util.List;

import model.GuildBuilding;
import model.MessageGuild;
import model.TroopGuild;


public class ResponseGetInteractionGuild extends BaseMsg {
    GuildBuilding guildBuilding;
    List<MessageGuild> list_message;
    
    public ResponseGetInteractionGuild(GuildBuilding _guildBuilding, List<MessageGuild> _list_message) {
        super(CmdDefine.GET_INTERACTION_GUILD);
        guildBuilding = _guildBuilding;
        list_message = _list_message;
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
            putStr(bf, mess.content);
            bf.putLong(mess.timeStamp);

        }
        
        return packBuffer(bf);
    }
}
