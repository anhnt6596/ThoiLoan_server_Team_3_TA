package cmd.receive.guild;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestGetGuildInfo extends BaseCmd {    
    public int id;    

    public RequestGetGuildInfo(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {            
            this.id = readInt(bf);            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}