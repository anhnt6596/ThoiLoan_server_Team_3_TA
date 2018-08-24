package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import util.server.ServerConstant;

public class ResponseGiveTroop extends BaseMsg {
    public short typeResponse;                      //validate or toAll
    public short validateValue;
    public int idUserGet;
    String troopType;
    int levelTroop;
    int idUserGive;
    
    public ResponseGiveTroop(short _typeResponse, short _validateValue, int _idUserGet, String _troopType, int _levelTroop, int _idUserGive) {
        super(CmdDefine.GIVE_TROOP_GUILD);
        typeResponse = _typeResponse;
        validateValue = _validateValue;
        idUserGet = _idUserGet;
        troopType = _troopType;
        levelTroop = _levelTroop;
        idUserGive = _idUserGive;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putShort(typeResponse);

        if(typeResponse == ServerConstant.VALIDATE){
            bf.putShort(validateValue);
        }else if(typeResponse == ServerConstant.TO_ALL){
            bf.putInt(idUserGet);
            putStr(bf, troopType);
            bf.putInt(levelTroop);
            bf.putInt(idUserGive);
        }
        return packBuffer(bf);
    }
}
