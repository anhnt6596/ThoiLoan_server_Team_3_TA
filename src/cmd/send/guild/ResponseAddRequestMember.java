package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import model.ZPUserInfo;

import util.server.ServerConstant;

public class ResponseAddRequestMember extends BaseMsg {    
    private ZPUserInfo member;
    private short validate;
    

    public ResponseAddRequestMember(short validate, ZPUserInfo member) {
        super(CmdDefine.ADD_REQUEST_MEMBER);        
        this.member = member;
        this.validate = validate;
    }  

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(this.validate);
        bf.putInt(member.id);
        //putStr(bf,member.name);
        
        return packBuffer(bf);
    }
}