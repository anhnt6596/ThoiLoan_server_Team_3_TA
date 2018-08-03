package cmd.receive.user;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;

//import service.DemoHandler.DemoDirection;

public class RequestAddResource extends BaseCmd {
    public int gold;
    public int elixir;
    public int darkElixir;
    public int coin;
    public RequestAddResource(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    public void unpackData(){
        ByteBuffer bf = makeBuffer();
        try {
            this.gold = readInt(bf);
            this.elixir = readInt(bf);
            this.darkElixir = readInt(bf);
            this.coin = readInt(bf);
            System.out.println("this.gold"+this.gold+"this.elixir"+this.elixir+"this.coin"+this.coin);
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);
        }
    }
}
