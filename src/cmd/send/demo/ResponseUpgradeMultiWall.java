package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import model.Building;

import java.nio.ByteBuffer;

import model.MapInfo;


public class ResponseUpgradeMultiWall extends BaseMsg {
    short validate ;
    public ResponseUpgradeMultiWall(short s) {
        super(CmdDefine.UPGRADE_MULTI_WALL);
        this.validate = s;
    }


    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(validate);
        return packBuffer(bf);
    }
}
