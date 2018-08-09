package cmd.receive.harvest;
 import bitzero.server.entities.User;
import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;
 import bitzero.util.common.business.CommonHandle;
 import java.nio.ByteBuffer;
 public class RequestGetHarvestInfo extends BaseCmd{
    public String type;
    
    public RequestGetHarvestInfo(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            this.type = readString(bf);
            //System.out.println("LOG_ADDBUILDING: type+" + this.type );                      
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);}
    }
}