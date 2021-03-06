package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import model.ZPUserInfo;

public class ResponseRequestUserInfo extends BaseMsg {
    public ZPUserInfo info;
    
    public ResponseRequestUserInfo(ZPUserInfo _info) {
        super(CmdDefine.GET_USER_INFO);
        info = _info;        
    }

   
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putInt(info.id);
        putStr(bf, info.name);
        bf.putLong(info.exp);
        bf.putInt(info.coin);
        bf.putInt(info.gold);
        bf.putInt(info.elixir);
        bf.putInt(info.darkElixir); 
        bf.putInt(info.builderNumber);                  
        bf.putLong(System.currentTimeMillis());
        
        putBoolean(bf, info.is_in_guild);
        if (info.is_in_guild){
            bf.putInt(info.id_guild);
            putStr(bf, info.name_guild);
            bf.putInt(info.id_logo_guild);
            bf.putLong(info.last_time_ask_for_troops);
            bf.putLong(info.last_time_left_guild);
        }
        return packBuffer(bf);
    }
}
