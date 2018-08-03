package cmd.receive.friend;

import java.nio.ByteBuffer;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

public class RequestGetMessageBox extends BaseCmd {
    //	public long friendId;
    public int startIndex;
    //	public int numMessage;

    public RequestGetMessageBox(DataCmd dataCmd) {
        super(dataCmd);
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            //    		friendId = readLong(bf);
            startIndex = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
