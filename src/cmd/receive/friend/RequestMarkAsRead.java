package cmd.receive.friend;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestMarkAsRead extends BaseCmd {
    public RequestMarkAsRead(DataCmd dataCmd) {
        super(dataCmd);
    }

    public long friendId;
    public int messageId;

    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            friendId = readLong(bf);
            messageId = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
