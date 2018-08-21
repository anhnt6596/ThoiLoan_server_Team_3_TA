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
    
    public long getMinStartTime() {
        long minTime = 1893415558482L;              //nam 2030
        BarrackQueue barrackQueue;
        for (Integer id : barrackQueueMap.keySet()) {
            barrackQueue = barrackQueueMap.get(id);
            if(barrackQueue.startTime < minTime && barrackQueue.amountItemInQueue > 0){
                minTime = barrackQueue.startTime;
            }
        }
        return minTime;
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
    
    
    //Tra ve so luong troop co the trong khoang tgian delta, khong phu thuoc vao capacity
    public int getAmountTroopCapacityCanBeTrained(long _deltaTime) {
        int totalTroopCapacityReturn = 0;
        BarrackQueue barrackQueue;
        long deltaTime;
        for (Integer idBarrack : barrackQueueMap.keySet()) {
            barrackQueue = barrackQueueMap.get(idBarrack);
            if(barrackQueue.startTime == 0){
                continue;
            }
            
            deltaTime = _deltaTime - (barrackQueue.startTime - this.getMinStartTime());
            
            //troopList
            TroopInBarrack troopInBarrack;
            long amountTrainedTroop;
            if(barrackQueue.amountItemInQueue > 0 && barrackQueue.totalTroopCapacity > 0){
                for(int i = 0; i < barrackQueue.amountItemInQueue; i++) {
                    troopInBarrack = barrackQueue.getTroopByPosition(i);
                    amountTrainedTroop = deltaTime / (troopInBarrack.trainingTime * 1000);
                    if(amountTrainedTroop >= troopInBarrack.amount){
                        totalTroopCapacityReturn += troopInBarrack.amount * troopInBarrack.housingSpace;        
                        int a = barrackQueue.amountItemInQueue - 1;
                        if(a == 0){     //Het troop thi break
                            break;
                        }
                        deltaTime -=  amountTrainedTroop * (troopInBarrack.trainingTime * 1000);
                        if(deltaTime == 0){
                            break;
                        }    
                    }else if(amountTrainedTroop >= 1){
                        totalTroopCapacityReturn += amountTrainedTroop * troopInBarrack.housingSpace;
                        break;
                    }else{
                        break;
                    }
                }            
            } 
        }
        return totalTroopCapacityReturn;
    }
}
