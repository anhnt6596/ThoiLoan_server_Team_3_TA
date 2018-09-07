package cmd.send.harvest;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;
import java.awt.Point;
import java.nio.ByteBuffer;

public class ResponseDoHarvest extends BaseMsg {
//    TroopInfo troopInfo;
    private short validate;
    private String type;
    private int sanluong;

    public ResponseDoHarvest(short s, String type, int sanluong) {
        super(CmdDefine.DO_HARVEST);
        this.validate = s;
        this.type = type;
        this.sanluong = sanluong;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putShort(this.validate);
        putStr(bf,this.type);
        bf.putInt(this.sanluong);
        return packBuffer(bf);
    }
}

