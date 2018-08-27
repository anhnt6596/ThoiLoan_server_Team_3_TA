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
        int sizeBarrackQueueInfo = this.barrackQueueInfo.barrackQueueMap.size();
        //kich thuoc cua BarrackQueueInfo
        bf.putInt(sizeBarrackQueueInfo);
        for (Integer idBarrack : this.barrackQueueInfo.barrackQueueMap.keySet()) {
            barrackQueue = this.barrackQueueInfo.barrackQueueMap.get(idBarrack);
            //id cua Barrack
            bf.putInt(idBarrack);
            bf.putInt(barrackQueue.getAmountItemInQueue());
            bf.putInt(barrackQueue.getTotalTroopCapacity());
            bf.putLong(barrackQueue.startTime);
            
            //troopList
            TroopInBarrack troopInBarrack;
            int sizeTroopList = barrackQueue.troopListMap.size();
            //kich thuoc cua TroopList
            bf.putInt(sizeTroopList);
            for (String troopType : barrackQueue.troopListMap.keySet()) {
                troopInBarrack = barrackQueue.troopListMap.get(troopType);
                putStr(bf, troopType);
                bf.putInt(troopInBarrack.getAmount());
                bf.putInt(troopInBarrack.getCurrentPosition());
            }
        }
        return packBuffer(bf);
    }
}
