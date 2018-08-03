package cmd.receive.log;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestLogMember extends BaseCmd {
    public long zingId;
    public int accountType;
    public String deviceId;
    public String openAccount;
    public int lastestStep;

    public RequestLogMember(DataCmd dataCmd) {
        super(dataCmd);
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            zingId = readLong(bf);
            accountType = readInt(bf);
            deviceId = readString(bf);
            openAccount = readString(bf);
            lastestStep = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
