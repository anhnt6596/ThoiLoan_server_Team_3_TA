package cmd.receive.friend;

import java.nio.ByteBuffer;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

public class RequestSearchFriend extends BaseCmd {
    public String name;

    public RequestSearchFriend(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            name = readString(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
