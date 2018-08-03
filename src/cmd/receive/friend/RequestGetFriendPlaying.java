package cmd.receive.friend;

import java.nio.ByteBuffer;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

public class RequestGetFriendPlaying extends BaseCmd {
    public long gameId;

    public RequestGetFriendPlaying(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
    }

    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            gameId = readLong(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
