package cmd.receive.friend;

import java.nio.ByteBuffer;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

public class RequestSuggestedFriends extends BaseCmd {
    public int friendIndex;
    //	public String accountType;
    //	public String accessToken;

    public RequestSuggestedFriends(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            friendIndex = readInt(bf);
            //			accountType = readString(bf);
            //			accessToken = readString(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
