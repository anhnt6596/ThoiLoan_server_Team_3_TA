package cmd.send.train;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import model.train.BarrackQueue;
import model.train.BarrackQueueInfo;
import model.train.TroopInBarrack;

public class ResponseRequestBarrackQueueInfo extends BaseMsg {
    private BarrackQueueInfo barrackQueueInfo;
    
    public ResponseRequestBarrackQueueInfo(BarrackQueueInfo barrackQueueInfo) {
        super(CmdDefine.GET_BARRACK_QUEUE_INFO);
        this.barrackQueueInfo = barrackQueueInfo;
    }

    @Override
    public byte[] createData() {
        BarrackQueue barrackQueue;
        ByteBuffer bf = makeBuffer();
        int sizeBarrackQueueInfo = barrackQueueInfo.barrackQueueList.size();
        //kich thuoc cua BarrackQueueInfo
        bf.putInt(sizeBarrackQueueInfo);
        for(int j = 0; j < sizeBarrackQueueInfo; j++) {
            barrackQueue = barrackQueueInfo.barrackQueueList.get(j);
            //id cua Barrack
            bf.putInt(barrackQueue.getId());
            bf.putInt(barrackQueue.getBarrackLevel());
            bf.putLong(barrackQueue.startTime);
            
            //troopList
            TroopInBarrack troopInBarrack;
            int sizeTroopList = barrackQueue.trainTroopList.size();
            //kich thuoc cua TroopList
            bf.putInt(sizeTroopList);
            for(int i = 0; i < sizeTroopList; i++) {
                troopInBarrack = barrackQueue.trainTroopList.get(i);
                putStr(bf, troopInBarrack.getName());
                bf.putInt(troopInBarrack.getAmount());
            }
        }
        return packBuffer(bf);
    }
}
