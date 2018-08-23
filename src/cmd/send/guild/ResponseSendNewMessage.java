package cmd.send.guild;

import bitzero.server.BitZeroServer;
import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import model.MessageGuild;

import model.train.BarrackQueue;
import model.train.TroopInBarrack;

import util.server.ServerConstant;

public class ResponseSendNewMessage extends BaseMsg {
    public short typeResponse;                      //validate or toAll
    public short validateValue;
    public MessageGuild messageGuild;
    public String nameSender;
    
    public ResponseSendNewMessage(short _typeResponse, short _validateValue, MessageGuild _messageGuild, String _nameSender) {
        super(CmdDefine.NEW_MESSAGE);
        typeResponse = _typeResponse;
        validateValue = _validateValue;
        messageGuild = _messageGuild;
        nameSender = _nameSender;
       
    }
    
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putShort(typeResponse);

        if(typeResponse == ServerConstant.VALIDATE){
            bf.putShort(validateValue);
        }else if(typeResponse == ServerConstant.TO_ALL){
            bf.putShort(messageGuild.type);
            bf.putInt(messageGuild.id_user);
            putStr(bf, nameSender);
            putStr(bf, messageGuild.content);
            bf.putInt(messageGuild.currentCapacityTroop);
            bf.putInt(messageGuild.guildCapacityAtTime);
        }
        return packBuffer(bf);
    }
}
