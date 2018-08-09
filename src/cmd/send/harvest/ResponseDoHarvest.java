package cmd.send.harvest;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;
import java.awt.Point;
import java.nio.ByteBuffer;

public class ResponseDoHarvest extends BaseMsg {
//    TroopInfo troopInfo;
    private short validate;

    public ResponseDoHarvest(short s) {
        super(CmdDefine.DO_HARVEST);
        this.validate = s;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putShort(this.validate);
        return packBuffer(bf);
    }
}

