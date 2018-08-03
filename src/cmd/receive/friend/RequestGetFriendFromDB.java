package cmd.receive.friend;

import java.nio.ByteBuffer;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

public class RequestGetFriendFromDB extends BaseCmd {
    public int friendIndex;

    public RequestGetFriendFromDB(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            friendIndex = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
