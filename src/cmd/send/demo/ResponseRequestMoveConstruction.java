package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import model.Building;

import java.nio.ByteBuffer;

import model.MapInfo;


public class ResponseRequestMoveConstruction extends BaseMsg {
    short validate ;
    public ResponseRequestMoveConstruction(short s) {
        super(CmdDefine.MOVE_CONSTRUCTION);
        this.validate = s;
    }


    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();        
        bf.putShort(validate);
        System.out.println("success or not "+ this.validate);
        return packBuffer(bf);
    }
}