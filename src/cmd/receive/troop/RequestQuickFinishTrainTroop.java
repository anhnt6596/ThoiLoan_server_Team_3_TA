package cmd.receive.train;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;

public class RequestQuickFinishTrainTroop extends BaseCmd {
    public int idBarrack;
    
    public RequestQuickFinishTrainTroop(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    public void unpackData(){
        ByteBuffer bf = makeBuffer();
        try {
            idBarrack = readInt(bf);
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);
        }
    }
}
