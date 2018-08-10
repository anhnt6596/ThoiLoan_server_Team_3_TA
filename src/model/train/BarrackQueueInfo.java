package model.train;


import java.util.HashMap;
import java.util.Map;

import util.database.DataModel;

public class BarrackQueueInfo extends DataModel {
    public Map<Integer, BarrackQueue> barrackQueueMap = new HashMap<Integer, BarrackQueue>();

    public BarrackQueueInfo() {
        super();
        this.init();
    }

    private void init() {
        //Lúc init chua có barrack nào
    }
    
    public long getMaxStartTime() {
        long maxTime = 0;
        BarrackQueue barrackQueue;
        for (Integer id : barrackQueueMap.keySet()) {
            barrackQueue = barrackQueueMap.get(id);
            if(barrackQueue.startTime > maxTime){
                maxTime = barrackQueue.startTime;
            }
        }
        return maxTime;
    }
    
    //In ra
    public void print() {
        System.out.println("======================= BarrackQueueInfo ======================");
        BarrackQueue barrackQueue;
        int i = 1;
        for (Integer idBarrack : barrackQueueMap.keySet()) {
            barrackQueue = barrackQueueMap.get(idBarrack);
            //id cua Barrack
            System.out.println("-------------- BarrackQueue thu " + i);
            System.out.println("id Barrack " + idBarrack);
            System.out.println("level Barrack " + barrackQueue.barrackLevel);
            System.out.println("amountItemInQueue " + barrackQueue.amountItemInQueue);
            System.out.println("totalTroopCapacity " + barrackQueue.totalTroopCapacity);
            System.out.println("startTime " + barrackQueue.startTime);
            
            //troopList
            int j = 1;
            TroopInBarrack troopInBarrack;
            System.out.println("-------------- TroopList --------------");
            for (String troopType : barrackQueue.troopListMap.keySet()) {
                troopInBarrack = barrackQueue.troopListMap.get(troopType);
                System.out.println("-------------- Troop thu " + j);
                System.out.println("Type troop: --- " + troopType);
                System.out.println("Amount troop in queue: --- " + troopInBarrack.amount);
                System.out.println("Current Position of troop in queue: --- " + troopInBarrack.currentPosition);
                j++;
            }
            System.out.println("-------------- END TroopList ======================");
            System.out.println("-------------- END BarrackQueue thu " + i);
            i++;
        }
        System.out.println("=======================END BarrackQueueInfo ======================");
    }
}
