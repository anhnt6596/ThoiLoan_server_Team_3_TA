package cmd.receive.guild;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import cmd.receive.user.RequestWriteStatus;

import java.nio.ByteBuffer;

public class RequestSearchGuild extends BaseCmd {
    public short type;
    //public int id_search;
    public String string;

    public RequestSearchGuild(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            this.type = readShort(bf);
            //this.id_search = readInt(bf);
            this.string = readString(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}