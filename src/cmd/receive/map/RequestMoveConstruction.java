package cmd.receive.map;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;




public class RequestMoveConstruction extends BaseCmd{
    public int id;
    public int posX;
    public int posY;
    
    public RequestMoveConstruction(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            id = readInt(bf);
            posX = readInt(bf);
            posY = readInt(bf);
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);}
    }
}

