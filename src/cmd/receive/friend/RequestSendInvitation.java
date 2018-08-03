package cmd.receive.friend;

import java.nio.ByteBuffer;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

public class RequestSendInvitation extends BaseCmd {
    public String[] listFriendId;

    public RequestSendInvitation(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
    }

    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            listFriendId = readStringArray(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
