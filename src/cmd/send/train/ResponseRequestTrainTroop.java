package cmd.send.train;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseRequestTrainTroop extends BaseMsg {
    private short validate;
    
    public ResponseRequestTrainTroop(short validate) {
        super(CmdDefine.TRAIN_TROOP);
        this.validate = validate;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(this.validate);
        System.out.println("==================Response Cancel Train Troop: " + this.validate);
        return packBuffer(bf);
    }
}
