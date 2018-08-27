package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseOnlineMessage extends BaseMsg {
    private int userId;
    private short onlineValue;
    
    public ResponseOnlineMessage(int userId, short onlineValue) {
        super(CmdDefine.ONLINE_MESSAGE);
        this.userId = userId;
        this.onlineValue = onlineValue;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putInt(this.userId);
        bf.putShort(this.onlineValue);
        return packBuffer(bf);
    }
}
