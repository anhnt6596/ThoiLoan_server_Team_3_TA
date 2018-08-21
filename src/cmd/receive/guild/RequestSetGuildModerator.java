package cmd.receive.guild;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;


public class RequestSetGuildModerator extends BaseCmd{
    public int id;  
        
    public RequestSetGuildModerator(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {            
            this.id = readInt(bf); 
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);}
    }
}
