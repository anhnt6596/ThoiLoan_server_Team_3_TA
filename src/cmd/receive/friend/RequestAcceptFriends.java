package cmd.receive.friend;

import java.nio.ByteBuffer;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

public class RequestAcceptFriends extends BaseCmd {
    public long friendId;
    public boolean isAccept;

    public RequestAcceptFriends(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            friendId = readLong(bf);
            isAccept = readBoolean(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
