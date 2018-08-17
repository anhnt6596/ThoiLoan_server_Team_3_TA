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
    public String username;
    
    public ResponseSendNewMessage(short _typeResponse, short _validateValue, MessageGuild _messageGuild) {
        super(CmdDefine.NEW_MESSAGE);
        typeResponse = _typeResponse;
        validateValue = _validateValue;
        messageGuild = _messageGuild;
        username = BitZeroServer.getInstance().getUserManager().getUserById(messageGuild.id_user).getName();
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
            putStr(bf, username);
            putStr(bf, messageGuild.content);
        }
        return packBuffer(bf);
    }
}
