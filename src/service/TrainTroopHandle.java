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
import cmd.send.train.ResponseRequestQuickFinishTrainTroop;
import cmd.receive.troop.RequestQuickFinishTrainTroop;

import cmd.send.demo.ResponseRequestQuickFinish;

import cmd.send.train.ResponseRequestCancelTrainTroop;
import cmd.send.train.ResponseRequestBarrackQueueInfo;

import cmd.send.train.ResponseRequestFinishTimeTrainTroop;
import cmd.send.train.ResponseRequestTrainTroop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import java.util.List;

import java.util.Map;

import model.Building;
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
                    RequestQuickFinishTrainTroop quickFinishPacket = new RequestQuickFinishTrainTroop(dataCmd);
                    processQuickFinishTrainTroop(user, quickFinishPacket);
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
                System.out.println("======================= userInfo null ======================");
               //send response error
               send(new ResponseRequestTrainTroop(ServerConstant.ERROR), user);
               return;
            }
            
            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
            if(barrackQueueInfo == null){
                System.out.println("======================= BarrackQueueInfo null 1======================");
                //send response error
                send(new ResponseRequestTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            BarrackQueue barrackQueue = barrackQueueInfo.barrackQueueMap.get(packet.idBarrack);
            if(barrackQueue == null){
                System.out.println("======================= BarrackQueue null 2======================");
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
                System.out.println("======================= check capacity false======================");
                send(new ResponseRequestTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            //check tai nguyen
            int levelTroop = getTroopLevel(user, packet.typeTroop);
            int trainingElixir = getElixirCost(packet.typeTroop, levelTroop);
            int trainingDarkElixir = getDarkElixirCost(packet.typeTroop, levelTroop);
            
            int g = checkResource(userInfo, trainingElixir, trainingDarkElixir);
            reduceUserResources(userInfo, trainingElixir, trainingDarkElixir, g);
            userInfo.saveModel(user.getId());
            
            
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
            
            MapInfo mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {               
               //send response error
               send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
               return;
            }
            
            //check tai nguyen
            int levelTroop = getTroopLevel(user, packet.typeTroop);
            int trainingElixir = getElixirCost(packet.typeTroop, levelTroop);
            int trainingDarkElixir = getDarkElixirCost(packet.typeTroop, levelTroop);
            System.out.println("============================================ trainingElixir " + trainingElixir);
            System.out.println("============================================ trainingDarkElixir " + trainingDarkElixir);

            
            //refund tai nguyen
            int gold_rq = mapInfo.getRequire(ServerConstant.gold_capacity, ServerConstant.gold_sto);    
            int elx_rq = mapInfo.getRequire(ServerConstant.elixir_capacity, ServerConstant.elixir_sto);
            int dElx_rq = mapInfo.getRequire(ServerConstant.darkElixir_capacity, ServerConstant.darkElixir_sto);
            
            userInfo.addResource(0,trainingElixir,trainingDarkElixir,0,gold_rq,elx_rq,dElx_rq);
            
            System.out.println("============================================ Elixir User " + userInfo.elixir);
            System.out.println("============================================ Dark Elixir User " + userInfo.darkElixir);

            
            userInfo.saveModel(user.getId());
            
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
    
    private void processQuickFinishTrainTroop(User user, RequestQuickFinishTrainTroop packet) {
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               //send response error
               send(new ResponseRequestQuickFinishTrainTroop(ServerConstant.ERROR), user);
               return;
            }
            
            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
            if(barrackQueueInfo == null){
                System.out.println("======================= BarrackQueueInfo null ======================");
                //send response error
                send(new ResponseRequestQuickFinishTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            BarrackQueue barrackQueue = barrackQueueInfo.barrackQueueMap.get(packet.idBarrack);
            if(barrackQueue == null){
                System.out.println("======================= BarrackQueue null ======================");
                //send response error
                send(new ResponseRequestQuickFinishTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            //Truoc
            System.out.println("============================================TRUOC");
            System.out.println("totalTroopCapacity: " + barrackQueue.totalTroopCapacity);
            System.out.println("amountItemInQueue: " + barrackQueue.amountItemInQueue);
            
            //Neu quick finish xong vuot qua capacity cua AMC thi khong cho
            
            //
            
            JSONObject troopBaseConfig = ServerConstant.configTroopBase;
            long timeTrain;
            Map <String, TroopInBarrack> troopListMap = barrackQueue.troopListMap;
            
            TroopInfo troopInfo = (TroopInfo) TroopInfo.getModel(user.getId(), TroopInfo.class);
            if (troopInfo == null) {
                //send response error
                send(new ResponseRequestQuickFinishTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            int time = 0;
            TroopInBarrack troopInBarrack;
            for (String troopType : troopListMap.keySet()) {
                troopInBarrack = troopListMap.get(troopType);
                try {
                    timeTrain = troopBaseConfig.getJSONObject(troopType).getLong("trainingTime");
                } catch (JSONException e) {
                    return;
                }
                
                time += troopInBarrack.amount * timeTrain;
                
                Troop troopObj = troopInfo.troopMap.get(troopType);
                troopObj.population += troopInBarrack.amount;
                troopInfo.troopMap.put(troopType, troopObj);
            }
            
            troopInfo.saveModel(user.getId());
            
            
            reduceUserResources(userInfo, 0, 0, timeToG(time));
            userInfo.saveModel(user.getId());
            System.out.println("============================================SAU");
            System.out.println("totalTroopCapacity: " + barrackQueue.totalTroopCapacity);
            System.out.println("amountItemInQueue: " + barrackQueue.amountItemInQueue);
            
//            troopListMap.clear();
            barrackQueue.doReset();            
            
            barrackQueueInfo.barrackQueueMap.put(packet.idBarrack, barrackQueue);
            barrackQueueInfo.saveModel(user.getId());
            send(new ResponseRequestQuickFinishTrainTroop(ServerConstant.SUCCESS), user);    
          
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void increaseAmountTroop(User user, String typeTroop, int amount) {
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
    
    
    private void checkFirst(BarrackQueueInfo barrackQueueInfo, User user) {
        long deltaTime;      //ms
        
        BarrackQueue barrackQueue;     
        for (Integer idBarrack : barrackQueueInfo.barrackQueueMap.keySet()) {
            barrackQueue = barrackQueueInfo.barrackQueueMap.get(idBarrack);
            if(barrackQueue.startTime == 0){
                continue;
            }
            
            //Neu trc currentTroop >= AMCs capacity thi khong can kiem tra
            //Cap nhat capacity AMCs
            int totalAMCsCapacity = getTotalCapacityAMCs(user);
            //So linh tinh tu luc off
            int troopCapacityBefore = getCurrentCapacityTroop(user);
            
            if(totalAMCsCapacity - troopCapacityBefore <= 0){
                System.out.println("================================= Dang max capacity, khong the check finish time");
                return;
            }
            
            
            deltaTime = System.currentTimeMillis() - barrackQueue.startTime;
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
                    
                    amountTrainedTroop = deltaTime / (troopInBarrack.trainingTime * 1000);
                    System.out.println("============================================ Amount Trained Troops: " + amountTrainedTroop);
                    if(amountTrainedTroop >= troopInBarrack.amount){
                        System.out.println("============================================ amountTrainedTroop >= troopInBarrack");
                        barrackQueue.updateQueue(troopInBarrack.currentPosition);
                        troopInBarrack.currentPosition = -1;
                        //Tang so luong linh cua troop do
                        increaseAmountTroop(user, troopInBarrack.name, troopInBarrack.amount);
                         
                        barrackQueue.amountItemInQueue--;
                        barrackQueue.totalTroopCapacity -= troopInBarrack.amount * troopInBarrack.housingSpace;
                        
                        troopInBarrack.amount = 0;
                        if(barrackQueue.amountItemInQueue == 0){
                            barrackQueue.startTime = 0;
                            break;
                        }
                        deltaTime -=  amountTrainedTroop * troopInBarrack.trainingTime * 1000;
                        if(deltaTime == 0){
                            break;
                        }                    
                    }else if(amountTrainedTroop >= 1){
                        System.out.println("============================================ 1 <= amountTrainedTroop < troopInBarrack");
                        increaseAmountTroop(user, troopInBarrack.name, (int) amountTrainedTroop);
                        troopInBarrack.amount -= amountTrainedTroop;
                        barrackQueue.totalTroopCapacity -= amountTrainedTroop * troopInBarrack.housingSpace;
                        barrackQueue.startTime = System.currentTimeMillis() - (deltaTime - amountTrainedTroop * troopInBarrack.trainingTime);
                        break;
                    }
                    i++;
                }
            } 
        }
    }
    
    
    public void checkFirstTest(BarrackQueueInfo barrackQueueInfo, User user) {
        //Cap nhat capacity AMCs
        int totalAMCsCapacity = getTotalCapacityAMCs(user);
        
        //So linh tinh tu luc off
        int troopCapacityBefore = getCurrentCapacityTroop(user);
        
        //So capacity linh toi da co the train
        int troopAvai = totalAMCsCapacity - troopCapacityBefore;
        
        
        //Thoi diem offline
        long timeOff = barrackQueueInfo.getMaxStartTime();
        if(timeOff == 0) return;
        long currentTime = System.currentTimeMillis();
        
        long timeAvai = 2700 * 3 + 600 * 1;                  //25x3 + 10x1 = 85 (max queue length barrack level 12)
        long deltaTime = currentTime - timeOff;
        if(deltaTime / 1000 > timeAvai) deltaTime = timeAvai;
        
        int temp = amountTroopCapacityCanBeTrained(barrackQueueInfo, deltaTime);
        if(temp <= troopAvai){
            //OK
            //Goi ham thay doi so luong linh voi deltaTime nay
            //return
        }

        while(temp > troopAvai && deltaTime > 0){
            deltaTime /= 2;
            temp = amountTroopCapacityCanBeTrained(barrackQueueInfo, deltaTime);
        }
        
        //Voi deltaTime luc nay, temp <= troopAvai
        
        int i = 1;
        while(deltaTime > 0){
            if(temp <= troopAvai){
                deltaTime = deltaTime + deltaTime / 2;
            }else{
                deltaTime /= 2;
            }
            temp = amountTroopCapacityCanBeTrained(barrackQueueInfo, deltaTime);
            i++;
        }
        
        //Luc nay, deltaTime da ok
            
    }
    
    //So unit capacity linh co the train trong khoang tgian deltaTime, k bi rang buoc boi AMCs capacity
    private int amountTroopCapacityCanBeTrained(BarrackQueueInfo barrackQueueInfo, long _deltaTime) {
        long deltaTime = _deltaTime;
        int totalCapacity = 0;
        
        BarrackQueue barrackQueue;     
        for (Integer idBarrack : barrackQueueInfo.barrackQueueMap.keySet()) {
            barrackQueue = barrackQueueInfo.barrackQueueMap.get(idBarrack);
            if(barrackQueue.startTime == 0){
                continue;
            }
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
                        return 0;
                    }
                    
                    amountTrainedTroop = deltaTime / troopInBarrack.trainingTime;
                    if(amountTrainedTroop >= troopInBarrack.amount){
                        System.out.println("============================================ amountTrainedTroop >= troopInBarrack");
                        barrackQueue.updateQueue(troopInBarrack.currentPosition);
                        totalCapacity += troopInBarrack.amount * troopInBarrack.housingSpace;
                         
                        barrackQueue.amountItemInQueue--;
                        if(barrackQueue.amountItemInQueue == 0){
                            break;
                        }
                        deltaTime -=  amountTrainedTroop * troopInBarrack.trainingTime;
                        if(deltaTime == 0){
                            break;
                        }    
                    }else if(amountTrainedTroop >= 1){
                        System.out.println("============================================ 1 <= amountTrainedTroop < troopInBarrack");
                        totalCapacity += amountTrainedTroop * troopInBarrack.housingSpace;
                        break;
                    }
                    i++;
                }
            } 
        }
        return totalCapacity;
    }
    
    private int getElixirCost(String troopType, int level) {
            JSONObject troopConfig = ServerConstant.configTroop;
            int trainingElixir = 0;
            try {
                trainingElixir = troopConfig.getJSONObject(troopType).getJSONObject(Integer.toString(level)).getInt("trainingElixir");
            } catch (JSONException e) {
                
            }
            return trainingElixir;
        }
        
    private int getDarkElixirCost(String troopType, int level) {
        JSONObject troopConfig = ServerConstant.configTroop;
        int trainingDarkElixir = 0;
        try {
            trainingDarkElixir = troopConfig.getJSONObject(troopType).getJSONObject(Integer.toString(level)).getInt("trainingDarkElixir");
        } catch (JSONException e) {
              
        }
        return trainingDarkElixir;
    }
        
    private int getTroopLevel(User user, String type) {
        try {
            TroopInfo troopInfo = (TroopInfo) TroopInfo.getModel(user.getId(), TroopInfo.class);
            Troop troop = troopInfo.troopMap.get(type);
            return (int) troop.level;
        } catch (Exception e) {
            return -1;
        }
    }
        
    private int elixirToG(int elixir_bd) {
        return elixir_bd;
    }

    private int darkElixirToG(int darkElixir_bd) {
        return darkElixir_bd;
    }
    
    private int timeToG(int time) {
        return (int) Math.ceil(time/60.0);
    }
        
    private int checkResource(ZPUserInfo user, int elixir, int darkElixir) {
        int g = 0;
        
        if (user.elixir < elixir){
            g += elixirToG(elixir - user.elixir);                    
        };
        
        if (user.darkElixir < darkElixir){
            g += darkElixirToG(darkElixir - user.darkElixir);                    
        };
        
        return g;
    }
        
    private void reduceUserResources(ZPUserInfo user, int elixir, int darkElixir, int coin){
        //tru elixir
        if (user.elixir < elixir){
            user.elixir = 0;     
        }else {
            user.elixir = user.elixir - elixir;
        }
        if (user.darkElixir < darkElixir){
            user.darkElixir = 0;
        }else {
            user.darkElixir = user.darkElixir - darkElixir;
        }
        
        user.coin = user.coin - coin;
    }
        
    private void increaseUserResources(ZPUserInfo user, int elixir, int darkElixir, int coin){
       //tang tien cua ng choi
    }
    
    private int getTotalCapacityAMCs(User user) {
        int total = 0;
        MapInfo mapInfo;
        try {
            mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
        } catch (Exception e) {
            return 0;
        }
        
        JSONObject amcConfig;
        try {
            amcConfig = ServerConstant.configArmyCamp.getJSONObject("AMC_1");
        } catch (JSONException e) {
            return 0;
        }


        List<Building> listBuilding = mapInfo.listBuilding;

        Iterator<Building> i = listBuilding.iterator();
        while (i.hasNext()) {
            Building build = i.next();
            
            if((build.status == "complete" || build.status == "upgrade") && (build.type == "AMC_1")){
                int capacity;

                try {
                    capacity = amcConfig.getJSONObject(Integer.toString(build.level)).getInt("capacity");
                } catch (JSONException e) {
                    return 0;
                }
                total += capacity;
            }
        }
        return total;
    }
    
    private int getCurrentCapacityTroop(User user) {
        int total = 0;
        TroopInfo troopInfo;
        try {
            troopInfo = (TroopInfo) TroopInfo.getModel(user.getId(), TroopInfo.class);
        } catch (Exception e) {
            return 0;
        }
        
        JSONObject troopBaseConfig = ServerConstant.configTroopBase;
        
        int space;
        Troop troop;
        for (String troopType : troopInfo.troopMap.keySet()) {
            troop = troopInfo.troopMap.get(troopType);
            try {
                space = troopBaseConfig.getJSONObject(troopType).getInt("housingSpace");
            } catch (JSONException e) {
                return 0;
            }
            total += space * troop.population;
        }
        
        return total;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}
