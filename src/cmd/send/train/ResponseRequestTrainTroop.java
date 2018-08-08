package cmd.send.train;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseRequestTrainTroop extends BaseMsg {
    public short validate ;
    
    public ResponseRequestTrainTroop(short _validate) {
        super(CmdDefine.TRAIN_TROOP);
        validate = _validate;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(this.validate);
        System.out.println("==================Response Cancel Train Troop: " + this.validate);
        return packBuffer(bf);
    }
}
