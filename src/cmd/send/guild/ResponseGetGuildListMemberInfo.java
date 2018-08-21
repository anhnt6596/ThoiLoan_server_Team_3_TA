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
    private Guild guild;    
    private short validate;
    
    public ResponseGetGuildListMemberInfo(short validate, Guild guild) {
        super(CmdDefine.GET_GUILD_LISTMEMBER_INFO);        
        this.guild = guild;
        this.validate = validate;
    }  
    @Override
    public byte[] createData() {     
        ByteBuffer bf = makeBuffer();
        if (this.validate == ServerConstant.SUCCESS ){
            bf.putInt(guild.list_member.size());
            //System.out.println("sizeeeeeeeeeeeeeeeeee "+guild.list_member.size());
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
                //System.out.println("sizeeeeeeeeeeeeeeeeee_id "+id_member);
                putStr(bf,name);
                bf.putShort(userInfo.donate_troop); //donate troop
                bf.putShort(userInfo.request_troop); //request troop
                bf.putShort(position);
                bf.putInt(userInfo.getDanhVong());
            }
        }
        else {
            bf.putInt(0);
        }
        
        
        return packBuffer(bf);
    }
}