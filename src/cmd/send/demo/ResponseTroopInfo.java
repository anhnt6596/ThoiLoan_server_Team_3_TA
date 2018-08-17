package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;
import java.nio.ByteBuffer;

import model.Troop;
import model.TroopInfo;

public class ResponseTroopInfo extends BaseMsg {
    TroopInfo troopInfo;
    public ResponseTroopInfo(TroopInfo troopInfo) {
        super(CmdDefine.GET_TROOP_INFO);
        this.troopInfo = troopInfo;
    }

    @Override
    public byte[] createData() {
        Troop troop;
        ByteBuffer bf = makeBuffer();
        int size = this.troopInfo.getSize();
        bf.putInt(size);
        for (String key : this.troopInfo.troopMap.keySet()) {
            troop = this.troopInfo.troopMap.get(key);
            putStr(bf, troop.type);
            bf.putShort(troop.isUnlock);
            bf.putShort(troop.level);
            bf.putShort(troop.population);
            bf.putLong(troop.startTime);
            putStr(bf, troop.status);
        }
        return packBuffer(bf);
    }
}