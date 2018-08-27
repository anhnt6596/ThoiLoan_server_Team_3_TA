package cmd.send.train;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseRequestFinishTimeTrainTroop extends BaseMsg {
    private short validate;
    private int idBarrack;
    private String troopType;
    
    public ResponseRequestFinishTimeTrainTroop(short validate, int idBarrack, String troopType) {
        super(CmdDefine.FINISH_TIME_TRAIN_TROOP);
        this.validate = validate;
        this.idBarrack = idBarrack;
        this.troopType = troopType;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(this.validate);
        bf.putInt(this.idBarrack);
        putStr(bf, this.troopType);
        System.out.println("==================Response Finish Time Train Troop: " + this.validate);
        return packBuffer(bf);
    }
}
