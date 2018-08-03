package cmd.receive.friend;

import java.nio.ByteBuffer;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

public class RequestSetFriendStar extends BaseCmd {
    public long friendId;
    public int numStar;

    public RequestSetFriendStar(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            friendId = readLong(bf);
            numStar = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
