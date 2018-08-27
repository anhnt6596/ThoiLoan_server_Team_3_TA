package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import util.server.ServerConstant;

public class ResponseGiveTroop extends BaseMsg {
    private short typeResponse;                      //validate or toAll
    private short validateValue;
    private int idUserGet;
    private String troopType;
    private int levelTroop;
    private int idUserGive;
    
    public ResponseGiveTroop(short typeResponse, short validateValue, int idUserGet, String troopType, int levelTroop, int idUserGive) {
        super(CmdDefine.GIVE_TROOP_GUILD);
        this.typeResponse = typeResponse;
        this.validateValue = validateValue;
        this.idUserGet = idUserGet;
        this.troopType = troopType;
        this.levelTroop = levelTroop;
        this.idUserGive = idUserGive;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putShort(this.typeResponse);

        if(this.typeResponse == ServerConstant.VALIDATE){
            bf.putShort(this.validateValue);
        }else if(this.typeResponse == ServerConstant.TO_ALL){
            bf.putInt(this.idUserGet);
            putStr(bf, this.troopType);
            bf.putInt(this.levelTroop);
            bf.putInt(this.idUserGive);
        }
        return packBuffer(bf);
    }
}
