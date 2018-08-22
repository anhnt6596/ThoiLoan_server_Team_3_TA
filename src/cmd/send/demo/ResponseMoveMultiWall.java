package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import model.Building;

import java.nio.ByteBuffer;

import model.MapInfo;


public class ResponseMoveMultiWall extends BaseMsg {
    short validate ;
    public ResponseMoveMultiWall(short s) {
        super(CmdDefine.MOVE_MULTI_WALL);
        this.validate = s;
    }


    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(validate);
        return packBuffer(bf);
    }
}
