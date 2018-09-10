package cmd.receive.map;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;

public class RequestFinishTimeRemoveObs extends BaseCmd{
    public int id;
    public RequestFinishTimeRemoveObs(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            this.id = readInt(bf);
            this.id -= 5000;            
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);}
    }
}
