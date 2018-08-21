package cmd.send.train;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import model.train.BarrackQueueInfo;

import util.server.ServerConstant;

public class ResponseRequestFinishTimeTrainTroop extends BaseMsg {
    public short validate ;
    public int idBarrack;
    String troopType;
    
    public ResponseRequestFinishTimeTrainTroop(short _validate, int _idBarrack, String _troopType) {
        super(CmdDefine.FINISH_TIME_TRAIN_TROOP);
        validate = _validate;
        idBarrack = _idBarrack;
        troopType = _troopType;
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
