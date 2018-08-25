package service;

import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventType;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.data.DataCmd;

import cmd.CmdDefine;

import cmd.receive.guild.RequestGetInteractionGuild;
import cmd.receive.guild.RequestGiveTroop;
import cmd.receive.guild.RequestSendNewMessage;

import cmd.send.guild.ResponseGetInteractionGuild;
import cmd.send.guild.ResponseGiveTroop;
import cmd.send.guild.ResponseSendNewMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import java.util.List;

import java.util.Map;

import model.Guild;
import model.GuildBuilding;
import model.MessageGuild;
import model.Troop;
import model.TroopGuild;
import model.TroopInfo;
import model.ZPUserInfo;

import model.train.TroopInBarrack;

import org.apache.commons.lang.exception.ExceptionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.server.ServerConstant;

public class InteractiveGuildHandler extends BaseClientRequestHandler {
    public static short INTERACTIVE_MULTI_IDS = 8000;
    private final Logger logger = LoggerFactory.getLogger("TrainTroopHandle");
    public InteractiveGuildHandler() {
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
                case CmdDefine.GET_INTERACTION_GUILD:
                    RequestGetInteractionGuild guildMessage = new RequestGetInteractionGuild(dataCmd);
                    processRequestGetInteractionGuild(user, guildMessage);
                    break;
                case CmdDefine.NEW_MESSAGE:
                    RequestSendNewMessage messagePacket = new RequestSendNewMessage(dataCmd);
                    processRequestNewMessage(user, messagePacket);
                    break;
                case CmdDefine.GIVE_TROOP_GUILD:
                    RequestGiveTroop packet = new RequestGiveTroop(dataCmd);
                    processRequestGiveTroop(user, packet);
                    break;
            }
        } catch (Exception e) {
            logger.warn("DEMO HANDLER EXCEPTION " + e.getMessage());
            logger.warn(ExceptionUtils.getStackTrace(e));
        }

    }
    
    private void processRequestNewMessage(User user, RequestSendNewMessage packet) {
        try {
            System.out.println("================== HERE 0 ===============");
            
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            System.out.println("================== HERE 1 ===============");
            if (userInfo == null) {
               //send response error
               send(new ResponseSendNewMessage(ServerConstant.VALIDATE, ServerConstant.ERROR, null, null), user);
               return;
            }
            
            System.out.println("================== HERE 2 ===============");
            
            GuildBuilding guildBuilding = (GuildBuilding) GuildBuilding.getModel(user.getId(), GuildBuilding.class);
            
            System.out.println("================== HERE 3 ===============");
            
            //Lay guild Capacity tai thoi diem do
            int guildCapacity = guildBuilding.getGuildCapacity(userInfo);
            System.out.println("===================== GET GUILD CAPACITY: ====================== " + guildCapacity);
            
            //Lay currentTroopCapacity tai thoi diem do. Co the da ton tai troop trc do
            int currentTroopCapacity = guildBuilding.getCurrentTroopCapacityGuild();
            MessageGuild message = new MessageGuild(packet.type, user.getId(), packet.content, System.currentTimeMillis(), guildCapacity, currentTroopCapacity);
            
            int idGuild = userInfo.id_guild;
            Guild guild = (Guild) Guild.getModel(idGuild, Guild.class);
            
            
            guild.addMessage(message);
            

            //Xac nhan add message
            send(new ResponseSendNewMessage(ServerConstant.VALIDATE, ServerConstant.SUCCESS, null, null), user);
            
            
            //Send to all members of guild that is online, except sender
            String nameSender = userInfo.getName();
            User otherUser;
            for (Integer idUser : guild.list_member.keySet()) {
                //Get user by id
                if(idUser == user.getId()) continue;
                otherUser = BitZeroServer.getInstance().getUserManager().getUserById(idUser);
                if(otherUser != null){
                    send(new ResponseSendNewMessage(ServerConstant.TO_ALL, (short) 0, message, nameSender), otherUser);                    
                }
            }
            
            guild.saveModel(idGuild);
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void processRequestGiveTroop(User user, RequestGiveTroop packet) {
        try {
            //user nhan troop
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(packet.idUserGet, ZPUserInfo.class);
            if (userInfo == null) {
               send(new ResponseGiveTroop(ServerConstant.VALIDATE, ServerConstant.ERROR, 0, null, 0, 0), user);
               return;
            }
            GuildBuilding guildBuildingGetter = (GuildBuilding) GuildBuilding.getModel(packet.idUserGet, GuildBuilding.class);
            
            //Check neu userGet nhan them quan se vuot qua guildCapacity
            int guildCapacity = guildBuildingGetter.getGuildCapacity(userInfo);
            int currentGuildCapacity = guildBuildingGetter.getCurrentTroopCapacityGuild();
            int capacityTroopGive = getTroopCapacity(packet.troopType);
            if(currentGuildCapacity + capacityTroopGive > guildCapacity){
                send(new ResponseGiveTroop(ServerConstant.VALIDATE, ServerConstant.ERROR, 0, null, 0, 0), user);
                return;
            }
            
            //Check So quan ma giveUser da cho trc do
            GuildBuilding guildBuildingGiver = (GuildBuilding) GuildBuilding.getModel(user.getId(), GuildBuilding.class);
            Integer amountGave = guildBuildingGiver.userGotMap.get(packet.idUserGet);
            if (amountGave != null) {
                if(amountGave >= ServerConstant.MAX_TROOP_AMOUNT_USER_CAN_GIVE){
                    System.out.println("================== VUOT QUA 5 TROOP CHO PHEP ===============");
                    send(new ResponseGiveTroop(ServerConstant.VALIDATE, ServerConstant.ERROR, 0, null, 0, 0), user);
                    return;
                }
                System.out.println("================== HERE 1 ===============");
                int newAmount = amountGave.intValue() + 1;
                guildBuildingGiver.userGotMap.put(packet.idUserGet, newAmount);
            } else {
                System.out.println("================== HERE 2 ===============");
                guildBuildingGiver.userGotMap.put(packet.idUserGet, 1);
            }
            
            System.out.println("================== HERE 3 ===============");
            //Giam population troop cua sender
            this.decreaseAmountTroop(user.getId(), packet.troopType, 1);
            
            //Cap nhat troopList cho nguoi nhan
            TroopGuild troopGuild = new TroopGuild(packet.troopType, packet.level);
            guildBuildingGetter.troopGuildList.add(troopGuild);
            
            //Cap nhat lai message xin quan cua userGet
            int idGuild = userInfo.id_guild;
            Guild guild = (Guild) Guild.getModel(idGuild, Guild.class);
            guild.updateRequestTroopMessage(packet.idUserGet, capacityTroopGive);
            
            //Send xac nhan give troop cho sender
            send(new ResponseGiveTroop(ServerConstant.VALIDATE, ServerConstant.SUCCESS, 0, null, 0, 0), user);
                
            //Send to all members of guild except sender
            User otherUser;
            for (Integer idUser : guild.list_member.keySet()) {
                //Get user by id
                if(idUser == user.getId()) continue;
                otherUser = BitZeroServer.getInstance().getUserManager().getUserById(idUser);
                if(otherUser != null){
                    send(new ResponseGiveTroop(ServerConstant.TO_ALL, (short) 0, packet.idUserGet, packet.troopType, packet.level, user.getId()), otherUser);
                }
            }
            
            //Neu da full guildCapacity thi reset userGotMap cua tat ca ng trong bang va xoa message xin quan
            if(currentGuildCapacity + capacityTroopGive == guildCapacity){
                //Reset userGotMap cua all user trong bang
                this.resetAllUserGotMapWhenFullRequestTroop(packet.idUserGet, guild);
                
                guild.removeMessageRequestTroop(packet.idUserGet);
            }
            
            //Cap nhat so quan cho, so quan nhan cua moi nguoi
            this.updateTransaction(user.getId(), packet.idUserGet);
            
            guild.saveModel(idGuild);
            guildBuildingGetter.saveModel(packet.idUserGet);
            guildBuildingGiver.saveModel(user.getId());

            
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void processRequestGetInteractionGuild(User user, RequestGetInteractionGuild packet) {
        try{
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               return;
            }
            
            GuildBuilding guildBuilding = (GuildBuilding) GuildBuilding.getModel(user.getId(), GuildBuilding.class);
            Guild guild = (Guild) Guild.getModel(userInfo.id_guild, Guild.class);
            
            Map <Integer, Short> listMemberOnline = new HashMap <Integer, Short>();
            User otherUser;
            for (Integer idUser : guild.list_member.keySet()) {
                otherUser = BitZeroServer.getInstance().getUserManager().getUserById(idUser);
                if(otherUser != null){
                    listMemberOnline.put(idUser, ServerConstant.ONLINE);
                }else{
                    listMemberOnline.put(idUser, ServerConstant.OFFLINE);                    
                }
            }
            
            send(new ResponseGetInteractionGuild(guildBuilding, guild.list_message, listMemberOnline), user);

        }catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private int getTroopCapacity(String troopType) {
        JSONObject troopConfig = ServerConstant.configTroopBase;
        int space;
        try {
            space = troopConfig.getJSONObject(troopType).getInt("housingSpace");
        } catch (JSONException e) {
            return 0;
        }
        return space;
    }
    
    private void decreaseAmountTroop(int userId, String typeTroop, int amount) {
        //Giam so luong loai troop nay
        TroopInfo troopInfo;
        try {
            troopInfo = (TroopInfo) TroopInfo.getModel(userId, TroopInfo.class);
        } catch (Exception e) {
            return;
        }
        
        Troop troopObj = troopInfo.troopMap.get(typeTroop);
        troopObj.population -= amount;
        troopInfo.troopMap.put(typeTroop, troopObj);
        try {
            troopInfo.saveModel(userId);
        } catch (Exception e) {
        }
    }
    
    private void resetAllUserGotMapWhenFullRequestTroop(int idUser, Guild guild) {
        GuildBuilding guildBuilding;
        for (Integer id : guild.list_member.keySet()) {
            try {
                guildBuilding = (GuildBuilding) GuildBuilding.getModel(id, GuildBuilding.class);
            } catch (Exception e) {
                return;
            }
            
            Integer value = guildBuilding.userGotMap.get(idUser);
            if (value != null) {
                guildBuilding.userGotMap.put(idUser, 0);
            }
            try {
                guildBuilding.saveModel(id);
            } catch (Exception e) {
            }
        }   
    }
       
    //cap nhat so luong give, so luong get cua 2 ng
    private void updateTransaction(int idUserGive, int idUserGet) {
        ZPUserInfo userGive;
        ZPUserInfo userGet;
        try{
            userGive = (ZPUserInfo) ZPUserInfo.getModel(idUserGive, ZPUserInfo.class);
            userGet = (ZPUserInfo) ZPUserInfo.getModel(idUserGet, ZPUserInfo.class);
        }catch(Exception e){
            return;
        }
        
        System.out.println("================ So quan da donate: " + userGive.donate_troop);
        
        userGive.donate_troop += 1;
        userGet.request_troop += 1;

        try {
            userGive.saveModel(idUserGive);
            userGet.saveModel(idUserGet);
        } catch (Exception e) {
        }
    }
}
