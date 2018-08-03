package cmd.receive.troop;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;


public class RequestTroopInfo extends BaseCmd {
    public String type;

    public RequestTroopInfo(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            
        } catch (Exception e) {
            CommonHandle.writeErrLog(e);
        }
    }
}
