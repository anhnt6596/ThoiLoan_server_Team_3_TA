package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseDenyRequestMember extends BaseMsg {
    private short validate;

    public ResponseDenyRequestMember(short s) {
        super(CmdDefine.DENY_REQUEST_MEMBER);
        this.validate = s;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putShort(this.validate);
        return packBuffer(bf);
    }
}
