

package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import model.Building;

import java.nio.ByteBuffer;

import model.MapInfo;


public class ResponseRequestServerTime extends BaseMsg {
    long time;
    public ResponseRequestServerTime(long _time) {
        super(CmdDefine.GET_SERVER_TIME);
        this.time = _time;
    }


    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putLong(this.time);
        System.out.println("ResponseRequestServerTime "+ this.time);
        return packBuffer(bf);
    }
}
