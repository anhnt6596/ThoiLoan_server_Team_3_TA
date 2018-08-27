package cmd.send.guild;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import model.MessageGuild;

import util.server.ServerConstant;

public class ResponseSendNewMessage extends BaseMsg {
    private short typeResponse;                      //validate or toAll
    private short validateValue;
    private MessageGuild messageGuild;
    private String nameSender;
    
    public ResponseSendNewMessage(short typeResponse, short validateValue, MessageGuild messageGuild, String nameSender) {
        super(CmdDefine.NEW_MESSAGE);
        this.typeResponse = typeResponse;
        this.validateValue = validateValue;
        this.messageGuild = messageGuild;
        this.nameSender = nameSender;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putShort(this.typeResponse);
        if(this.typeResponse == ServerConstant.VALIDATE){
            bf.putShort(this.validateValue);
        }else if(this.typeResponse == ServerConstant.TO_ALL){
            bf.putShort(this.messageGuild.type);
            bf.putInt(this.messageGuild.id_user);
            putStr(bf, this.nameSender);
            putStr(bf, this.messageGuild.content);
            bf.putInt(this.messageGuild.currentCapacityTroop);
            bf.putInt(this.messageGuild.guildCapacityAtTime);
        }
        return packBuffer(bf);
    }
}
