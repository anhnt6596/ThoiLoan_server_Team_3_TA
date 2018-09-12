package model.train;

import bitzero.server.entities.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Building;
import model.MapInfo;
import model.ZPUserInfo;

import util.database.DataModel;
import util.server.ServerConstant;

public class BarrackQueueInfo extends DataModel {
    public List<BarrackQueue> barrackQueueList = new ArrayList<BarrackQueue>();
    
    //Tra ve so luong troop co the trong khoang tgian delta, khong phu thuoc vao capacity
    public int getAmountTroopCapacityCanBeTrained(User user, long _deltaTime) {
        int totalTroopCapacityReturn = 0;
        BarrackQueue barrackQueue;
        long deltaTime;     
        
        for(int j = 0; j < barrackQueueList.size(); j++) {
            barrackQueue = barrackQueueList.get(j);
            if(this.isBarrackUpgrading(user, barrackQueue.getId()) || (barrackQueue.isStop == ServerConstant.YES)){
                continue;
            }
            
            deltaTime = _deltaTime - (barrackQueue.startTime - this.getMinStartTime(user));
            
            //troopList
            TroopInBarrack troopInBarrack;
            long amountTrainedTroop;
            if(barrackQueue.getAmountItemInQueue() > 0 && barrackQueue.getTotalTroopCapacity() > 0){
                for(int i = 0; i < barrackQueue.getAmountItemInQueue(); i++) {
                    troopInBarrack = barrackQueue.trainTroopList.get(i);
                    amountTrainedTroop = deltaTime / (troopInBarrack.getTrainingTime() * 1000);
                    if(amountTrainedTroop >= troopInBarrack.getAmount()){
                        totalTroopCapacityReturn += troopInBarrack.getAmount() * troopInBarrack.getHousingSpace();        
                        int a = barrackQueue.getAmountItemInQueue() - 1;
                        if(a == 0) break;
                        deltaTime -=  troopInBarrack.getAmount() * (troopInBarrack.getTrainingTime() * 1000);
                        if(deltaTime == 0) break;  
                    }else if(amountTrainedTroop >= 1){
                        totalTroopCapacityReturn += amountTrainedTroop * troopInBarrack.getHousingSpace();
                        break;
                    }else{
                        break;
                    }
                }            
            } 
        }
        return totalTroopCapacityReturn;
    }
    
    public void checkFirst(User user) {
        boolean isCheck = false;
        BarrackQueue barrackQueue;     
        for(int j = 0; j < barrackQueueList.size(); j++) {
            barrackQueue = barrackQueueList.get(j);
            if(barrackQueue.getAmountItemInQueue() > 0){
                isCheck = true;
            }
        }
        
        if(isCheck == false) {
            return;
        }

        ZPUserInfo userInfo;
        try {
            userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
        } catch (Exception e) {
            return;
        }
        //Cap nhat capacity AMCs
        int totalAMCsCapacity = userInfo.getTotalCapacityAMCs();
        //So linh tinh tu luc off
        int troopCapacityBefore = userInfo.getCurrentCapacityTroop();
        //So capacity linh toi da co the train
        int troopAvai = totalAMCsCapacity - troopCapacityBefore;
        if(troopAvai == 0) return; 
        
        //Thoi diem offline
        long timeOff = this.getMinStartTime(user);
        if(timeOff == 0) return;
        long currentTime = System.currentTimeMillis();
        
        long timeAvai = (2700 * 3 + 600 * 1) * 1000;                  //25x3 + 10x1 = 85 (max queue length barrack level 12)
        long deltaTime = currentTime - timeOff;
        if(deltaTime > timeAvai) deltaTime = timeAvai * 1000;

        int tempTroop = this.getAmountTroopCapacityCanBeTrained(user, deltaTime);
        
        if(tempTroop <= troopAvai){
            this.finishCheckBarrack(user, deltaTime);
            return;
        }
        
        //Voi deltaTime luc nay, temp <= troopAvai
        int i = 1;
        long tempTime = deltaTime;
        //Truong hop tempTime <= 1000 ma temp > troopAvai
        long tempTimeLoop = 0L;
        while(tempTime > 1000){
            if(tempTroop < troopAvai){
                tempTime /= 2;
                deltaTime += tempTime;
            }else if(tempTroop > troopAvai){
                tempTime /= 2;
                deltaTime -= tempTime;
            }else{
                this.finishCheckBarrack(user, deltaTime);
                return;
            }
            tempTroop = this.getAmountTroopCapacityCanBeTrained(user, deltaTime);
            if(tempTroop <= troopAvai) {
                tempTimeLoop = deltaTime;
            }
            i++;
        }
        
        //Luc nay, deltaTime da ok
        this.finishCheckBarrack(user, tempTimeLoop);
        
        //Set lai startTime cho Barrack dang STOP
        for(int j = 0; j < barrackQueueList.size(); j++) {
            barrackQueue = barrackQueueList.get(j);
            if(barrackQueue.isStop == ServerConstant.YES){
                System.out.println("==== Set lai time ======");
                barrackQueue.startTime = System.currentTimeMillis() - barrackQueue.trainTroopList.get(0).getTrainingTime() * 1000 - 2500;
            }
        }
        
        return;
    }
    
    private void finishCheckBarrack(User user, long _deltaTime) {
        BarrackQueue barrackQueue;
        long deltaTime;
        long minStartTime = this.getMinStartTime(user);
        
        ZPUserInfo userInfo;
        try {
            userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
        } catch (Exception e) {
            return;
        }

        for(int j = 0; j < barrackQueueList.size(); j++) {
            barrackQueue = barrackQueueList.get(j);
            if(this.isBarrackUpgrading(user, barrackQueue.getId()) || (barrackQueue.isStop == ServerConstant.YES)){
                continue;
            }
            
            deltaTime = _deltaTime - (barrackQueue.startTime - minStartTime);

            //troopList
            if(barrackQueue.getAmountItemInQueue() > 0 && barrackQueue.getTotalTroopCapacity() > 0){
                long amountTrainedTroop = 0;
                TroopInBarrack troopInBarrack;
                
                for(int i = 0; i < barrackQueue.getAmountItemInQueue(); i++) {
                    troopInBarrack = barrackQueue.trainTroopList.get(i);
                    amountTrainedTroop = deltaTime / (troopInBarrack.getTrainingTime() * 1000);
                    if(amountTrainedTroop >= troopInBarrack.getAmount()){
                        int temp = troopInBarrack.getAmount();
                        barrackQueue.updateQueue(i);
                        troopInBarrack.setAmount(0);
                        
                        //Tang so luong linh cua troop do
                        userInfo.increaseAmountTroop(troopInBarrack.getName(), temp);
                              
                        if(barrackQueue.getAmountItemInQueue() == 0){
                            barrackQueue.startTime = 0;
                            break;
                        }
                        
                        deltaTime -=  temp * troopInBarrack.getTrainingTime() * 1000;
                        if(deltaTime == 0) break;
                        i--;
                    }else if(amountTrainedTroop >= 1){
                        userInfo.increaseAmountTroop(troopInBarrack.getName(), (int) amountTrainedTroop);
                        troopInBarrack.setAmount(troopInBarrack.getAmount() - (int) amountTrainedTroop);
                        barrackQueue.startTime = System.currentTimeMillis() - (deltaTime - amountTrainedTroop * troopInBarrack.getTrainingTime() * 1000);
                        break;
                    }else{
                        System.out.println("============================== Chua train duoc con nao");
                        break;
                    }
                }
                
            } 
        }
    }
    
    public BarrackQueue getBarrackQueueById(int id) {
        BarrackQueue barrackQueue;
        for(int i = 0; i < barrackQueueList.size(); i++) {
            barrackQueue = barrackQueueList.get(i);
            if(barrackQueue.getId() == id) return barrackQueue;
        }
        return null;
    }
    
    public long getMinStartTime(User user) {
        long minTime = 1893415558482L;              //nam 2030
        BarrackQueue barrackQueue;
        
        for(int i = 0; i < barrackQueueList.size(); i++) {
            barrackQueue = barrackQueueList.get(i);
            if(!this.isBarrackUpgrading(user, barrackQueue.getId()) 
               && barrackQueue.startTime < minTime 
               && barrackQueue.getAmountItemInQueue() > 0){
                minTime = barrackQueue.startTime;
            }
        }
        return minTime;
    }
    
    private boolean isBarrackUpgrading(User user, int barrackId) {
        boolean bool = false;
        MapInfo mapInfo;
        try {
            mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
        } catch (Exception e) {
            return false;
        }
        
        List<Building> listBuilding = mapInfo.listBuilding;
        Iterator<Building> i = listBuilding.iterator();
        while (i.hasNext()) {
            Building build = i.next();

            if(build.getId() == barrackId && build.status.equals("upgrade") && build.type.equals(ServerConstant.BARRACK_TYPE)){
                bool = true;
                break;
            }
        }
        return bool;
    }
    
    public void updateWhenBarrackStartUpgrade(int id) {
        BarrackQueue newBarrackQueue = this.getBarrackQueueById(id);
        int index = barrackQueueList.indexOf(newBarrackQueue);
        
        //VD: dang train co start time = 8h15'00
        //Luc upgrade co time = 8h 20'00
        //Dat lai startTime = 5'00
        //Luc upgrade xong (finish time hoac quickfinish, hoac check) co time = 9h00
        //Dat lai startTime = 9h00 - 5'
        newBarrackQueue.startTime = System.currentTimeMillis() - newBarrackQueue.startTime;
        barrackQueueList.set(index, newBarrackQueue);
    }
    
    public void updateWhenBarrackUpgraded(int id) {
        BarrackQueue newBarrackQueue = this.getBarrackQueueById(id);
        int index = barrackQueueList.indexOf(newBarrackQueue);
        newBarrackQueue.setBarrackLevel(newBarrackQueue.getBarrackLevel() + 1);
        
        //Dat lai startTime cho barrack
        newBarrackQueue.startTime = System.currentTimeMillis() - newBarrackQueue.startTime;
        this.barrackQueueList.set(index, newBarrackQueue);
    }
    
    public void updateWhenBarrackCancelUpgrade(int id) {
        BarrackQueue newBarrackQueue = this.getBarrackQueueById(id);
        int index = barrackQueueList.indexOf(newBarrackQueue);
        
        //Dat lai startTime cho barrack
        newBarrackQueue.startTime = System.currentTimeMillis() - newBarrackQueue.startTime;
        barrackQueueList.set(index, newBarrackQueue);
    }
    
    public void updateWhenBarrackBuilt(int id) {
        BarrackQueue barrackQueue = new BarrackQueue(id, 1);
        barrackQueue.setId(id);
        barrackQueueList.add(barrackQueue);
    }
    
    public void print() {
        System.out.println("======================= BarrackQueueInfo ======================");
        BarrackQueue barrackQueue;
        int i = 1;
        for(int j = 0; j < barrackQueueList.size(); j++) {
            barrackQueue = barrackQueueList.get(j);
            //id cua Barrack
            System.out.println("-------------- BarrackQueue thu " + i);
            System.out.println("id Barrack " + barrackQueue.getId());
            System.out.println("level Barrack " + barrackQueue.getBarrackLevel());
            System.out.println("amountItemInQueue " + barrackQueue.getAmountItemInQueue());
            System.out.println("totalTroopCapacity " + barrackQueue.getTotalTroopCapacity());
            System.out.println("startTime " + barrackQueue.startTime);
            
            //troopList
            TroopInBarrack troopInBarrack;
            System.out.println("-------------- TroopList --------------");
            for(int m = 0; m < barrackQueue.trainTroopList.size(); m++) {
                troopInBarrack = barrackQueue.trainTroopList.get(m);
                System.out.println("-------------- Troop thu " + (m + 1));
                System.out.println("Type troop: --- " + troopInBarrack.getName());
                System.out.println("Amount troop in queue: --- " + troopInBarrack.getAmount());
                j++;
            }
            System.out.println("-------------- END TroopList ======================");
            System.out.println("-------------- END BarrackQueue thu " + i);
            i++;
        }
        System.out.println("=======================END BarrackQueueInfo ======================");
    }
}
