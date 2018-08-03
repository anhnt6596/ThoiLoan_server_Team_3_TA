package cmd.receive.friend;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestSendMessage extends BaseCmd {
    public long receiverId;
    public String message;


    public RequestSendMessage(DataCmd dataCmd) {
        super(dataCmd);
    }

    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            receiverId = readLong(bf);
            message = readString(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
