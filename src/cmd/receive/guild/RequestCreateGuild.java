package cmd.receive.guild;

import bitzero.server.entities.User;
import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;




public class RequestCreateGuild extends BaseCmd{
    public int id;
    public String name;
    public int logo_id;
    public RequestCreateGuild(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    @Override
    public void unpackData() {  
        ByteBuffer bf = makeBuffer();
        try {            
            this.id = readInt(bf); //id nay la id trong mang listBuilding/MapInfo
            this.name = readString(bf);
            this.logo_id = readInt(bf);
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);}
    }
}


