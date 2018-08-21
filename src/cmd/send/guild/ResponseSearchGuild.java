package cmd.send.guild;

import java.util.Queue;

import model.Guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import util.server.ServerConstant;

public class ResponseSearchGuild extends BaseMsg {    
    Queue<Guild> list_suggest_guild;
    short validate;
    
    public ResponseSearchGuild(short s, Queue<Guild> q) {
        super(CmdDefine.SEARCH_GUILD_INFO);
        this.validate = s;
        this.list_suggest_guild = q;
    }  
    @Override
    public byte[] createData() {        
        ByteBuffer bf = makeBuffer();
        if (this.validate == ServerConstant.ERROR){
            bf.putInt(0);
            
        } 
        else if (this.validate == ServerConstant.SUCCESS){
            
            int listSize = this.list_suggest_guild.size();
            bf.putInt(listSize);
            System.out.println("Co "+listSize+" bang duoc tim thay");
            for (int i=0;i<listSize;i++){
                Guild guild = this.list_suggest_guild.poll();
                bf.putInt(guild.id);
                putStr(bf,guild.name);
                bf.putInt(guild.logo_id);
                bf.putShort(guild.status);
                bf.putInt(guild.level);
                bf.putInt(guild.list_member.size());
                bf.putInt(guild.danh_vong);
                bf.putInt(guild.danh_vong_require);
            }
        }
       
        return packBuffer(bf);
    }
}
