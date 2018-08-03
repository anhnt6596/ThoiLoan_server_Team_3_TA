



package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import model.Building;

import java.nio.ByteBuffer;

import model.MapInfo;


public class ResponseRequestFinishTimeConstruction extends BaseMsg {
    short validate ;
    public ResponseRequestFinishTimeConstruction(short s) {
        super(CmdDefine.FINISH_TIME_CONSTRUCTION);
        this.validate = s;
    }


    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(validate);
        System.out.println("FINISH_TIME_CONSTRUCTION send respondse "+ this.validate);
        return packBuffer(bf);
    }
}

