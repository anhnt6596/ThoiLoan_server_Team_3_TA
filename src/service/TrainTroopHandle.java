package service;

import bitzero.server.core.BZEventType;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseClientRequestHandler;

import bitzero.server.extensions.data.DataCmd;

import cmd.CmdDefine;

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
            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
            if(barrackQueueInfo == null){
                System.out.println("======================= BarrackQueueInfo null ======================");
                barrackQueueInfo = new BarrackQueueInfo();
                barrackQueueInfo.saveModel(user.getId());
            }
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
               send(new ResponseRequestTrainTroop(ServerConstant.ERROR), user);
               return;
            }
            
            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
            if(barrackQueueInfo == null){
                send(new ResponseRequestTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            BarrackQueue barrackQueue = barrackQueueInfo.getBarrackQueueById(packet.idBarrack);
            int index = barrackQueueInfo.barrackQueueList.indexOf(barrackQueue);

            if(barrackQueue == null){
                send(new ResponseRequestTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            //Can check xem loai linh can train da dc mo khoa hay chua
            
            TroopInBarrack troop = new TroopInBarrack(packet.typeTroop);
            //check queue length
            if(barrackQueue.getTotalTroopCapacity() + troop.getHousingSpace() > barrackQueue.getQueueLength()){
                System.out.println("======================= Vuot qua queue length ======================");
                send(new ResponseRequestTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            TroopInBarrack troopInBarrack = barrackQueue.getTroopInBarrackByName(packet.typeTroop);
            //Neu chua co loai troop nay
            if(troopInBarrack == null) {
                if(barrackQueue.getAmountItemInQueue() == 0){
                    barrackQueue.startTime = System.currentTimeMillis();
                }
                troop.amount++;
                barrackQueue.trainTroopList.add(troop);
            }else{
                troopInBarrack.amount++;
            }
     
            int levelTroop = userInfo.getTroopLevel(packet.typeTroop);
            int trainingElixir = troop.getTrainingElixir(levelTroop);
            int trainingDarkElixir = troop.getTrainingDarkElixir(levelTroop);
            int g = ServerConstant.checkResource(userInfo, 0, trainingElixir, trainingDarkElixir);

            //check tai nguyen co du khong
            if(g > userInfo.getCoin()) {
                System.out.println("======================= Khong du tai nguyen ======================");
                send(new ResponseRequestTrainTroop(ServerConstant.ERROR), user);    
            }

            //Neu du thi giam tai nguyen
            userInfo.reduceUserResources(0, trainingElixir, trainingDarkElixir, g, "", false);
            userInfo.saveModel(user.getId());
              
            barrackQueueInfo.barrackQueueList.set(index, barrackQueue);
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
               send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
               return;
            }
            
            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
            if(barrackQueueInfo == null){
                System.out.println("======================= BarrackQueueInfo null ======================");
                send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            BarrackQueue barrackQueue = barrackQueueInfo.getBarrackQueueById(packet.idBarrack);
            int index = barrackQueueInfo.barrackQueueList.indexOf(barrackQueue);

            if(barrackQueue == null){
                send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            TroopInBarrack troop = barrackQueue.getTroopInBarrackByName(packet.typeTroop);
            if(troop == null){
                send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            int indexTroop = barrackQueue.trainTroopList.indexOf(troop);
            troop.amount -= 1;
            
            if(troop.getAmount() == 0){
                barrackQueue.updateQueue(indexTroop);
            }
            
            MapInfo mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {
               send(new ResponseRequestCancelTrainTroop(ServerConstant.ERROR), user);
               return;
            }
            
            int levelTroop = userInfo.getTroopLevel(packet.typeTroop);
            int trainingElixir = troop.getTrainingElixir(levelTroop);
            int trainingDarkElixir = troop.getTrainingDarkElixir(levelTroop);

            //refund tai nguyen
            int gold_rq = mapInfo.getRequire(ServerConstant.gold_capacity, ServerConstant.gold_sto);    
            int elx_rq = mapInfo.getRequire(ServerConstant.elixir_capacity, ServerConstant.elixir_sto);
            int dElx_rq = mapInfo.getRequire(ServerConstant.darkElixir_capacity, ServerConstant.darkElixir_sto);
            
            userInfo.addResource(0,trainingElixir,trainingDarkElixir,0,gold_rq,elx_rq,dElx_rq);
            userInfo.saveModel(user.getId());

            barrackQueueInfo.barrackQueueList.set(index, barrackQueue);
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
                send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.ERROR, packet.idBarrack, packet.typeTroop), user);
                return;
            }
            
            BarrackQueue barrackQueue = barrackQueueInfo.getBarrackQueueById(packet.idBarrack);
            int index = barrackQueueInfo.barrackQueueList.indexOf(barrackQueue);

            if(barrackQueue == null){
                send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.ERROR, packet.idBarrack, packet.typeTroop), user);
                return;
            }

            TroopInBarrack troop = barrackQueue.getTroopInBarrackByName(packet.typeTroop);
            if(troop == null){
                send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.ERROR, packet.idBarrack, packet.typeTroop), user);
                return;
            }

            //check capacity
            if(userInfo.getCurrentCapacityTroop() + troop.getHousingSpace() > userInfo.getTotalCapacityAMCs()){
                System.out.println("======================= Vuot qua capacity ======================");
                send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.ERROR, packet.idBarrack, packet.typeTroop), user);
                return;
            }

            int indexTroop = barrackQueue.trainTroopList.indexOf(troop);

            int remainTroop = packet.remainTroop;

            long timeTrain = ServerConstant.configTroopBase.getJSONObject(packet.typeTroop).getLong("trainingTime") * 1000;

            long currentTime = System.currentTimeMillis();
            long pastTime = currentTime - barrackQueue.startTime;

            
            if ((pastTime >= timeTrain) && (remainTroop == troop.getAmount() - 1)){

                troop.amount -= 1;
                //Tang so luong loai troop nay
                TroopInfo troopInfo = (TroopInfo) TroopInfo.getModel(user.getId(), TroopInfo.class);
                if (troopInfo == null) {
                    send(new ResponseRequestFinishTimeTrainTroop(ServerConstant.ERROR, packet.idBarrack, packet.typeTroop), user);
                    return;
                }

                Troop troopObj = troopInfo.troopMap.get(packet.typeTroop);

                troopObj.population++;
                troopInfo.troopMap.put(packet.typeTroop, troopObj);
                troopInfo.saveModel(user.getId());

                //Het icon trong item
                if(troop.getAmount() == 0){
                    barrackQueue.updateQueue(indexTroop);

                }else{
                    barrackQueue.startTime = System.currentTimeMillis();    
                }
            }           

            barrackQueueInfo.barrackQueueList.set(index, barrackQueue);
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
               send(new ResponseRequestQuickFinishTrainTroop(ServerConstant.ERROR), user);
               return;
            }
            
            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
            if(barrackQueueInfo == null){
                send(new ResponseRequestQuickFinishTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            BarrackQueue barrackQueue = barrackQueueInfo.getBarrackQueueById(packet.idBarrack);
            int index = barrackQueueInfo.barrackQueueList.indexOf(barrackQueue);

            if(barrackQueue == null){
                send(new ResponseRequestQuickFinishTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            //Neu quick finish xong vuot qua capacity cua AMC thi khong cho
            
            //
            
            TroopInfo troopInfo = (TroopInfo) TroopInfo.getModel(user.getId(), TroopInfo.class);
            if (troopInfo == null) {
                send(new ResponseRequestQuickFinishTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            long timeTrain;
            int time = 0;
            TroopInBarrack troop;
            //Check xem het bn G de quick finish
            for(int i = 0; i < barrackQueue.trainTroopList.size(); i++) {
                troop = barrackQueue.trainTroopList.get(i);
                timeTrain = ServerConstant.configTroopBase.getJSONObject(troop.getName()).getLong("trainingTime");
                time += troop.getAmount() * timeTrain;
            }
            
            //check tai nguyen co du khong
            if(timeToG(time) > userInfo.getCoin()) {
                send(new ResponseRequestQuickFinishTrainTroop(ServerConstant.ERROR), user);
                return;
            }
            
            for(int i = 0; i < barrackQueue.trainTroopList.size(); i++) {
                troop = barrackQueue.trainTroopList.get(i);
                Troop troopObj = troopInfo.troopMap.get(troop.getName());
                troopObj.population += troop.getAmount();
                troopInfo.troopMap.put(troop.getName(), troopObj);
            }
            troopInfo.saveModel(user.getId());
            userInfo.reduceUserResources(0, 0, 0, timeToG(time), "", false);
            userInfo.saveModel(user.getId());
            
            barrackQueue.doReset();            
            barrackQueueInfo.barrackQueueList.set(index, barrackQueue);
            barrackQueueInfo.saveModel(user.getId());
            send(new ResponseRequestQuickFinishTrainTroop(ServerConstant.SUCCESS), user);    
          
        } catch (Exception e) {
            System.out.println(e);
        }
    }
     
    private int timeToG(int time) {
        return (int) Math.ceil(time/60.0);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
}
