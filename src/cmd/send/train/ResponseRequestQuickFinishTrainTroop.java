package cmd.send.train;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseRequestQuickFinishTrainTroop extends BaseMsg {
    private short validate;
    
    public ResponseRequestQuickFinishTrainTroop(short validate) {
        super(CmdDefine.QUICK_FINISH_TRAIN_TROOP);
        this.validate = validate;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(this.validate);
        System.out.println("==================Response Quick Finish Train Troop: " + this.validate);
        return packBuffer(bf);
    }
}
