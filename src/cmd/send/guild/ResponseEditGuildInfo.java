package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import model.Guild;

public class ResponseEditGuildInfo extends BaseMsg {
    short validate ;
    public ResponseEditGuildInfo(short s) {
        super(CmdDefine.EDIT_GUILD_INFO);
        this.validate = s;
    }


    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putShort(validate);        
        return packBuffer(bf);
    }
}