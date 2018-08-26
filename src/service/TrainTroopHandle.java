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
import cmd.receive.train.RequestQuickFinishTrainTroop;

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
    private final Logger logger = LoggerFactory.getLogger("TrainTroopHandle");
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
//                checkFirst(barrackQueueInfo, user);
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
            if(barrackQueue.getTotalTroopCapacity() + troop.getHousingSpace() > barrackQueue.getQueueLength()){
                System.out.println("======================= Train Troop over Barrack Queue length ======================");
                System.out.println("Total Troop Capacity: " + barrackQueue.getTotalTroopCapacity());
                System.out.println("Current housingSpace: " + troop.getHousingSpace());
                System.out.println("Queue length: " + barrackQueue.getQueueLength());
                System.out.println("Barrack Level: " + barrackQueue.getBarrackLevel());
                //send response error
                System.out.println("======================= check capacity false======================");
                send(new ResponseRequestTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            //check tai nguyen
            int levelTroop = userInfo.getTroopLevel(packet.typeTroop);
            int trainingElixir = troop.getTrainingElixir(levelTroop);
            int trainingDarkElixir = troop.getTrainingDarkElixir(levelTroop);
            
            int g = checkResource(userInfo, trainingElixir, trainingDarkElixir);
            reduceUserResources(userInfo, trainingElixir, trainingDarkElixir, g);
            userInfo.saveModel(user.getId());
            
            //OK het
            barrackQueue.setTotalTroopCapacity(barrackQueue.getTotalTroopCapacity() + troop.getHousingSpace());
            
            if(barrackQueue.getAmountItemInQueue() == 0){
                barrackQueue.startTime = System.currentTimeMillis();
                barrackQueue.setAmountItemInQueue(1);
                troop.setCurrentPosition(0);
                System.out.println("currentPosition: " + troop.getCurrentPosition());
            }else{
                if(troop.getAmount() == 0){
                    barrackQueue.setAmountItemInQueue(barrackQueue.getAmountItemInQueue() + 1);
                    troop.setCurrentPosition(barrackQueue.getAmountItemInQueue() - 1);
                    System.out.println("currentPosition: " + troop.getCurrentPosition());
                }
            }
            troop.setAmount(troop.getAmount() + 1);
            
            
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
            if(barrackQueue.getTotalTroopCapacity() - troop.getHousingSpace() < 0){
                //send response error
                send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            troop.setAmount(troop.getAmount() - 1);
            if(troop.getAmount() < 0){
                //send response error
                send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            barrackQueue.setTotalTroopCapacity(barrackQueue.getTotalTroopCapacity() - troop.getHousingSpace());
            if(troop.getAmount() == 0){
                barrackQueue.setAmountItemInQueue(barrackQueue.getAmountItemInQueue() - 1);
                if(barrackQueue.getAmountItemInQueue() != 0){
                    barrackQueue.updateQueue(troop.getCurrentPosition());
                }
                troop.setCurrentPosition(-1);
            }
            
            MapInfo mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {               
               //send response error
               send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
               return;
            }
            
            //check tai nguyen
            int levelTroop = userInfo.getTroopLevel(packet.typeTroop);
            int trainingElixir = troop.getTrainingElixir(levelTroop);
            int trainingDarkElixir = troop.getTrainingDarkElixir(levelTroop);
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
               send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.ERROR, packet.idBarrack, packet.typeTroop), user);
               return;
            }
            
            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
            if(barrackQueueInfo == null){
                System.out.println("======================= BarrackQueueInfo null ======================");
                //send response error
                send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.ERROR, packet.idBarrack, packet.typeTroop), user);
                return;
            }
            
            BarrackQueue barrackQueue = barrackQueueInfo.barrackQueueMap.get(packet.idBarrack);
            if(barrackQueue == null){
                System.out.println("======================= BarrackQueue null ======================");
                //send response error
                send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.ERROR, packet.idBarrack, packet.typeTroop), user);
                return;
            }
            
            TroopInBarrack troop = barrackQueue.troopListMap.get(packet.typeTroop);
            int remainTroop = packet.remainTroop;
                    
            JSONObject troopBaseConfig = ServerConstant.configTroopBase;
            long timeTrain;
            try {
                timeTrain = troopBaseConfig.getJSONObject(packet.typeTroop).getLong("trainingTime") * 1000;
            } catch (JSONException e) {
                return;
            }
            
            long time_cur = System.currentTimeMillis();
            long time_da_chay = time_cur - barrackQueue.startTime;
            
            if ((time_da_chay >= timeTrain) && remainTroop == troop.getAmount() - 1){
                troop.setAmount(troop.getAmount() - 1);
                //Tang so luong loai troop nay
                TroopInfo troopInfo = (TroopInfo) TroopInfo.getModel(user.getId(), TroopInfo.class);
                if (troopInfo == null) {
                    //send response error
                    send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.ERROR, packet.idBarrack, packet.typeTroop), user);
                    return;
                }
                Troop troopObj = troopInfo.troopMap.get(packet.typeTroop);
                troopObj.population++;
                troopInfo.troopMap.put(packet.typeTroop, troopObj);
                troopInfo.saveModel(user.getId());

                barrackQueue.setTotalTroopCapacity(barrackQueue.getTotalTroopCapacity() - troop.getHousingSpace());
                
                //Het icon trong item
                if(troop.getAmount() == 0){
                    barrackQueue.setAmountItemInQueue(barrackQueue.getAmountItemInQueue() - 1);              
                    if(barrackQueue.getAmountItemInQueue() != 0){
                        barrackQueue.updateQueue(troop.getCurrentPosition());
                    }
                    troop.setCurrentPosition(-1);
                }else{
                    barrackQueue.startTime = System.currentTimeMillis();    
                }
            }           
            
            barrackQueue.troopListMap.put(packet.typeTroop, troop);
            barrackQueueInfo.barrackQueueMap.put(packet.idBarrack, barrackQueue);
            barrackQueueInfo.saveModel(user.getId());
            send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.SUCCESS, packet.idBarrack, packet.typeTroop), user);
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
                
                time += troopInBarrack.getAmount() * timeTrain;
                
                Troop troopObj = troopInfo.troopMap.get(troopType);
                troopObj.population += troopInBarrack.getAmount();
                troopInfo.troopMap.put(troopType, troopObj);
            }
            
            troopInfo.saveModel(user.getId());
            
            
            reduceUserResources(userInfo, 0, 0, timeToG(time));
            userInfo.saveModel(user.getId());
            
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
    
    public void checkFirst(BarrackQueueInfo barrackQueueInfo, User user) {
        boolean isCheck = false;
        BarrackQueue barrackQueue;     
        for (Integer idBarrack : barrackQueueInfo.barrackQueueMap.keySet()) {
            barrackQueue = barrackQueueInfo.barrackQueueMap.get(idBarrack);
            if(barrackQueue.startTime != 0){
                isCheck = true;
            }
        }
        
        if(isCheck == false) {
            System.out.println("============================================ KHONG CO GI DE CHECK ============== ");
            return;
        }
        
        //Cap nhat capacity AMCs
        int totalAMCsCapacity = getTotalCapacityAMCs(user);
        //So linh tinh tu luc off
        int troopCapacityBefore = getCurrentCapacityTroop(user);
        //So capacity linh toi da co the train
        int troopAvai = totalAMCsCapacity - troopCapacityBefore;
        if(troopAvai == 0) return; 
        
        //Thoi diem offline
        long timeOff = barrackQueueInfo.getMinStartTime();
        if(timeOff == 0) return;
        long currentTime = System.currentTimeMillis();
        
        long timeAvai = (2700 * 3 + 600 * 1) * 1000;                  //25x3 + 10x1 = 85 (max queue length barrack level 12)
        long deltaTime = currentTime - timeOff;
        if(deltaTime > timeAvai) deltaTime = timeAvai * 1000;

        int temp = barrackQueueInfo.getAmountTroopCapacityCanBeTrained(deltaTime);
        
        if(temp <= troopAvai){
            System.out.println("============================================ temp 1 return = " + temp);
            //OK, Goi ham thay doi so luong linh voi deltaTime nay
            this.finishCheckBarrack(barrackQueueInfo, user, deltaTime);
            return;
        }
        
        //Voi deltaTime luc nay, temp <= troopAvai
        int i = 1;
        long tempTime = deltaTime;
        while(tempTime > 1000){
            if(temp < troopAvai){
                tempTime /= 2;
                deltaTime += tempTime / 2;
            }else if(temp > troopAvai){
                tempTime /= 2;
                deltaTime -= tempTime / 2;
            }else{
                System.out.println("============================================ tem 5 return = " + temp);
                this.finishCheckBarrack(barrackQueueInfo, user, deltaTime);
                return;
            }
            temp = barrackQueueInfo.getAmountTroopCapacityCanBeTrained(deltaTime);
            System.out.println("============================================ tem lan loop thu " + i + ": " + temp);
            i++;
        }
        
        //Luc nay, deltaTime da ok
        System.out.println("============================================ tem final return = " + temp);
        this.finishCheckBarrack(barrackQueueInfo, user, deltaTime);
        return;
    }
    
    private void finishCheckBarrack(BarrackQueueInfo barrackQueueInfo, User user, long _deltaTime) {
        BarrackQueue barrackQueue;
        long deltaTime;
        long minStartTime = barrackQueueInfo.getMinStartTime();
        
        System.out.println("==================== DeltaTime duoc chon (chua tru di chenh lech): " + _deltaTime);
        System.out.println("==================== getMinStartTime() cua Ban To Chuc: " + barrackQueueInfo.getMinStartTime());


        for (Integer idBarrack : barrackQueueInfo.barrackQueueMap.keySet()) {
            barrackQueue = barrackQueueInfo.barrackQueueMap.get(idBarrack);
            if(barrackQueue.startTime == 0){
                continue;
            }
            
            deltaTime = _deltaTime - (barrackQueue.startTime - minStartTime);
            System.out.println("==================== StartTime cua barrack id: " + idBarrack + " la: " + barrackQueue.startTime);
            System.out.println("==================== DeltaTime cua barrack id: " + idBarrack + " la: " + deltaTime);
            System.out.println("==================== getMinStartTime(): " + barrackQueueInfo.getMinStartTime());

            //troopList
            if(barrackQueue.getAmountItemInQueue() > 0 && barrackQueue.getTotalTroopCapacity() > 0){
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
                    
                    amountTrainedTroop = deltaTime / (troopInBarrack.getTrainingTime() * 1000);
                    System.out.println("============================================ Amount Trained Troops: " + amountTrainedTroop);
                    if(amountTrainedTroop >= troopInBarrack.getAmount()){
                        int temp = troopInBarrack.getAmount();
                        System.out.println("============================================ amountTrainedTroop >= troopInBarrack");
                        barrackQueue.updateQueue(troopInBarrack.getCurrentPosition());
                        
                        troopInBarrack.setCurrentPosition(-1);
                        troopInBarrack.setAmount(0);
                        
                        //Tang so luong linh cua troop do
                        increaseAmountTroop(user, troopInBarrack.getName(), temp);
                         
                        barrackQueue.setAmountItemInQueue(barrackQueue.getAmountItemInQueue() - 1);
                        barrackQueue.setTotalTroopCapacity(barrackQueue.getTotalTroopCapacity() - temp * troopInBarrack.getHousingSpace());
                              
                        if(barrackQueue.getAmountItemInQueue() == 0){
                            System.out.println("=========================== Amount item in queue = 0");
                            barrackQueue.startTime = 0;
                            break;
                        }
                        
                        deltaTime -=  temp * troopInBarrack.getTrainingTime() * 1000;
                        if(deltaTime == 0) {
                            System.out.println("=========================== deltaTime = 0");
                            break;
                        }
                                     
                    }else if(amountTrainedTroop >= 1){
                        System.out.println("============================================ 1 <= amountTrainedTroop < troopInBarrack");
                        increaseAmountTroop(user, troopInBarrack.getName(), (int) amountTrainedTroop);
                        troopInBarrack.setAmount(troopInBarrack.getAmount() - (int) amountTrainedTroop);
                        barrackQueue.setTotalTroopCapacity(barrackQueue.getTotalTroopCapacity() - (int) amountTrainedTroop * troopInBarrack.getHousingSpace());
                        barrackQueue.startTime = System.currentTimeMillis() - (deltaTime - amountTrainedTroop * troopInBarrack.getTrainingTime() * 1000);
                        break;
                    }else{
                        System.out.println("============================== Chua train duoc con nao");
                        break;
                    }
                    i++;
                }
            } 
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
            System.out.println("=============== KHONG GET DC MAP INFO =================");
            return 0;
        }
        
        JSONObject amcConfig;
        try {
            amcConfig = ServerConstant.configArmyCamp.getJSONObject("AMC_1");
        } catch (JSONException e) {
            System.out.println("=============== KHONG GET DC ARMY CONFIG =================");
            return 0;
        }


        List<Building> listBuilding = mapInfo.listBuilding;

        Iterator<Building> i = listBuilding.iterator();
        while (i.hasNext()) {
            Building build = i.next();

            if((build.status.equals("complete") || build.status.equals("upgrade")) && (build.type.equals("AMC_1"))){
                int capacity;

                try {
                    capacity = amcConfig.getJSONObject(Integer.toString(build.level)).getInt("capacity");
                    System.out.println("=============== capacity:  " + capacity);
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
