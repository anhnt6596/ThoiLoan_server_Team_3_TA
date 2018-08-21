package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;
import java.nio.ByteBuffer;

import model.Guild;
import model.TroopInfo;

import util.server.ServerConstant;

public class ResponseCreateGuild extends BaseMsg {
    private short validate;
    private Guild guild;

    public ResponseCreateGuild(short s, Guild guild) {
        super(CmdDefine.CREATE_GUILD);
        this.validate = s;
        this.guild = guild;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putShort(this.validate);
        if (this.validate == ServerConstant.SUCCESS){
            bf.putInt(guild.id);
            putStr(bf,guild.name); 
            bf.putInt(guild.logo_id);
            bf.putShort(guild.status);
            bf.putInt(guild.level);
            bf.putInt(guild.danh_vong);
            bf.putInt(guild.danh_vong_require);
            putStr(bf,guild.description);
        }
        
        
        return packBuffer(bf);
    }
}