package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import model.ZPUserInfo;

import util.server.ServerConstant;

public class ResponseRemoveMember extends BaseMsg {
    private short type;
    private ZPUserInfo member;
    private short validate;

    public ResponseRemoveMember(short validate,ZPUserInfo member) {
        super(CmdDefine.REMOVE_MEMBER);        
        this.member = member;
        this.validate = validate;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        System.out.println("this.validate = "+ this.validate);
        bf.putShort(this.validate);
        System.out.println("this.member.id = "+ this.member.id);
        bf.putInt(this.member.id);        
        
        return packBuffer(bf);
    }
}