package cmd.receive.guild;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;

public class RequestGiveTroop extends BaseCmd{
    public int idUserGet;
    public String troopType;
    public short level;
    
    public RequestGiveTroop(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    public void unpackData(){
        ByteBuffer bf = makeBuffer();
        try {
            this.idUserGet = readInt(bf);
            this.troopType = readString(bf);
            this.level = readShort(bf);
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);
        }
    }
}
