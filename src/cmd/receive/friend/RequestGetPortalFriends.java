package cmd.receive.friend;

import java.nio.ByteBuffer;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

public class RequestGetPortalFriends extends BaseCmd {
    public String accountType;
    public String accessToken;

    public int friendIndex;

    public RequestGetPortalFriends(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            accountType = readString(bf);
            accessToken = readString(bf);
            friendIndex = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
