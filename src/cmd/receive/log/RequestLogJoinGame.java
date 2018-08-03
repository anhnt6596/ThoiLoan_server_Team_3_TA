package cmd.receive.log;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestLogJoinGame extends BaseCmd {
    public long zingId;
    public int accountType;
    public String deviceId;
    public String openAccount;
    public long gameId;

    public RequestLogJoinGame(DataCmd data) {
        super(data);
    }

    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            zingId = readLong(bf);
            accountType = readInt(bf);
            deviceId = readString(bf);
            openAccount = readString(bf);
            gameId = readLong(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
