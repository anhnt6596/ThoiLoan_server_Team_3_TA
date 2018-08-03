package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;


import java.awt.Point;

import java.nio.ByteBuffer;

import model.Building;
import model.MapInfo;
import cmd.obj.map.Obs;

import model.Troop;
import model.TroopInfo;

public class ResponseResearch extends BaseMsg {
    TroopInfo troopInfo;
    private short validate;

    public ResponseResearch(short s) {
        super(CmdDefine.RESEARCH_TROOP);
        this.validate = s;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putShort(this.validate);
        return packBuffer(bf);
    }
}
