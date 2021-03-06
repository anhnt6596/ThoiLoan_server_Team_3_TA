package service;

import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.data.DataCmd;

import cmd.CmdDefine;

import cmd.receive.map.RequestAddConstruction;
import cmd.receive.map.RequestCancleConstruction;
import cmd.receive.map.RequestFinishTimeConstruction;
import cmd.receive.map.RequestGetServerTime;
import cmd.receive.map.RequestMapInfo;
import cmd.receive.map.RequestMoveConstruction;
import cmd.receive.map.RequestQuickFinish;
import cmd.receive.map.RequestUpgradeConstruction;
import cmd.receive.troop.RequestQuickFinishResearch;
import cmd.receive.troop.RequestResearch;
import cmd.receive.troop.RequestResearchComplete;
import cmd.receive.troop.RequestTroopInfo;
import cmd.receive.user.RequestAddResource;
import cmd.receive.user.RequestUserInfo;

import cmd.send.demo.ResponseQuickFinishResearch;
import cmd.send.demo.ResponseRequestAddResource;
import cmd.send.demo.ResponseRequestMapInfo;
import cmd.send.demo.ResponseRequestQuickFinish;
import cmd.send.demo.ResponseRequestUserInfo;

import cmd.send.demo.ResponseResearch;
import cmd.send.demo.ResponseResearchComplete;
import cmd.send.demo.ResponseTroopInfo;

import cmd.send.train.ResponseRequestBarrackQueueInfo;

import extension.FresherExtension;

import java.util.Iterator;
import java.util.List;

import java.util.Map;

import model.Building;
import model.MapInfo;
import model.Troop;
import model.TroopInfo;
import model.ZPUserInfo;

import model.train.BarrackQueueInfo;

import org.apache.commons.lang.exception.ExceptionUtils;


import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.server.ServerConstant;

public class TroopHandle extends BaseClientRequestHandler {
    public static short TROOP_MULTI_IDS = 4000;
    private final Logger logger = LoggerFactory.getLogger("TroopHandle");
    public TroopHandle() {
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
                case CmdDefine.GET_TROOP_INFO:
                    RequestTroopInfo packet1 = new RequestTroopInfo(dataCmd);
                    processTroopInfo(user, packet1);
                    break;
                case CmdDefine.RESEARCH_TROOP:
                    RequestResearch packet2 = new RequestResearch(dataCmd);
                    processResearchTroop(user, packet2);
                    break;
                case CmdDefine.RESEARCH_TROOP_COMPLETE:
                    RequestResearchComplete packet3 = new RequestResearchComplete(dataCmd);
                    processResearchComplete(user, packet3);
                    break;
                case CmdDefine.RESEARCH_TROOP_QUICK_FINISH:
                    RequestQuickFinishResearch packet4 = new RequestQuickFinishResearch(dataCmd);
                    processQuickFinishResearch(user, packet4);
                    break;
            }
        } catch (Exception e) {
            logger.warn("DEMO HANDLER EXCEPTION " + e.getMessage());
            logger.warn(ExceptionUtils.getStackTrace(e));
        }

    }

    private void processTroopInfo(User user, RequestTroopInfo troop) {
        try {
            //Check newest population
            try {
                BarrackQueueInfo barrackQueueInfo = (BarrackQueueInfo) BarrackQueueInfo.getModel(user.getId(), BarrackQueueInfo.class);
                if(barrackQueueInfo != null){
                    barrackQueueInfo.checkFirst(user);      
                    barrackQueueInfo.saveModel(user.getId());
                }          
            } catch (Exception e) {
                System.out.println(e);
            }
            
            System.out.println("get troop info, userId: " + user.getId());
            TroopInfo troopInfo = (TroopInfo) TroopInfo.getModel(user.getId(), TroopInfo.class);
            if (troopInfo == null) {
                System.out.println("==> troopInfo null");
                troopInfo = new TroopInfo();
                troopInfo.saveModel(user.getId());
            } else {
                Map troopMap = troopInfo.troopMap;
                long i = 0;
                Iterator<Map.Entry<String, Troop>> it = troopMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Troop> pair = it.next();
                    Troop _troop = pair.getValue();
                    if (_troop.status.equals("researching")) {
                        long currentTime = System.currentTimeMillis();
                        long startTime = _troop.startTime;
                        long passTime = currentTime - startTime;
                        long requestTime = 1000 * ServerConstant.configTroop
                            .getJSONObject(_troop.type)
                            .getJSONObject(String.valueOf(_troop.level + 1))
                            .getInt("researchTime");
                        if (passTime >= requestTime) {
                            _troop.levelUp();
                            troopInfo.troopMap.put(_troop.type, _troop);
                            troopInfo.saveModel(user.getId());
                        }
                    }
                }
            }
            
            
            send(new ResponseTroopInfo(troopInfo), user);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processResearchTroop(User user, RequestResearch troop) {
        try {
            System.out.println("research troop: id " + user.getId() + " & type: " + troop.type);
            MapInfo mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            
            if (mapInfo == null) {
                //send response error
                send(new ResponseResearch(ServerConstant.ERROR), user);
                return;
            }
        //            mapInfo.listBuilding
            List<Building> listBuilding = mapInfo.listBuilding;
            for (int i = 0; i < listBuilding.size(); ++i) {
                Building building = listBuilding.get(i);
                if (building.type.equals("LAB_1")) {
                    if (building.status.equals("complete")) {
                        System.out.println("building.status: " + building.status);
                        int troopLevel = this.getTroopLevel(user, troop.type);
                        if (troopLevel == -1) {
                            System.out.println("ERROR: Khong thay thong tin cua " + troop.type);
                            send(new ResponseResearch(ServerConstant.ERROR), user);
                        }
                        int labLevel = building.level;
                        JSONObject troopNextLvInfo = ServerConstant.configTroop
                            .getJSONObject(troop.type)
                            .getJSONObject(String.valueOf(troopLevel + 1));
                        int laboratoryLevelRequired = troopNextLvInfo.getInt("laboratoryLevelRequired");
                        System.out.println("TROOP_LEVEL : " + laboratoryLevelRequired);
                        if (labLevel < laboratoryLevelRequired) {
                            System.out.println("ERROR: Nha nghien cuu khong du cap de nghien cuu linh");
                            send(new ResponseResearch(ServerConstant.ERROR), user);
                            return;
                        }
                        ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
                        int requiredElixir = troopNextLvInfo.getInt("researchElixir");
                        int requireDarkElixir = troopNextLvInfo.getInt("researchDarkElixir");
                        int requireCoin = 0;
                        if (userInfo.elixir < requiredElixir || userInfo.darkElixir < requireDarkElixir) {
                            if (userInfo.elixir < requiredElixir) {
                                requireCoin += requiredElixir - userInfo.elixir;
                                requiredElixir = userInfo.elixir;
                            }
                            if (userInfo.darkElixir < requireDarkElixir) {
                                requireCoin += requireDarkElixir - userInfo.darkElixir;
                                requireDarkElixir = userInfo.darkElixir;
                            }
                            if (userInfo.coin < requireCoin) {
                                System.out.println("ERROR: Nguoi choi thieu tai nguyen de nang cap!");
                                send(new ResponseResearch(ServerConstant.ERROR), user);
                                return;
                            }
                        }
                        //troopLevelUp(user, troop.type);
                        send(new ResponseResearch(ServerConstant.SUCCESS), user);
                        this.startResearchTroop(user, troop.type, requiredElixir, requireDarkElixir, requireCoin);
                        System.out.println("Yeu cau nghien cuu thanh cong_____SUCCESS");
                        return;
                    } else {
                        System.out.println("ERROR: Nha nghien cuu khong o trang thai hoan thanh 'complete'!");
                        send(new ResponseResearch(ServerConstant.ERROR), user);
                        return;
                    }
                }
            }
            System.out.println("ERROR: Khong co nha Nghien cuu!");
            send(new ResponseResearch(ServerConstant.ERROR), user);
        } catch (Exception e) {
            send(new ResponseResearch(ServerConstant.ERROR), user);
            System.out.println("ERROR: Khong nghien cuu duoc!");
        }
    }

    private void processResearchComplete(User user, RequestResearchComplete packet3) {
        System.out.println("RESEARCH COMPLETE REQUEST : " + packet3.type);
        try {
            TroopInfo troopInfo = (TroopInfo) TroopInfo.getModel(user.getId(), TroopInfo.class);
            if (troopInfo == null) {
                //send response error
                send(new ResponseResearchComplete(ServerConstant.ERROR), user);
                return;
            }
            Troop troop = troopInfo.troopMap.get(packet3.type);
            if (!troop.status.equals("researching")) {
                System.out.println("ERROR: Quan linh khong trong trang thai dang nghien cuu!");
                send(new ResponseResearchComplete(ServerConstant.ERROR), user);
                return;
            }
            long currentTime = System.currentTimeMillis();
            long startTime = troop.startTime;
            long passTime = currentTime - startTime;
            long requestTime = 1000 * ServerConstant.configTroop
                .getJSONObject(troop.type)
                .getJSONObject(String.valueOf(troop.level + 1))
                .getInt("researchTime");
            if (passTime < requestTime) {
                System.out.println("ERROR: Chua du thoi gian nghien cuu!");
                send(new ResponseResearchComplete(ServerConstant.ERROR), user);
                return;
            }
            System.out.println("SUCCESS: Nghien cuu thanh cong!");
            this.troopLevelUp(user, packet3.type);
            send(new ResponseResearchComplete(ServerConstant.SUCCESS), user);
            return;
        } catch (Exception e) {
            
        }
        send(new ResponseResearchComplete(ServerConstant.ERROR), user);
    }

    private void processQuickFinishResearch(User user, RequestQuickFinishResearch packet4) {
        System.out.println("RESEARCH QUICK FINISH COMPLETE REQUEST : " + packet4.type);
        try {
            TroopInfo troopInfo = (TroopInfo) TroopInfo.getModel(user.getId(), TroopInfo.class);
            if (troopInfo == null) {
                //send response error
                send(new ResponseQuickFinishResearch(ServerConstant.ERROR), user);
                return;
            }
            Troop troop = troopInfo.troopMap.get(packet4.type);
            if (!troop.status.equals("researching")) {
                System.out.println("ERROR: Quan linh khong trong trang thai dang nghien cuu!");
                send(new ResponseQuickFinishResearch(ServerConstant.ERROR), user);
                return;
            }
            long currentTime = System.currentTimeMillis();
            long startTime = troop.startTime;
            long passTime = currentTime - startTime;
            long requestTime = 1000 * ServerConstant.configTroop
                .getJSONObject(troop.type)
                .getJSONObject(String.valueOf(troop.level + 1))
                .getInt("researchTime");
            long timeLeft = requestTime - passTime;
            if (timeLeft <= 0) {
                System.out.println("ERROR: Nghien cuu da xong roi!");
                send(new ResponseQuickFinishResearch(ServerConstant.ERROR), user);
                return;
            } else {
                int reqG = (int) Math.ceil(timeLeft / 60000);
                boolean ok = reduceG(user, reqG);
                if(ok) {
                    System.out.println("SUCCESS: Nghien cuu thanh cong: " + reqG + "G.");
                    this.troopLevelUp(user, packet4.type);
                    send(new ResponseQuickFinishResearch(ServerConstant.SUCCESS), user);
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: C� l?i x?y ra!");
            send(new ResponseQuickFinishResearch(ServerConstant.ERROR), user);
            return;
        }
        send(new ResponseQuickFinishResearch(ServerConstant.ERROR), user);
    }
    
    private void troopLevelUp(User user, String type) {
        try {
            TroopInfo troopInfo = (TroopInfo) TroopInfo.getModel(user.getId(), TroopInfo.class);
            Troop troop = troopInfo.troopMap.get(type);
            System.out.println("LEVEL : " + troop.level);
            troop.levelUp();
            troopInfo.troopMap.put(type, troop);
            troopInfo.saveModel(user.getId());
        } catch (Exception e) {
        }
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

    private void startResearchTroop(User user, String type, int reqElixir, int reqDarkElixir, int reqCoin) {
        try {
            TroopInfo troopInfo = (TroopInfo) TroopInfo.getModel(user.getId(), TroopInfo.class);
            Troop troop = troopInfo.troopMap.get(type);
            troop.setStatus("researching");
            troop.setStartTime(System.currentTimeMillis());
            troopInfo.troopMap.put(type, troop);
            troopInfo.saveModel(user.getId());

            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            userInfo.reduceUserResources(0, reqElixir, reqDarkElixir, reqCoin, "", false);
            userInfo.saveModel(user.getId());
        } catch (Exception e) {
        }
    }

    private boolean reduceG(User user, int reqG) {
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            int coin = userInfo.coin;
            if (coin < reqG) {
                System.out.println("ERROR: Khong du G!");
                return false;
            }
            userInfo.reduceUserResources(0, 0, 0, reqG, "", false);
            userInfo.saveModel(user.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}