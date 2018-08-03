package cmd.receive.log;

import java.nio.ByteBuffer;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

public class RequestLogLogin extends BaseCmd {
    public long zingId;
    public int accountType;
    public String deviceId;
    public String openAccount;

    public RequestLogLogin(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            zingId = readLong(bf);
            accountType = readInt(bf);
            deviceId = readString(bf);
            openAccount = readString(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
