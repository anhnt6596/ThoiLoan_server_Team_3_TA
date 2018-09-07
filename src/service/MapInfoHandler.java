package service;

import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.data.DataCmd;

import cmd.CmdDefine;

import cmd.obj.map.MapArray;

import cmd.obj.map.Obs;

import cmd.receive.map.RequestMoveMultiWall;
import cmd.receive.harvest.RequestDoHarvest;
import cmd.receive.harvest.RequestGetHarvestInfo;
import cmd.receive.map.RequestUpgradeConstruction;
import cmd.receive.map.RequestAddConstruction;
import cmd.receive.map.RequestCancleConstruction;
import cmd.receive.map.RequestFinishTimeConstruction;
import cmd.receive.map.RequestGetServerTime;
import cmd.receive.map.RequestMapInfo;

//import cmd.send.demo.ResponseMove;

import cmd.receive.map.RequestMoveConstruction;

import cmd.receive.map.RequestMoveMultiWall;
import cmd.receive.map.RequestQuickFinish;

import cmd.receive.map.RequestRemoveObs;

import cmd.receive.map.RequestUpgradeMultiWall;

import cmd.send.demo.ResponseMoveMultiWall;
import cmd.send.demo.ResponseRequestAddConstruction;
import cmd.send.demo.ResponseRequestCancleConstruction;
import cmd.send.demo.ResponseRequestFinishTimeConstruction;
import cmd.send.demo.ResponseRequestMapInfo;
import cmd.send.demo.ResponseRequestMoveConstruction;
import cmd.send.demo.ResponseRequestQuickFinish;
import cmd.send.demo.ResponseRequestRemoveObs;
import cmd.send.demo.ResponseRequestServerTime;
import cmd.send.demo.ResponseRequestUpgradeConstruction;
import cmd.send.demo.ResponseRequestUserInfo;

import cmd.send.demo.ResponseUpgradeMultiWall;
import cmd.send.harvest.ResponseDoHarvest;
import cmd.send.train.ResponseRequestBarrackQueueInfo;

import java.awt.Point;

import java.util.List;

import java.util.Map;

import model.Building;
import model.GuildBuilding;
import model.MapInfo;
import model.Wall;
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

public class 
MapInfoHandler extends BaseClientRequestHandler {
    
    public static short MAPINFO_MULTI_IDS = 2000;
    public static short add_house_id = 1;
    public static short upgrade_house_id = 2;
    private final Logger logger = LoggerFactory.getLogger("MapInfoHandler");
    private final Logger logger_move = LoggerFactory.getLogger("Move_construction");
    
    public MapInfoHandler() {
        super();
    }
    
    public void init() {
        getParentExtension().addEventListener(BZEventType.PRIVATE_MESSAGE, this);        
    }

    @Override
    public void handleClientRequest(User user, DataCmd dataCmd) {
        try {
            switch (dataCmd.getId()) {
                case CmdDefine.GET_MAP_INFO:                
                    RequestMapInfo map = new RequestMapInfo(dataCmd);
                    processMapInfo(user);
                    break;
                case CmdDefine.MOVE_CONSTRUCTION:
                    System.out.println("Receive MOVE REQUEST");
                    RequestMoveConstruction move_construction = new RequestMoveConstruction(dataCmd);
                    processMoveConstruction(user,move_construction);
                    break;                
                case CmdDefine.ADD_CONSTRUCTION:
                    System.out.println("Receive MAP REQUEST");
                    RequestAddConstruction add_construction = new RequestAddConstruction(dataCmd);
                    processAddConstruction(user,add_construction);
                    break;
                case CmdDefine.UPGRADE_CONSTRUCTION:
                    System.out.println("Receive MAP UPGRADE");
                    RequestUpgradeConstruction upgrade_construction = new RequestUpgradeConstruction(dataCmd);
                    processUpgradeConstruction(user,upgrade_construction);
                    break;
                case CmdDefine.FINISH_TIME_CONSTRUCTION:
                    System.out.println("FINISH_TIME_CONSTRUCTION");
                    RequestFinishTimeConstruction finish_time = new RequestFinishTimeConstruction(dataCmd);
                    processFinishTimeConstruction(user,finish_time);
                    break;
                case CmdDefine.CANCLE_CONSTRUCTION:
                    logger.info("CANCLE_CONSTRUCTION");
                    RequestCancleConstruction cancle_construction = new RequestCancleConstruction(dataCmd);
                    processRequestCancleConstruction(user,cancle_construction);
                    break;
                case CmdDefine.REMOVE_OBS:
                    logger.info("REMOVE_OBS");
                    RequestRemoveObs remove_obs = new RequestRemoveObs(dataCmd);
                    processRequestRemoveObs(user,remove_obs);
                    break;
                
                case CmdDefine.QUICK_FINISH:
                    logger.info("QUICK_FINISH ");
                    RequestQuickFinish quick_finish = new RequestQuickFinish(dataCmd);
                    processQuickFinish(user,quick_finish);
                    break;
                case CmdDefine.GET_SERVER_TIME:
                    //System.out.println("GET_SERVER_TIME");
                    RequestGetServerTime server_time = new RequestGetServerTime(dataCmd);
                    processGetServerTime(user,server_time);
                    break;                
                case CmdDefine.DO_HARVEST:
                        //System.out.println("GET_SERVER_TIME");
                        RequestDoHarvest do_harvest = new RequestDoHarvest(dataCmd);
                        processDoHarvest(user, do_harvest);
                        break;
                case CmdDefine.MOVE_MULTI_WALL:                    
                    RequestMoveMultiWall move_multiWall = new RequestMoveMultiWall(dataCmd);
                    processMoveMultiWall(user,move_multiWall);
                    break;
                case CmdDefine.UPGRADE_MULTI_WALL :                    
                    RequestUpgradeMultiWall upgrade_multiWall = new RequestUpgradeMultiWall(dataCmd);
                    processUpgradeMultiWall(user,upgrade_multiWall);
                    break;
            }
        } catch (Exception e) {
            logger.warn("DEMO HANDLER EXCEPTION " + e.getMessage());
            logger.warn(ExceptionUtils.getStackTrace(e));
        }

    }
    
    public void handleServerEvent(IBZEvent ibzevent) {        
        if (ibzevent.getType() == BZEventType.PRIVATE_MESSAGE) {
            this.processEventPrivateMsg((User)ibzevent.getParameter(BZEventParam.USER));
        }
    }
    
    private void processMapInfo(User user){
        try {
            System.out.println("getID map:" + user.getId() );
            MapInfo mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
                if (mapInfo == null) {
                    System.out.println("mapInfo_null @#%&*^$&*&$@^$#$&^@#$&@$%@#%^@#^");
                    mapInfo = new MapInfo();
                    
                    
                    
                    mapInfo.saveModel(user.getId());
                }
//            System.out.println(">>>>>MAP ARRAY:");
            logger.info("in ra TRUOC KHI CHECK MAP map");
            mapInfo.print();
            mapInfo.checkStatus(user);
            logger.info("in ra SAU KHI CHECK MAP");
            mapInfo.print();
            mapInfo.saveModel(user.getId());
            send(new ResponseRequestMapInfo(mapInfo), user);
            
        } catch (Exception e) {
        }
    }
    
    private void processEventPrivateMsg(User user){
        /**
         * process event
         */
        logger.info("processEventPrivateMsg, userId = " + user.getId());
    }

    private void processMoveConstruction(User user, RequestMoveConstruction move_construction) {
        
        try {
            System.out.println("processMoveConstruction" + user.getId() );
            MapInfo mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {
                //send response error
            }
            logger_move.debug("Map Info truoc khi move");
            //mapInfo.print();
            Building building = mapInfo.listBuilding.get(move_construction.id);
            if (building.type.equals("CLC_1") && building.level==0){
                    logger.debug("Nha Bang hoi chua duoc xay dung, khong the di chuyen!");
                    send(new ResponseRequestMoveConstruction(ServerConstant.ERROR), user);
                    return;
                }
            MapArray mapArray = new MapArray();
            mapArray = mapInfo.getMapArray();
            //System.out.println("VI TRI CU="+mapInfo.listBuilding.get(move_construction.id).posX+" "+mapInfo.listBuilding.get(move_construction.id).posY);
            boolean check = mapArray.moveBuilding(mapInfo, move_construction.id, move_construction.posX,move_construction.posY);
            //mapInfo.saveModel(user.getId());
            if (check){
                //System.out.println("new positionnnnn = "+ mapInfo.listBuilding.toString() );
                System.out.println("VI TRI MOI="+mapInfo.listBuilding.get(move_construction.id).posX+" "+mapInfo.listBuilding.get(move_construction.id).posY);
                mapInfo.saveModel(user.getId());
                send(new ResponseRequestMoveConstruction(ServerConstant.SUCCESS), user);
            }
            else{
                System.out.println("new positionnnnn = FALSE"  );
                //mapInfo.saveModel(user.getId());
                send(new ResponseRequestMoveConstruction(ServerConstant.ERROR), user);
            }
                
                   
               } catch (Exception e) {
            }
    }

    private void processAddConstruction(User user, RequestAddConstruction add_construction) {
        try {           
            
            System.out.println("processAddConstruction" + user.getId() );
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
           if (userInfo == null) {
               ////send response error
               send(new ResponseRequestAddConstruction(ServerConstant.ERROR), user);
            }
            MapInfo mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
           if (mapInfo == null) {
               
               //send response error
               send(new ResponseRequestAddConstruction(ServerConstant.ERROR), user);
            }
            
            MapArray mapArray = new MapArray();
            mapArray = mapInfo.getMapArray();
            
            boolean checkPosition = mapArray.check_addBuilding(mapInfo, add_construction.type, add_construction.posX,add_construction.posY);
            System.out.println("checkPosition = " + checkPosition );
            
            //CHECK_RESOURCE *********************************************
            int level =1;
            if (add_construction.type.equals("BDH_1")){
                       level=userInfo.builderNumber+1;
                   }
            //System.out.println("new level = " + level );
            int check_resource = 0;
            check_resource = ServerConstant.checkResourceBasedOnType(userInfo,(add_construction.type),level);
               
            
            //System.out.println("check_resource coin bu vao tai nguyen khac to add building= " + check_resource );
            
            int coin = ServerConstant.getCoin(add_construction.type,level); //coin de thuc hien thao tac voi nha
            
            System.out.println("check_resource+coin= " + (check_resource+coin) );
            System.out.println("userInfo.coin = " + userInfo.coin );
            
            if (checkPosition && (check_resource+coin<userInfo.coin)){ 
                //add building to pending
                int gold = ServerConstant.getGold(add_construction.type,level);
                int elixir = ServerConstant.getElixir(add_construction.type,level);
                int darkElixir = ServerConstant.getDarkElixir(add_construction.type,level);
                
                
                
                //Xet truong hop dac biet
                if (add_construction.type.equals("BDH_1")){
                    level = userInfo.builderNumber+1;
                    if (level>5){
                        send(new ResponseRequestAddConstruction(ServerConstant.ERROR), user);
                        return;
                    }
                    mapInfo.addBuilding(add_construction.type, add_construction.posX, add_construction.posY,level, "complete");
                    userInfo.reduceUserResources(gold,elixir,darkElixir,check_resource+coin, add_construction.type, true);
                    userInfo.saveModel(user.getId());
                    mapInfo.saveModel(user.getId());
                    
                    send(new ResponseRequestAddConstruction(ServerConstant.SUCCESS), user);
                    return;
                }
                else { //neu khong phai nha BuildingHut
                
                
                
                System.out.println("so tho xay hien tai la: "+ userInfo.builderNumber);
                
                // kiem tra tho xay
                //                if (mapInfo.getBuilderNotFree()>=userInfo.builderNumber){ //neu khong co tho xay
                if (mapInfo.getBuilderNotFree()>=userInfo.builderNumber){ //neu khong co tho xay
                    
                    System.out.println("CAN GIAI PHONG THO XAY");
                    
                    //get resource cua nha
                    
                    int g_release = mapInfo.getGToReleaseBuilder();
                    System.out.println("So G de giai phong la "+ g_release);
//                    check_resource = check_resource +g;
                    if (userInfo.coin < coin+check_resource+g_release ){ //neu khong du tien mua tho xay
                        //linhrafa --Neu false
                        //tra ve false
                        send(new ResponseRequestAddConstruction(ServerConstant.ERROR), user);
                    }
                    else {
                        //giai phong 1 ngoi nha pending
                        
                        mapInfo.releaseBuilding(user); 
                        
                        mapInfo.addBuilding(add_construction.type, add_construction.posX, add_construction.posY,level, "pending");
                        
                        mapInfo.print();
                        
                        mapArray = mapInfo.getMapArray();
                        
                        userInfo.reduceUserResources(gold,elixir,darkElixir,check_resource+coin+g_release, add_construction.type, true);
                        userInfo.saveModel(user.getId());
                        mapInfo.saveModel(user.getId());
//                        logger.info("in ra khi add construction");
//                        mapInfo.print();
                        send(new ResponseRequestAddConstruction(ServerConstant.SUCCESS), user);
                    }
                } 
                else { //neu da du tho xay
                    userInfo.reduceUserResources(gold,elixir,darkElixir,check_resource+coin, add_construction.type, true);
                    mapInfo.addBuilding(add_construction.type, add_construction.posX, add_construction.posY,level, "pending");
                    userInfo.saveModel(user.getId());
                    mapInfo.saveModel(user.getId());
//                    logger.info("in ra khi add construction");
//                    mapInfo.print();
                    send(new ResponseRequestAddConstruction(ServerConstant.SUCCESS), user);
                }
            }
                
                
            }
            else {
                //linhrafa --Neu false
                //tra ve false
                send(new ResponseRequestAddConstruction(ServerConstant.ERROR), user);
            }      
               } catch (Exception e) {
            }
    }
    
    private void processUpgradeConstruction(User user, RequestUpgradeConstruction upgrade_construction) {
        MapInfo mapInfo;
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               ////send response error
               send(new ResponseRequestUpgradeConstruction(ServerConstant.ERROR), user);
               return;
            }
            //*------------------------------------------------
            mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {               
               //send response error
               send(new ResponseRequestUpgradeConstruction(ServerConstant.ERROR), user);
               return;
            }
            Building building = mapInfo.listBuilding.get(upgrade_construction.id);
            if (building.type.equals("BDH_1") || building.status.equals(ServerConstant.destroy_status)){
                    send(new ResponseRequestUpgradeConstruction(ServerConstant.ERROR), user);
                    return;
                }
            //*------------------------------------------------
//            logger.info(">>>>>>>>>>>>>in ra truoc khi upgrade>>>>>>>");
//            mapInfo.print();
        //neu la nha Resource thi thu hoac truoc
            if (building.type.equals("RES_1") || building.type.equals("RES_2") || building.type.equals("RES_3")){
                doHarvest(user,building.id);
            }
            
        int exchange_resource = 0;
        exchange_resource = ServerConstant.checkResourceBasedOnType(userInfo,(building.type),building.level+1);
            
        System.out.println("check_resource chuyen doi to upgrade building= " + exchange_resource );
        int coin = ServerConstant.getCoin(building.type,building.level+1);

        if ((exchange_resource+coin<userInfo.coin)){ 
                //add building to pending
                int gold = ServerConstant.getGold(building.type,building.level+1);
                int elixir = ServerConstant.getElixir(building.type,building.level+1);
                int darkElixir = ServerConstant.getDarkElixir(building.type,building.level+1);
                
                mapInfo.print();
                
                System.out.println("so tho xay hien tai la: "+ userInfo.builderNumber);
                
                // kiem tra tho xay
                //                if (mapInfo.getBuilderNotFree()>=userInfo.builderNumber){ //neu khong co tho xay
                if (mapInfo.getBuilderNotFree()>=userInfo.builderNumber){ //neu khong co tho xay
                    
                    System.out.println("CAN GIAI PHONG THO XAY");
                    
                    //get resource cua nha
                    
                    int g_release = mapInfo.getGToReleaseBuilder();
                    System.out.println("So G de giai phong la "+ g_release);
//                    check_resource = check_resource +g;
                    if (userInfo.coin < coin+exchange_resource+g_release ){ //neu khong du tien mua tho xay
                        //linhrafa --Neu false
                        //tra ve false
                        send(new ResponseRequestUpgradeConstruction(ServerConstant.ERROR), user);
                        return;
                    }
                    else {
                        //giai phong 1 ngoi nha pending
                        
                        mapInfo.releaseBuilding(user); 
                        
                        mapInfo.print();
                        
                        userInfo.reduceUserResources(gold,elixir,darkElixir,exchange_resource+coin+g_release, building.type, false);
                        mapInfo.upgradeBuilding(upgrade_construction.id);
                        
                        if (building.type.equals("CLC_1") && building.level ==0){
                                GuildBuilding guildBuilding = new GuildBuilding();
                                guildBuilding.saveModel(user.getId());
                        }
                        
                        userInfo.saveModel(user.getId());
                        mapInfo.saveModel(user.getId());
                        logger.info(">>>>>>>>>>>>>in ra sau khi upgrade>>>>>>>");
                        //mapInfo.print();
                        send(new ResponseRequestUpgradeConstruction(ServerConstant.SUCCESS), user);
                    }
                } 
                else { //neu da du tho xay
                    userInfo.reduceUserResources(gold,elixir,darkElixir,exchange_resource+coin, building.type, false);                    
                    mapInfo.upgradeBuilding(upgrade_construction.id);
                    
                    if (building.type.equals("CLC_1") && building.level ==0){
                            GuildBuilding guildBuilding = new GuildBuilding();
                            guildBuilding.saveModel(user.getId());
                    }
                        
                    userInfo.saveModel(user.getId());
                    mapInfo.saveModel(user.getId());
                    logger.info(">>>>>>>>>>>>>in ra sau khi upgrade>>>>>>>");
                    mapInfo.print();
                    send(new ResponseRequestUpgradeConstruction(ServerConstant.SUCCESS), user);
                }
            }
        else {
            //linhrafa --Neu false
            //tra ve false
            send(new ResponseRequestUpgradeConstruction(ServerConstant.ERROR), user);
            return;
        }
            
        //Dat lai startTime cho Barrack
        if(building.type.equals(ServerConstant.BARRACK_TYPE)){
            System.out.println("==============================START UPGRADE BAR_1===========================");
            try {
                BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
                barrackQueueInfo.updateWhenBarrackStartUpgrade(building.id);
                barrackQueueInfo.saveModel(user.getId());
            } catch (Exception e) {
            }
        } 
            
        } catch (Exception e) {
        }
    
        
    }
    private void processUpgradeConstruction(User user, int id) {
        MapInfo mapInfo;
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               ////send response error
               send(new ResponseRequestUpgradeConstruction(ServerConstant.ERROR), user);
               return;
            }
            //*------------------------------------------------
            mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {               
               //send response error
               send(new ResponseRequestUpgradeConstruction(ServerConstant.ERROR), user);
               return;
            }
            Building building = mapInfo.listBuilding.get(id);
            if (building.type.equals("BDH_1") || building.status.equals(ServerConstant.destroy_status)){
                    send(new ResponseRequestUpgradeConstruction(ServerConstant.ERROR), user);
                    return;
                }
            //*------------------------------------------------
    //            logger.info(">>>>>>>>>>>>>in ra truoc khi upgrade>>>>>>>");
    //            mapInfo.print();
        //neu la nha Resource thi thu hoac truoc
            if (building.type.equals("RES_1") || building.type.equals("RES_2") || building.type.equals("RES_3")){
                doHarvest(user,building.id);
            }
            
        int exchange_resource = 0;
        exchange_resource = ServerConstant.checkResourceBasedOnType(userInfo,(building.type),building.level+1);
            
        System.out.println("check_resource chuyen doi to upgrade building= " + exchange_resource );
        int coin = ServerConstant.getCoin(building.type,building.level+1);

        if ((exchange_resource+coin<userInfo.coin)){ 
                //add building to pending
                int gold = ServerConstant.getGold(building.type,building.level+1);
                int elixir = ServerConstant.getElixir(building.type,building.level+1);
                int darkElixir = ServerConstant.getDarkElixir(building.type,building.level+1);
                
                mapInfo.print();
                
                System.out.println("so tho xay hien tai la: "+ userInfo.builderNumber);
                
                // kiem tra tho xay
                //                if (mapInfo.getBuilderNotFree()>=userInfo.builderNumber){ //neu khong co tho xay
                if (mapInfo.getBuilderNotFree()>=userInfo.builderNumber){ //neu khong co tho xay
                    
                    System.out.println("CAN GIAI PHONG THO XAY");
                    
                    //get resource cua nha
                    
                    int g_release = mapInfo.getGToReleaseBuilder();
                    System.out.println("So G de giai phong la "+ g_release);
    //                    check_resource = check_resource +g;
                    if (userInfo.coin < coin+exchange_resource+g_release ){ //neu khong du tien mua tho xay
                        //linhrafa --Neu false
                        //tra ve false
                        send(new ResponseRequestUpgradeConstruction(ServerConstant.ERROR), user);
                        return;
                    }
                    else {
                        //giai phong 1 ngoi nha pending
                        
                        mapInfo.releaseBuilding(user); 
                        
                        mapInfo.print();
                        
                        userInfo.reduceUserResources(gold,elixir,darkElixir,exchange_resource+coin+g_release, building.type, false);
                        mapInfo.upgradeBuilding(id);
                        
                        userInfo.saveModel(user.getId());
                        mapInfo.saveModel(user.getId());
                        logger.info(">>>>>>>>>>>>>in ra sau khi upgrade>>>>>>>");
                        //mapInfo.print();
                        send(new ResponseRequestUpgradeConstruction(ServerConstant.SUCCESS), user);
                    }
                } 
                else { //neu da du tho xay
                    userInfo.reduceUserResources(gold,elixir,darkElixir,exchange_resource+coin, building.type, false);                    
                    mapInfo.upgradeBuilding(id);
                    
                    userInfo.saveModel(user.getId());
                    mapInfo.saveModel(user.getId());
                    logger.info(">>>>>>>>>>>>>in ra sau khi upgrade>>>>>>>");
                    mapInfo.print();
                    send(new ResponseRequestUpgradeConstruction(ServerConstant.SUCCESS), user);
                }
            }
        else {
            //linhrafa --Neu false
            //tra ve false
            send(new ResponseRequestUpgradeConstruction(ServerConstant.ERROR), user);
            return;
        }
            
        //Dat lai startTime cho Barrack
        if(building.type.equals(ServerConstant.BARRACK_TYPE)){
            System.out.println("==============================START UPGRADE BAR_1===========================");
            try {
                BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
                barrackQueueInfo.updateWhenBarrackStartUpgrade(building.id);
                barrackQueueInfo.saveModel(user.getId());
            } catch (Exception e) {
            }
        }
              
        } catch (Exception e) {
        }   
    }
    private void processFinishTimeConstruction(User user, RequestFinishTimeConstruction finish_time) {
        System.out.println(">>>>>>processFinishTimeConstruction");
        MapInfo mapInfo;
        try {
            mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {
                //send response error
                send(new ResponseRequestFinishTimeConstruction(ServerConstant.ERROR), user);
                return;
            }  
            Building building = mapInfo.listBuilding.get(finish_time.id);
            if (building.status.equals(ServerConstant.destroy_status)){
                System.out.println("Nha nay da huy");
                send(new ResponseRequestFinishTimeConstruction(ServerConstant.ERROR), user);
                return;
            }
            if (building.type.equals("BDH_1")){
                System.out.println("Nha nay la nha tho xay ma` ma'");
                send(new ResponseRequestFinishTimeConstruction(ServerConstant.ERROR), user);
                return;
            }
            long time_cur = System.currentTimeMillis();
            long time_da_chay = time_cur-building.timeStart;
            long time_can_chay = building.getTimeBuild(building.status);
            if ((time_da_chay > time_can_chay) ){
                if (building.status.equals("upgrade")){
                    if(mapInfo.listBuilding.get(finish_time.id).type.equals(ServerConstant.BARRACK_TYPE)){
                        System.out.println("==============================FINISH TIME UPGRADE BAR_1===========================");
                        try {
                            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
                            barrackQueueInfo.updateWhenBarrackUpgraded(building.id);
                            barrackQueueInfo.saveModel(user.getId());
                        } catch (Exception e) {
                        }
                    }
                    
                    mapInfo.listBuilding.get(finish_time.id).level ++;
                }else{
                    if(mapInfo.listBuilding.get(finish_time.id).type.equals(ServerConstant.BARRACK_TYPE)){
                        System.out.println("==============================FINISH TIME BUILD BAR_1===========================");
                        try {
                            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
                            barrackQueueInfo.updateWhenBarrackBuilt(building.id);
                            barrackQueueInfo.saveModel(user.getId());
                        } catch (Exception e) {
                        }
                    }
                }
                
                mapInfo.listBuilding.get(finish_time.id).setStatus("complete");
                mapInfo.listBuilding.get(finish_time.id).setStartTime();
            }
            
            mapInfo.saveModel(user.getId());
            
            send(new ResponseRequestFinishTimeConstruction(ServerConstant.SUCCESS), user);

        } catch (Exception e) {
            
        }
    }

    private void processGetServerTime(User user, RequestGetServerTime finish_time) {
        try {
//            System.out.println("getID:" + user.getId() );   
            long time_cur = System.currentTimeMillis();
            send(new ResponseRequestServerTime(time_cur), user);
        } catch (Exception e) {

        }

    }

    private void processQuickFinish(User user, RequestQuickFinish quick_finish) {
        logger.info("processQuickFinish");
        MapInfo mapInfo;
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               ////send response error
               send(new ResponseRequestQuickFinish(ServerConstant.ERROR), user);
               return;
            }
            //*------------------------------------------------
            mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {               
               //send response error
               send(new ResponseRequestQuickFinish(ServerConstant.ERROR), user);
               return;
            }
            
            logger.info("quick_finish.id: "+ quick_finish.id);
            
            Building building = mapInfo.listBuilding.get(quick_finish.id);
            
            logger.info("quick_finish.type : "+ building.type);
            logger.info("quick_finish.id: "+ quick_finish.id);
            if (building.type.equals("BDH_1") || building.status.equals(ServerConstant.destroy_status)){
                    send(new ResponseRequestQuickFinish(ServerConstant.ERROR), user);
                    return;
                }
            //*------------------------------------------------
            logger.info(">>>>>>>>>>>>>in ra truoc khi quick finish>>>>>>>");
//            
            mapInfo.print();                
            int g_release = building.getGtoQuickFinish();
            System.out.println("So G de hoan thanh nhanh la "+ g_release);
            mapInfo.print();
            
            if (userInfo.coin < g_release ){
                send(new ResponseRequestQuickFinish(ServerConstant.ERROR), user);
                return;
            }
            else {
                //Check if BAR_1
                if (building.status.equals(ServerConstant.upgrade_status)){
                    if(mapInfo.listBuilding.get(quick_finish.id).type.equals(ServerConstant.BARRACK_TYPE)){
                        System.out.println("==============================QUICK FINISH UPGRADE BAR_1===========================");
                        try {
                            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
                            barrackQueueInfo.updateWhenBarrackUpgraded(building.id);
                            barrackQueueInfo.saveModel(user.getId());
                        } catch (Exception e) {
                        }
                    }
                }
                else{
                    if(mapInfo.listBuilding.get(quick_finish.id).type.equals(ServerConstant.BARRACK_TYPE)){
                        System.out.println("==============================QUICK FINISH BUILD BAR_1===========================");
                        try {
                            BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
                            barrackQueueInfo.updateWhenBarrackBuilt(building.id);
                            barrackQueueInfo.saveModel(user.getId());
                        } catch (Exception e) {
                        }
                    }
                }
                
                
                System.out.println("Building = "+ building.type+ ", level: "+ building.level+" , status: "+building.status);
                userInfo.reduceUserResources(0,0,0,g_release, building.type, false);
                if (building.status.equals(ServerConstant.upgrade_status)){
                    mapInfo.listBuilding.get(quick_finish.id).level ++;
                }
                mapInfo.listBuilding.get(quick_finish.id).setStatus(ServerConstant.complete_status);
                mapInfo.listBuilding.get(quick_finish.id).setStartTime();
                mapInfo.print();                
                
                mapInfo.saveModel(user.getId());
                userInfo.saveModel(user.getId());
                send(new ResponseRequestQuickFinish(ServerConstant.SUCCESS), user);     
            }
        } catch (Exception e) {
        }
    }

    private void processRequestCancleConstruction(User user, RequestCancleConstruction cancle_construction) {
        logger.info("processRequestCancleConstruction");
        MapInfo mapInfo;
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               ////send response error
                logger.debug("khong ton tai user");
               send(new ResponseRequestCancleConstruction(ServerConstant.ERROR), user);
               return;
            }
            //*------------------------------------------------
            mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {               
               //send response error
               logger.debug("khong ton tai map");
               send(new ResponseRequestCancleConstruction(ServerConstant.ERROR), user);
               return;
            }
            
            Building building = mapInfo.listBuilding.get(cancle_construction.id);
            logger.debug(building.type+"lalalaal" + building.status+ building.id);
            logger.debug("id duoc truyen len la: "+ cancle_construction.id);
            if (building.type.equals("BDH_1") || building.status.equals(ServerConstant.destroy_status)){
                    logger.debug("nha BDH hoac la nha da huy");
                    send(new ResponseRequestCancleConstruction(ServerConstant.ERROR), user);
                    return;
                }
            int gold = building.getGtoCancle(ServerConstant.gold_resource);
            int elixir = building.getGtoCancle(ServerConstant.elixir_resource);
            int darkElixir = building.getGtoCancle(ServerConstant.darkElixir_resource);
            int coin = building.getGtoCancle(ServerConstant.coin_resource);
            
            int gold_rq = mapInfo.getRequire(ServerConstant.gold_capacity, ServerConstant.gold_sto);    
            int elx_rq = mapInfo.getRequire(ServerConstant.elixir_capacity, ServerConstant.elixir_sto);
            int dElx_rq = mapInfo.getRequire(ServerConstant.darkElixir_capacity, ServerConstant.darkElixir_sto);
            
            userInfo.addResource(gold,elixir,darkElixir,coin,gold_rq,elx_rq,dElx_rq);
            
            if (building.status.equals(ServerConstant.upgrade_status)){
                mapInfo.listBuilding.get(cancle_construction.id).setStatus(ServerConstant.complete_status);
                
                //Check Barrack
                if(building.type.equals(ServerConstant.BARRACK_TYPE)){
                    System.out.println("==============================CANCEL UPGRADE BAR_1===========================");
                    try {
                        BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
                        barrackQueueInfo.updateWhenBarrackCancelUpgrade(building.id);
                        barrackQueueInfo.saveModel(user.getId());
                    } catch (Exception e) {
                    }
                }
            }
            else if ((building.status.equals(ServerConstant.pending_status))){
                mapInfo.listBuilding.get(cancle_construction.id).setStatus(ServerConstant.destroy_status);    
                mapInfo.listBuilding.get(cancle_construction.id).setStartTime();
            }
            
            
            mapInfo.saveModel(user.getId());
            userInfo.saveModel(user.getId());
            send(new ResponseRequestCancleConstruction(ServerConstant.SUCCESS), user);
            
        } catch (Exception e) {
            
        }
    }

    private void processRequestRemoveObs(User user, RequestRemoveObs remove_obs) {
        logger.info("processRequestRemoveObs");
        MapInfo mapInfo;
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               ////send response error
                logger.debug("khong ton tai user");
               send(new ResponseRequestRemoveObs(ServerConstant.ERROR), user);
               return;
            }
            //*------------------------------------------------
            mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {               
               //send response error
               logger.debug("khong ton tai map");
               send(new ResponseRequestRemoveObs(ServerConstant.ERROR), user);
               return;
            }
            
            Obs obs = mapInfo.listObs.get(remove_obs.id);
            logger.debug(obs.type+" " + obs.status+ obs.id);
            logger.debug("id obs duoc truyen len la: "+ remove_obs.id);
            if (obs.status.equals(ServerConstant.destroy_status)){    
                    logger.info("Obstacle da duoc loai bo tu truoc");
                    send(new ResponseRequestRemoveObs(ServerConstant.ERROR), user);
                    return;
                }
            
            int exchange_resource = 0;
            exchange_resource = ServerConstant.checkResourceBasedOnType(userInfo,(obs.type),1);
            int coin = ServerConstant.getCoin(obs.type,obs.level);
            if ((exchange_resource+coin>userInfo.coin)){ 
                logger.info("User khong du tai nguyen de don cay");
                send(new ResponseRequestRemoveObs(ServerConstant.ERROR), user);
                return;
            }
            
            
            
            
//            int coin = obs.getGtoRemove(ServerConstant.coin_resource);
            
            if (exchange_resource<userInfo.coin){ 
                //add obs to pending
                int gold = obs.getGtoRemove(ServerConstant.gold_resource);
                int elixir = obs.getGtoRemove(ServerConstant.elixir_resource);
                int darkElixir = obs.getGtoRemove(ServerConstant.darkElixir_resource);
                //Xet truong hop dac biet
                
                System.out.println("so tho xay hien tai la: "+ userInfo.builderNumber);
                
                // kiem tra tho xay
                //                if (mapInfo.getBuilderNotFree()>=userInfo.builderNumber){ //neu khong co tho xay
                if (mapInfo.getBuilderNotFree()>=userInfo.builderNumber){ //neu khong co tho xay    
                    System.out.println("CAN GIAI PHONG THO XAY");                    
                    //get resource cua nha                    
                    int g_release = mapInfo.getGToReleaseBuilder();
                    System.out.println("So G de giai phong la "+ g_release);
        //                    check_resource = check_resource +g;
                    if (userInfo.coin < exchange_resource+g_release ){ //neu khong du tien mua tho xay
                        //linhrafa --Neu false
                        //tra ve false
                        logger.warn("Khong du tien de giai phong tho xay va chat cay");
                        send(new ResponseRequestRemoveObs(ServerConstant.ERROR), user);
                    }
                    else {
                        //giai phong 1 ngoi nha pending
                        
                        mapInfo.releaseBuilding(user); 
                        mapInfo.print();
                        
                        //mapArray = mapInfo.getMapArray();
                        
                        userInfo.reduceUserResources(gold,elixir,darkElixir,exchange_resource+g_release, obs.type, true);
                    }
                } 
                else { //neu da du tho xay
                    userInfo.reduceUserResources(gold,elixir,darkElixir,exchange_resource, obs.type, true);
                }
                
                //thuong resource sau khi bo cay                
                int  elixir_reward = obs.getElixirReward();
                int  darkElixir_reward = obs.getDarkElixirReward();
                
                
                int gold_rq = mapInfo.getRequire(ServerConstant.gold_capacity, ServerConstant.gold_sto);    
                int elx_rq = mapInfo.getRequire(ServerConstant.elixir_capacity, ServerConstant.elixir_sto);
                int dElx_rq = mapInfo.getRequire(ServerConstant.darkElixir_capacity, ServerConstant.darkElixir_sto);
                
                
                userInfo.addResource(0,elixir_reward,darkElixir_reward,0,gold_rq,elx_rq,dElx_rq);
                
                userInfo.saveModel(user.getId());
                mapInfo.saveModel(user.getId());
                //                        logger.info("in ra khi add construction");
                //                        mapInfo.print();
                send(new ResponseRequestRemoveObs(ServerConstant.SUCCESS), user);
                
            }
            else {
                //linhrafa --Neu false
                //tra ve false
                send(new ResponseRequestRemoveObs(ServerConstant.ERROR), user);
            } 
            
            
                      
            
            mapInfo.saveModel(user.getId());
            userInfo.saveModel(user.getId());
            send(new ResponseRequestCancleConstruction(ServerConstant.SUCCESS), user);
            
        } catch (Exception e) {
            
        }
    }
        

    private void processDoHarvest(User user, RequestDoHarvest do_harvest) {
        logger.debug("Thu hoach mo id "+do_harvest.id);
        
        try {
            MapInfo mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {
                //send response error
                logger.info("Khong ton tai mapInfo");
                send(new ResponseDoHarvest(ServerConstant.ERROR, "", 0), user);
                return;
            }
            
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               ////send response error
                logger.debug("khong ton tai user");
                send(new ResponseDoHarvest(ServerConstant.ERROR, "", 0), user);
               return;
            }
            
            Building building = mapInfo.listBuilding.get(do_harvest.id);
            logger.debug("Mo "+building.type+" duoc thu hoach, trang thai: " + building.status);
            if (!building.type.equals("RES_1") && !building.type.equals("RES_2") && !building.type.equals("RES_3")){
                    logger.debug("Khong phai nha resource");
                    send(new ResponseDoHarvest(ServerConstant.ERROR, "", 0), user);
                    return;
                }
            if (!building.status.equals(ServerConstant.complete_status)){
                logger.debug("Nha resource khong trong trang thai complete de co the xay duoc");
                send(new ResponseDoHarvest(ServerConstant.ERROR, "", 0), user);
                return;
            }
            
            int productivity = doHarvest(user, do_harvest.id);
            
            send(new ResponseDoHarvest(ServerConstant.SUCCESS, building.type, productivity ), user);
            
        } catch (Exception e) {
            logger.info("Khong thu hoach duoc");
            send(new ResponseDoHarvest(ServerConstant.ERROR, "", 0), user);
        }
        
        
    }
         private int timeToProductivity(String type, int level, long time_sanxuat) {
            //Ham chuyen doi thoi gian (Milisecond) toi san luong
            try {
                logger.debug("type = "+type);
                logger.debug("level = "+level);
                
                int unit_product = ServerConstant.configResource.getJSONObject(type).getJSONObject(Integer.toString(level)).getInt("productivity");
                logger.debug("unit_product = "+unit_product);
                time_sanxuat = time_sanxuat/1000;
                logger.debug("time_sanxuat = "+time_sanxuat);
                float ans = (float)(time_sanxuat * (float)(unit_product) /3600);
                int capacity = ServerConstant.configResource.getJSONObject(type).getJSONObject(Integer.toString(level)).getInt("capacity");
                logger.info("capacity = "+capacity);
                logger.info("ans = "+ans);
                if (ans>capacity){
                    logger.info("san luong vuot qua suc chua cua nha");
                    logger.info(type+" level= "+ level);
                    ans = capacity;
                }
                
                return (int) ans;
                
                
            } catch (JSONException e) {
                logger.debug("Khong lay duoc san luong");
                logger.debug(type+" level= "+ level);
                return -1;
            }
        }
         private int doHarvest(User user, int id) {
            ZPUserInfo userInfo;
            try {
                userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
                MapInfo mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
                Building building = mapInfo.listBuilding.get(id);
                
                
                Long time_sanxuat = System.currentTimeMillis() - building.timeStart;
                logger.debug("timeStart = "+building.timeStart);
                logger.debug("time now = "+System.currentTimeMillis());
                logger.debug("time sanxuat = "+ time_sanxuat);
                mapInfo.listBuilding.get(id).setStartTime();
                int productivity = timeToProductivity(building.type,building.level,time_sanxuat);
                logger.debug("productivity = " + productivity);
                int gold = 0, elixir =0, darkElixir=0, coin =0 ;
                switch (building.type){
                    case "RES_1":
                        gold = productivity;
                        break;
                    case "RES_2":
                        elixir = productivity;
                        break;
                    case "RES_3":
                        darkElixir = productivity;
                        break;
                }
                
                int gold_rq = mapInfo.getRequire(ServerConstant.gold_capacity, ServerConstant.gold_sto);    
                int elx_rq = mapInfo.getRequire(ServerConstant.elixir_capacity, ServerConstant.elixir_sto);
                int dElx_rq = mapInfo.getRequire(ServerConstant.darkElixir_capacity, ServerConstant.darkElixir_sto);
                
                userInfo.addResource(gold,elixir,darkElixir,coin,gold_rq,elx_rq,dElx_rq);
                logger.info("Add them gold, elixir, dark, coin= "+gold+" "+elixir+" "+darkElixir+" "+coin);
                userInfo.saveModel(user.getId());
                mapInfo.saveModel(user.getId());
                return productivity;
                
            } catch (Exception e) {
                return 0;
            }
            
        }

    private void processMoveMultiWall(User user, RequestMoveMultiWall move_multiWall) {
        logger.info("*****************processMoveMultiWall***************" );        
        for (int i=0; i< move_multiWall.length_wall; i++){
            Wall wall = move_multiWall.listWall.get(i);
            boolean dd = processMoveWall(user, wall.id, wall.posX, wall.posY);
            if (!dd){
                send(new ResponseMoveMultiWall(ServerConstant.ERROR), user);
            }
        }
        send(new ResponseMoveMultiWall(ServerConstant.SUCCESS), user);
        
    }
    private boolean processMoveWall(User user, int id, int posX, int posY) {
        
        try {
            
            MapInfo mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {
                //send response error
                return false;
            }
            logger_move.debug("Map Info truoc khi move");
            //mapInfo.print();            
            MapArray mapArray = new MapArray();
            mapArray = mapInfo.getMapArray();
            //System.out.println("VI TRI CU="+mapInfo.listBuilding.get(move_construction.id).posX+" "+mapInfo.listBuilding.get(move_construction.id).posY);
            boolean check = mapArray.moveBuilding(mapInfo, id, posX, posY);
            //mapInfo.saveModel(user.getId());
            if (check){
                //System.out.println("new positionnnnn = "+ mapInfo.listBuilding.toString() );
                System.out.println("VI TRI MOI="+mapInfo.listBuilding.get(id).posX+" "+mapInfo.listBuilding.get(id).posY);
                mapInfo.saveModel(user.getId());
                //send(new ResponseRequestMoveConstruction(ServerConstant.SUCCESS), user);
                return true;
            }
            else{
                System.out.println("new positionnnnn = FALSE"  );
                return false;
                //mapInfo.saveModel(user.getId());
                //send(new ResponseRequestMoveConstruction(ServerConstant.ERROR), user);
            }
                
                   
               } catch (Exception e) {
                return false;
            }
    }

    private void processUpgradeMultiWall(User user, RequestUpgradeMultiWall upgrade_multiWall) {
        logger.info("*****************processUpgradeMultiWall***************" );
        //processUpgradeWall(user, id);
        int exchange_resource = 0;
        int coin = 0;
        int gold = 0;
        int elixir = 0;
        int darkElixir = 0;
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               ////send response error
               send(new ResponseRequestUpgradeConstruction(ServerConstant.ERROR), user);
               return;
            }
            //*------------------------------------------------
            MapInfo mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {               
               //send response error
               send(new ResponseRequestUpgradeConstruction(ServerConstant.ERROR), user);
               return;
            }
            
            for (int i=0; i< upgrade_multiWall.length_wall; i++){
                int id = upgrade_multiWall.listWall.get(i);
                Building building = mapInfo.listBuilding.get(id);
                if (building.type.equals("BDH_1") || building.status.equals(ServerConstant.destroy_status)){
                        send(new ResponseRequestUpgradeConstruction(ServerConstant.ERROR), user);
                        return;
                    }
                
                exchange_resource = exchange_resource + ServerConstant.checkResourceBasedOnType(userInfo,(building.type),building.level+1);
                coin = coin + ServerConstant.getCoin(building.type,building.level+1);
                gold = gold + ServerConstant.getGold(building.type,building.level+1);
                elixir = elixir + ServerConstant.getElixir(building.type,building.level+1);
                darkElixir = darkElixir + ServerConstant.getDarkElixir(building.type,building.level+1);
            }
            
            
            if ((exchange_resource+coin<userInfo.coin)){ 
                System.out.println("so tho xay hien tai la: "+ userInfo.builderNumber);
                // kiem tra tho xay                
                if (mapInfo.getBuilderNotFree()>=userInfo.builderNumber){
                    System.out.println("CAN GIAI PHONG THO XAY");
                    //get resource cua nha                    
                    int g_release = mapInfo.getGToReleaseBuilder();
                    System.out.println("So G de giai phong la "+ g_release);
                    if (userInfo.coin < coin+exchange_resource+g_release ){ //neu khong du tien mua tho xay
                        //linhrafa --Neu false
                        //tra ve false
                        send(new ResponseUpgradeMultiWall(ServerConstant.ERROR), user);
                        return;
                    }
                    else {
                        //giai phong 1 ngoi nha pending
                        
                        mapInfo.releaseBuilding(user); 
                        
                        mapInfo.print();
                        for (int i=0; i< upgrade_multiWall.length_wall; i++){
                            int id = upgrade_multiWall.listWall.get(i);
                            Building building = mapInfo.listBuilding.get(id);
                            userInfo.reduceUserResources(gold,elixir,darkElixir,exchange_resource+coin+g_release, building.type, false);
                            mapInfo.upgradeBuilding(id);
                        }
                                               
                        userInfo.saveModel(user.getId());
                        mapInfo.saveModel(user.getId());
                        logger.info(">>>>>>>>>>>>>in ra sau khi upgrade>>>>>>>");
                        //mapInfo.print();
                        send(new ResponseUpgradeMultiWall(ServerConstant.SUCCESS), user);
                    }                    
                }
                else {
                    for (int i=0; i< upgrade_multiWall.length_wall; i++){
                        int id = upgrade_multiWall.listWall.get(i);
                        Building building = mapInfo.listBuilding.get(id);
                        userInfo.reduceUserResources(gold,elixir,darkElixir,exchange_resource+coin, building.type, false);
                        mapInfo.upgradeBuilding(id);
                    }
                                           
                    userInfo.saveModel(user.getId());
                    mapInfo.saveModel(user.getId());
                    logger.info(">>>>>>>>>>>>>AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA>>>>>>>");
                    //mapInfo.print();
                    
                    send(new ResponseUpgradeMultiWall(ServerConstant.SUCCESS), user);
                }
            }
            else {
                send(new ResponseUpgradeMultiWall(ServerConstant.ERROR), user);
                return;
            }
            
        } catch (Exception e){
            send(new ResponseUpgradeMultiWall(ServerConstant.ERROR), user);
            return;
        }
        
        
        
    }    
}

