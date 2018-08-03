package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import model.Building;

import java.nio.ByteBuffer;

import model.MapInfo;


public class ResponseRequestAddConstruction extends BaseMsg {
    short validate ;
    public ResponseRequestAddConstruction(short s) {
        super(CmdDefine.ADD_CONSTRUCTION);
        this.validate = s;
    }


    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        System.out.println("validate XAY= " + this.validate );
        
        bf.putShort(this.validate);
        
        return packBuffer(bf);
    }
}
