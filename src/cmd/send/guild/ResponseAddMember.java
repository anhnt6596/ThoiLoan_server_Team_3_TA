package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;


public class ResponseAddMember extends BaseMsg {
    private short validate;

    public ResponseAddMember(short s) {
        super(CmdDefine.ADD_MEMBER);
        this.validate = s;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putShort(this.validate);
        return packBuffer(bf);
    }
}
