package service;

import bitzero.server.core.BZEventType;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseClientRequestHandler;

import bitzero.server.extensions.data.DataCmd;

import cmd.CmdDefine;

import cmd.receive.train.RequestBarrackQueueInfo;

import cmd.receive.train.RequestCancelTrainTroop;
import cmd.receive.train.RequestFinishTimeTrainTroop;
import cmd.receive.train.RequestTrainTroop;
import cmd.receive.troop.RequestTroopInfo;

import cmd.send.demo.ResponseRequestMapInfo;
import cmd.send.demo.ResponseRequestQuickFinish;
import cmd.send.demo.ResponseResearch;
import cmd.send.demo.ResponseTroopInfo;

import cmd.send.train.ResponseRequestCancelTrainTroop;
import cmd.send.train.ResponseRequestBarrackQueueInfo;

import cmd.send.train.ResponseRequestFinishTimeTrainTroop;
import cmd.send.train.ResponseRequestTrainTroop;

import model.MapInfo;
import model.Troop;
import model.TroopInfo;

import model.ZPUserInfo;

import model.train.BarrackQueue;
import model.train.BarrackQueueInfo;

import model.train.TroopInBarrack;

import org.apache.commons.lang.exception.ExceptionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.server.ServerConstant;

public class TrainTroopHandle extends BaseClientRequestHandler {
    public static short TRAINTROOP_MULTI_IDS = 7000;
    private final Logger logger = LoggerFactory.getLogger("TroopHandle");
    public TrainTroopHandle() {
        super();
    }
    public void init() {
        getParentExtension().addEventListener(BZEventType.PRIVATE_MESSAGE, this);        
    }
    @Override
    public void handleClientRequest(User user, DataCmd dataCmd) {
        try {
            System.out.println("dataCmd.getId()" + dataCmd.getId());
            switch (dataCmd.getId()) {
                case CmdDefine.GET_BARRACK_QUEUE_INFO:
                    processRequestBarrackQueueInfo(user);
                    break;
                case CmdDefine.TRAIN_TROOP:
                    RequestTrainTroop trainTroopPacket = new RequestTrainTroop(dataCmd);
                    processRequestTrainTroop(user, trainTroopPacket);
                    break;
                case CmdDefine.CANCEL_TRAIN_TROOP:
                    RequestCancelTrainTroop cancelPacket = new RequestCancelTrainTroop(dataCmd);
                    processRequestCancelTrainTroop(user, cancelPacket);
                    break;
                case CmdDefine.QUICK_FINISH_TRAIN_TROOP:
                
                    break;
                case CmdDefine.FINISH_TIME_TRAIN_TROOP:
                    RequestFinishTimeTrainTroop finishTimePacket = new RequestFinishTimeTrainTroop(dataCmd);
                    processRequestFinishTimeTrainTroop(user, finishTimePacket);
                    break;
            }
        } catch (Exception e) {
            logger.warn("DEMO HANDLER EXCEPTION " + e.getMessage());
            logger.warn(ExceptionUtils.getStackTrace(e));
        }
    }
    
    private void processRequestBarrackQueueInfo(User user) {
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               //send response error
               send(new ResponseRequestQuickFinish(ServerConstant.ERROR), user);
               return;
            }
            
            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
            if(barrackQueueInfo == null){
                System.out.println("======================= BarrackQueueInfo null ======================");
                barrackQueueInfo = new BarrackQueueInfo();  
            }else {
                checkFirst(barrackQueueInfo, user);
            }
            barrackQueueInfo.saveModel(user.getId());
            send(new ResponseRequestBarrackQueueInfo(barrackQueueInfo), user);
            
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void processRequestTrainTroop(User user, RequestTrainTroop packet) {
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               //send response error
               send(new ResponseRequestTrainTroop(ServerConstant.ERROR), user);
               return;
            }
            
            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
            if(barrackQueueInfo == null){
                System.out.println("======================= BarrackQueueInfo null ======================");
                //send response error
                send(new ResponseRequestTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            BarrackQueue barrackQueue = barrackQueueInfo.barrackQueueMap.get(packet.idBarrack);
            if(barrackQueue == null){
                System.out.println("======================= BarrackQueue null ======================");
                //send response error
                send(new ResponseRequestTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            TroopInBarrack troop = barrackQueue.troopListMap.get(packet.typeTroop);
            
            //check capacity
            if(barrackQueue.totalTroopCapacity + troop.housingSpace > barrackQueue.queueLength){
                System.out.println("======================= Train Troop over Barrack Queue length ======================");
                System.out.println("Total Troop Capacity: " + barrackQueue.totalTroopCapacity);
                System.out.println("Current housingSpace: " + troop.housingSpace);
                System.out.println("Queue length: " + barrackQueue.queueLength);
                System.out.println("Barrack Level: " + barrackQueue.barrackLevel);
                //send response error
                send(new ResponseRequestTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            //
            
            //check tai nguyen
            
            
            //Truoc
            System.out.println("============================================TRUOC");
            System.out.println("totalTroopCapacity: " + barrackQueue.totalTroopCapacity);
            System.out.println("amountItemInQueue: " + barrackQueue.amountItemInQueue);
            System.out.println("Troop currentPosition: " + troop.currentPosition);
            System.out.println("Troop Amount: " + troop.amount);
            
            //OK het
            barrackQueue.totalTroopCapacity += troop.housingSpace;
            
            if(barrackQueue.amountItemInQueue == 0){
                barrackQueue.startTime = System.currentTimeMillis();
                barrackQueue.amountItemInQueue++;
                troop.currentPosition = 0;
                System.out.println("currentPosition: " + troop.currentPosition);
            }else{
                if(troop.amount == 0){
                    barrackQueue.amountItemInQueue++;
                    troop.currentPosition = barrackQueue.amountItemInQueue - 1;
                    System.out.println("currentPosition: " + troop.currentPosition);
                }
            }
            troop.amount++;
            
            System.out.println("============================================SAU");
            System.out.println("totalTroopCapacity: " + barrackQueue.totalTroopCapacity);
            System.out.println("amountItemInQueue: " + barrackQueue.amountItemInQueue);
            System.out.println("Troop currentPosition: " + troop.currentPosition);
            System.out.println("Troop Amount: " + troop.amount);
            
            
            barrackQueue.troopListMap.put(packet.typeTroop, troop);
            barrackQueueInfo.barrackQueueMap.put(packet.idBarrack, barrackQueue);
            barrackQueueInfo.saveModel(user.getId());
            send(new ResponseRequestTrainTroop(ServerConstant.SUCCESS), user);    
          
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void processRequestCancelTrainTroop(User user, RequestCancelTrainTroop packet) {
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               //send response error
               send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
               return;
            }
            
            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
            if(barrackQueueInfo == null){
                System.out.println("======================= BarrackQueueInfo null ======================");
                //send response error
                send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            BarrackQueue barrackQueue = barrackQueueInfo.barrackQueueMap.get(packet.idBarrack);
            if(barrackQueue == null){
                System.out.println("======================= BarrackQueue null ======================");
                //send response error
                send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            TroopInBarrack troop = barrackQueue.troopListMap.get(packet.typeTroop);
            
            //check capacity
            if(barrackQueue.totalTroopCapacity - troop.housingSpace < 0){
                //send response error
                send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            //
            
            //check tai nguyen
            
            
            //Truoc
            System.out.println("============================================TRUOC");
            System.out.println("totalTroopCapacity: " + barrackQueue.totalTroopCapacity);
            System.out.println("amountItemInQueue: " + barrackQueue.amountItemInQueue);
            System.out.println("Troop currentPosition: " + troop.currentPosition);
            System.out.println("Troop Amount: " + troop.amount);
            
            
            troop.amount--;
            if(troop.amount < 0){
                //send response error
                send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            barrackQueue.totalTroopCapacity -= troop.housingSpace;
            if(troop.amount == 0){
                barrackQueue.amountItemInQueue--;
                if(barrackQueue.amountItemInQueue != 0){
                    barrackQueue.updateQueue(troop.currentPosition);
                }
                troop.currentPosition = -1;
            }
            
            
            
            System.out.println("============================================SAU");
            System.out.println("totalTroopCapacity: " + barrackQueue.totalTroopCapacity);
            System.out.println("amountItemInQueue: " + barrackQueue.amountItemInQueue);
            System.out.println("Troop currentPosition: " + troop.currentPosition);
            System.out.println("Troop Amount: " + troop.amount);
            
            
            barrackQueue.troopListMap.put(packet.typeTroop, troop);
            barrackQueueInfo.barrackQueueMap.put(packet.idBarrack, barrackQueue);
            barrackQueueInfo.saveModel(user.getId());
            send(new ResponseRequestCancelTrainTroop(ServerConstant.SUCCESS), user);    
          
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void processRequestFinishTimeTrainTroop(User user, RequestFinishTimeTrainTroop packet) {
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               //send response error
               send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.ERROR), user);
               return;
            }
            
            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
            if(barrackQueueInfo == null){
                System.out.println("======================= BarrackQueueInfo null ======================");
                //send response error
                send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            BarrackQueue barrackQueue = barrackQueueInfo.barrackQueueMap.get(packet.idBarrack);
            if(barrackQueue == null){
                System.out.println("======================= BarrackQueue null ======================");
                //send response error
                send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            TroopInBarrack troop = barrackQueue.troopListMap.get(packet.typeTroop);
            int remainTroop = packet.remainTroop;
            
            //Truoc
            System.out.println("============================================TRUOC");
            System.out.println("totalTroopCapacity: " + barrackQueue.totalTroopCapacity);
            System.out.println("amountItemInQueue: " + barrackQueue.amountItemInQueue);
            System.out.println("Troop currentPosition: " + troop.currentPosition);
            System.out.println("Troop Amount: " + troop.amount);
            
            JSONObject troopBaseConfig = ServerConstant.configTroopBase;
            long timeTrain;
            try {
                timeTrain = troopBaseConfig.getJSONObject(packet.typeTroop).getLong("trainingTime") * 1000;
            } catch (JSONException e) {
                return;
            }
            
            long time_cur = System.currentTimeMillis();
            long time_da_chay = time_cur - barrackQueue.startTime;
            
            System.out.println("============================================ HERE 1===========================");
            if ((time_da_chay >= timeTrain) && remainTroop == troop.amount - 1){
                troop.amount--;
                //Tang so luong loai troop nay
                System.out.println("============================================ HERE 2===========================");
                TroopInfo troopInfo = (TroopInfo) TroopInfo.getModel(user.getId(), TroopInfo.class);
                if (troopInfo == null) {
                    //send response error
                    send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.ERROR), user);
                    return;
                }
                Troop troopObj = troopInfo.troopMap.get(packet.typeTroop);
                troopObj.population++;
                troopInfo.troopMap.put(packet.typeTroop, troopObj);
                troopInfo.saveModel(user.getId());
                System.out.println("============================================ HERE 3===========================");

                barrackQueue.totalTroopCapacity -= troop.housingSpace;
                
                //Het icon trong item
                if(troop.amount == 0){
                    barrackQueue.amountItemInQueue--;              
                    if(barrackQueue.amountItemInQueue != 0){
                        barrackQueue.updateQueue(troop.currentPosition);
                    }
                    troop.currentPosition = -1;
                }else{
                    barrackQueue.startTime = System.currentTimeMillis();    
                }
            }
            

            System.out.println("============================================SAU");
            System.out.println("totalTroopCapacity: " + barrackQueue.totalTroopCapacity);
            System.out.println("amountItemInQueue: " + barrackQueue.amountItemInQueue);
            System.out.println("Troop currentPosition: " + troop.currentPosition);
            System.out.println("Troop Amount: " + troop.amount);
            
            
            barrackQueue.troopListMap.put(packet.typeTroop, troop);
            barrackQueueInfo.barrackQueueMap.put(packet.idBarrack, barrackQueue);
            barrackQueueInfo.saveModel(user.getId());
            send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.SUCCESS), user);    
          
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void increaseAmountTroop(User user, String typeTroop, int amount) {
        //Tang so luong loai troop nay
        TroopInfo troopInfo;
        try {
            troopInfo = (TroopInfo) TroopInfo.getModel(user.getId(), TroopInfo.class);
        } catch (Exception e) {
            return;
        }
        
        Troop troopObj = troopInfo.troopMap.get(typeTroop);
        troopObj.population += amount;
        troopInfo.troopMap.put(typeTroop, troopObj);
        try {
            troopInfo.saveModel(user.getId());
        } catch (Exception e) {
        }
    }
        
    public void checkFirst(BarrackQueueInfo barrackQueueInfo, User user) {
        long time_da_chay;      //ms
        
        BarrackQueue barrackQueue;     
        for (Integer idBarrack : barrackQueueInfo.barrackQueueMap.keySet()) {
            barrackQueue = barrackQueueInfo.barrackQueueMap.get(idBarrack);
            if(barrackQueue.startTime == 0){
                continue;
            }
            time_da_chay = System.currentTimeMillis() - barrackQueue.startTime;
            //troopList
            if(barrackQueue.amountItemInQueue > 0 && barrackQueue.totalTroopCapacity > 0){
                long amountTrainedTroop = 0;
                TroopInBarrack troopInBarrack;
                int i = 1;
                while(true){
                    System.out.println("============================================ Lan while thu: " + i);
                    try {
                        troopInBarrack = barrackQueue.getTroopByPosition(0);
                    } catch (Exception e) {
                        System.out.println("============================================ Loi o day " + e);
                        return;
                    }
                    
                    amountTrainedTroop = time_da_chay / (troopInBarrack.trainingTime * 1000);
                    System.out.println("============================================ Amount Trained Troops: " + amountTrainedTroop);
                    if(amountTrainedTroop >= troopInBarrack.amount){
                        System.out.println("============================================ amountTrainedTroop >= troopInBarrack");
                        barrackQueue.updateQueue(troopInBarrack.currentPosition);
                        troopInBarrack.currentPosition = -1;
                        increaseAmountTroop(user, troopInBarrack.name, troopInBarrack.amount);
                         
                        barrackQueue.amountItemInQueue--;
                        barrackQueue.totalTroopCapacity -= troopInBarrack.amount * troopInBarrack.housingSpace;
                        troopInBarrack.amount = 0;
                        if(amountTrainedTroop > troopInBarrack.amount){
                            if(barrackQueue.amountItemInQueue == 0){
                                break;
                            }
                            time_da_chay -=  amountTrainedTroop * troopInBarrack.trainingTime * 1000;
                            System.out.println("============================================ amountTrainedTroop > troopInBarrack");
                        }else{
                            System.out.println("============================================ amountTrainedTroop = troopInBarrack");
                            barrackQueue.startTime = System.currentTimeMillis();
                            break;
                        }
                    }else{
                        System.out.println("============================================ amountTrainedTroop < troopInBarrack");
                        increaseAmountTroop(user, troopInBarrack.name, (int) amountTrainedTroop);
                        troopInBarrack.amount -= amountTrainedTroop;
                        barrackQueue.totalTroopCapacity -= amountTrainedTroop * troopInBarrack.housingSpace;
                        barrackQueue.startTime = System.currentTimeMillis() - (time_da_chay - amountTrainedTroop * troopInBarrack.trainingTime);
                        break;
                    }
                    i++;
                }
            } 
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}
