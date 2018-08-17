package cmd.receive.guild;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;

public class RequestGiveTroop extends BaseCmd{
    private int idUserGet;
    private String troopType;
    private short level;
    
    public RequestGiveTroop(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    public void unpackData(){
        ByteBuffer bf = makeBuffer();
        try {
            idUserGet = readInt(bf);
            troopType = readString(bf);
            level = readShort(bf);
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);
        }
    }
}
