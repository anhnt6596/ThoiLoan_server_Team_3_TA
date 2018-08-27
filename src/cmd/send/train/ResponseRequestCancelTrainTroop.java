package cmd.send.train;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseRequestCancelTrainTroop extends BaseMsg {
    private short validate;
    
    public ResponseRequestCancelTrainTroop(short validate) {
        super(CmdDefine.CANCEL_TRAIN_TROOP);
        this.validate = validate;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(this.validate);
        System.out.println("==================Response Train Troop: " + this.validate);
        return packBuffer(bf);
    }
}
