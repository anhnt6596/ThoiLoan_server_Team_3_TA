package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import util.server.ServerConstant;

public class ResponseGiveTroop extends BaseMsg {
    public short typeResponse;                      //validate or toAll
    public short validateValue;
    public int idUserGet;
    public short capacityGet;
    
    public ResponseGiveTroop(short _typeResponse, short _validateValue, int _idUserGet, short _capacityGet) {
        super(CmdDefine.GIVE_TROOP_GUILD);
        typeResponse = _typeResponse;
        validateValue = _validateValue;
        idUserGet = _idUserGet;
        capacityGet = _capacityGet;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putShort(typeResponse);

        if(typeResponse == ServerConstant.VALIDATE){
            bf.putShort(validateValue);
        }else if(typeResponse == ServerConstant.TO_ALL){
            bf.putInt(idUserGet);
            bf.putShort(capacityGet);
        }
        return packBuffer(bf);
    }
}
