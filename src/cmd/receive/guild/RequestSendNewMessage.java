package cmd.receive.guild;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;

public class RequestSendNewMessage extends BaseCmd {
    public short type;             //normal or askTroop
    public String content;
    
    public RequestSendNewMessage(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    public void unpackData(){
        ByteBuffer bf = makeBuffer();
        try {
            this.type = readShort(bf);
            this.content = readString(bf);
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);
        }
    }
}
