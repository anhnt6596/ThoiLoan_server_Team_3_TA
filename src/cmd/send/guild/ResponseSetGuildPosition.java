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

    public ResponseSetGuildPosition(short validate, int id , short type_position ) {
        super(CmdDefine.SET_GUILD_POSITION);          
        this.validate = validate;
        this.id = id;
        this.type_position = type_position;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(this.validate);
        bf.putInt(this.id);
        bf.putShort(this.type_position);
                
        return packBuffer(bf);
    }
}
