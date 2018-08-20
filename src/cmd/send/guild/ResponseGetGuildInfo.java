package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import java.util.Map;
import java.util.Queue;

import model.Guild;

import model.ZPUserInfo;

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
        bf.putInt(guild.logo_id);
        bf.putShort(guild.status);
        bf.putInt(guild.level);
        bf.putInt(guild.danh_vong);
        bf.putInt(guild.danh_vong_require);
        return packBuffer(bf);
    }
}