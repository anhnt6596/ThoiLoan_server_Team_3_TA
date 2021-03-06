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
        
        bf.putShort(this.validate);
        System.out.println("UPGRADE_MULTI_WALL, validate = "+ this.validate);
        return packBuffer(bf);
    }
}
