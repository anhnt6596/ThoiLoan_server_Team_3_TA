package cmd.receive.harvest;

import bitzero.server.entities.User;
import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;

public class RequestDoHarvest extends BaseCmd{
    public int id;
    
    public RequestDoHarvest(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            this.id = readInt(bf);
            //System.out.println("LOG_ADDBUILDING: type+" + this.type );                      
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);}
    }
}


