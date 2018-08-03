package cmd.receive.friend;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestDeleteMessage extends BaseCmd {
    //	public long friendId;
    public int[] listMessageId;

    public RequestDeleteMessage(DataCmd dataCmd) {
        super(dataCmd);
    }

    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            //    	  friendId = readLong(bf);
            listMessageId = readIntArray(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
