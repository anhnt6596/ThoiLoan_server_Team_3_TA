package cmd.send.train;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

import model.Troop;

import model.train.BarrackQueue;
import model.train.BarrackQueueInfo;
import model.train.TroopInBarrack;

public class ResponseRequestBarrackQueueInfo extends BaseMsg {
    BarrackQueueInfo barrackQueueInfo;
    
    public ResponseRequestBarrackQueueInfo(BarrackQueueInfo _barrackQueueInfo) {
        super(CmdDefine.GET_BARRACK_QUEUE_INFO);
        barrackQueueInfo = _barrackQueueInfo;
    }

    @Override
    public byte[] createData() {
        BarrackQueue barrackQueue;
        ByteBuffer bf = makeBuffer();
        int sizeBarrackQueueInfo = barrackQueueInfo.barrackQueueMap.size();
        //kich thuoc cua BarrackQueueInfo
        bf.putInt(sizeBarrackQueueInfo);
        for (Integer idBarrack : barrackQueueInfo.barrackQueueMap.keySet()) {
            barrackQueue = barrackQueueInfo.barrackQueueMap.get(idBarrack);
            //id cua Barrack
            bf.putInt(idBarrack);
//            bf.putInt(barrackQueue.barrackLevel);
            bf.putInt(barrackQueue.amountItemInQueue);
            bf.putInt(barrackQueue.totalTroopCapacity);
            bf.putLong(barrackQueue.startTime);
            
            //troopList
            TroopInBarrack troopInBarrack;
            int sizeTroopList = barrackQueue.troopListMap.size();
            //kich thuoc cua TroopList
            bf.putInt(sizeTroopList);
            for (String troopType : barrackQueue.troopListMap.keySet()) {
                troopInBarrack = barrackQueue.troopListMap.get(troopType);
                //type cua troop
                putStr(bf, troopType);
                bf.putInt(troopInBarrack.amount);
                putBoolean(bf, troopInBarrack.isInQueue);
                bf.putInt(troopInBarrack.currentPosition);
            }
        }
        return packBuffer(bf);
    }
}
