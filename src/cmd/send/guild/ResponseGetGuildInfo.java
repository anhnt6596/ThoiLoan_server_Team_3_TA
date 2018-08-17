package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import java.util.Queue;

import model.Guild;

import util.server.ServerConstant;

public class ResponseGetGuildInfo extends BaseMsg {    
    Guild guild;    
    
    public ResponseGetGuildInfo(Guild guild) {
        super(CmdDefine.GET_GUILD_INFO);        
        this.guild = guild;
    }  
    @Override
    public byte[] createData() {        
        ByteBuffer bf = makeBuffer();
        bf.putInt(guild.id);
        putStr(bf,guild.name);                
        bf.putShort(guild.status);
        bf.putInt(guild.level);
        bf.putInt(guild.list_member.size());
        bf.putInt(guild.danh_vong);
        bf.putInt(guild.danh_vong_require);
        return packBuffer(bf);
    }
}