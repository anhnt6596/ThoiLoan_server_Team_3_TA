package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import model.ZPUserInfo;

import util.server.ServerConstant;


public class ResponseSetGuildPosition extends BaseMsg {
    private short type;    
    private short type_position;    
    private short validate;
    private int id;

    public ResponseSetGuildPosition(short type, int id , short type_position, short validate) {
        super(CmdDefine.SET_GUILD_POSITION);
        this.id = type;        
        this.validate = validate;
        this.type_position = type_position;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        if (this.type == ServerConstant.VALIDATE){
            bf.putShort(this.validate);    
        }
        else { //gui cho tat ca moi nguoi khac trong guild
            bf.putInt(this.id);  
            bf.putShort(this.type_position);
        }
        
        return packBuffer(bf);
    }
}
