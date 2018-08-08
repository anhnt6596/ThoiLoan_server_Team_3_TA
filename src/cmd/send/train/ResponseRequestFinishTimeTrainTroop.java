package cmd.send.train;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import model.train.BarrackQueueInfo;

public class ResponseRequestFinishTimeTrainTroop extends BaseMsg {
    public short validate ;
    
    public ResponseRequestFinishTimeTrainTroop(short _validate) {
        super(CmdDefine.FINISH_TIME_TRAIN_TROOP);
        validate = _validate;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(this.validate);
        System.out.println("==================Response Finish Time Train Troop: " + this.validate);
        return packBuffer(bf);
    }
}
