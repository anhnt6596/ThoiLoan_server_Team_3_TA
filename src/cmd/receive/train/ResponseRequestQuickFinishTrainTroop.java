package cmd.receive.train;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseRequestQuickFinishTrainTroop extends BaseMsg {
    public short validate ;
    
    public ResponseRequestQuickFinishTrainTroop(short _validate) {
        super(CmdDefine.QUICK_FINISH_TRAIN_TROOP);
        validate = _validate;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(this.validate);
        System.out.println("==================Response Quick Finish Train Troop: " + this.validate);
        return packBuffer(bf);
    }
}
