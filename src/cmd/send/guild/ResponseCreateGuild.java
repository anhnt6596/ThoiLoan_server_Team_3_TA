package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;
import java.nio.ByteBuffer;

import model.TroopInfo;

public class ResponseCreateGuild extends BaseMsg {
    private short validate;

    public ResponseCreateGuild(short s) {
        super(CmdDefine.CREATE_GUILD);
        this.validate = s;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putShort(this.validate);
        return packBuffer(bf);
    }
}