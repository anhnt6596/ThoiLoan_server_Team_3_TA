package cmd.receive.guild;

import bitzero.server.entities.User;
import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;




public class RequestCreateGuild extends BaseCmd{    
    public String name;
    public int logo_id;
    public short status;
    public int require_danh_vong;
    public String description;
    
    public RequestCreateGuild(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    @Override
    public void unpackData() {  
        ByteBuffer bf = makeBuffer();
        try {                        
            this.name = readString(bf);
            this.logo_id = readInt(bf);
            this.status = readShort(bf);
            this.require_danh_vong = readInt(bf);
            this.description = readString(bf);
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);}
    }
}


