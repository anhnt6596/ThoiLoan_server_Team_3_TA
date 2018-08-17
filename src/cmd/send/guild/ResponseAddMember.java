package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import model.ZPUserInfo;

import util.server.ServerConstant;


public class ResponseAddMember extends BaseMsg {
    private short type;
    private ZPUserInfo member;
    private short validate;

    public ResponseAddMember(short type, ZPUserInfo member, short validate) {
        super(CmdDefine.ADD_MEMBER);
        this.type = type;
        this.member = member;
        this.validate = validate;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        if (this.type == ServerConstant.VALIDATE){
            bf.putShort(this.validate);    
        }
        else {
            bf.putInt(member.id);
            putStr(bf,member.name);
        }
        
        return packBuffer(bf);
    }
}
