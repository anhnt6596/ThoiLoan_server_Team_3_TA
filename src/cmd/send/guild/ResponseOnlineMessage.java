package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseOnlineMessage extends BaseMsg {
    int userId;
    short onlineValue;
    
    public ResponseOnlineMessage(int _userId, short _onlineValue) {
        super(CmdDefine.ONLINE_MESSAGE);
        userId = _userId;
        onlineValue = _onlineValue;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putInt(userId);
        bf.putShort(onlineValue);
        return packBuffer(bf);
    }
}
