package cmd.receive.guild;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;


public class RequestSetGuildPosition extends BaseCmd{
    public short type_position;
    public int id;  
        
    public RequestSetGuildPosition(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {         
            this.id = readInt(bf); 
            this.type_position = readShort(bf);
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);}
    }
}