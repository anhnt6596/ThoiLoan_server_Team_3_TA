package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import model.ZPUserInfo;

import util.server.ServerConstant;


public class ResponseSetGuildLeader extends BaseMsg {
    private short type;    
    private short validate;
    private int id;

    public ResponseSetGuildLeader(short type, int id , short validate) {
        super(CmdDefine.SET_GUILD_LEADER);
        this.id = type;        
        this.validate = validate;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        if (this.type == ServerConstant.VALIDATE){
            bf.putShort(this.validate);    
        }
        else { //gui cho tat ca moi nguoi khac trong guild
            bf.putInt(this.id);            
        }
        
        return packBuffer(bf);
    }
}
