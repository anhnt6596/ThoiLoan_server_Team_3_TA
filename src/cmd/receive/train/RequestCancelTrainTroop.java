package cmd.receive.train;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;

public class RequestCancelTrainTroop extends BaseCmd {
    public int idBarrack;
    public String typeTroop;
    
    public RequestCancelTrainTroop(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    public void unpackData(){
        ByteBuffer bf = makeBuffer();
        try {
            this.idBarrack = readInt(bf);
            this.typeTroop = readString(bf);
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);
        }
    }
}
