package cmd.receive.friend;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestGetFriends extends BaseCmd {
    public String[] listFriendId;
    public String[] listAccountType;

    public RequestGetFriends(DataCmd data) {
        super(data);
    }

    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            listFriendId = readStringArray(bf);
            listAccountType = readStringArray(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
