package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import java.util.Map;
import java.util.Queue;

import model.Guild;

import model.ZPUserInfo;

import util.server.ServerConstant;

public class ResponseGetGuildListMemberInfo extends BaseMsg {    
    Guild guild;    
    
    public ResponseGetGuildListMemberInfo(Guild guild) {
        super(CmdDefine.GET_GUILD_LISTMEMBER_INFO);        
        this.guild = guild;
    }  
    @Override
    public byte[] createData() {        
        ByteBuffer bf = makeBuffer();
        
        bf.putInt(guild.list_member.size());                
        for(Map.Entry<Integer, Short> member : guild.list_member.entrySet()) {
            Integer id_member = member.getKey();
            Short position = member.getValue();            
            String name = "";
            ZPUserInfo userInfo = null;
            try {
                userInfo = (ZPUserInfo) ZPUserInfo.getModel(id_member, ZPUserInfo.class);
                name = userInfo.getName();
            } catch (Exception e) {
                
            }
            bf.putInt(id_member);
            putStr(bf,name);
            bf.putShort(position);
            bf.putInt(userInfo.getDanhVong());
        }
        
        return packBuffer(bf);
    }
}