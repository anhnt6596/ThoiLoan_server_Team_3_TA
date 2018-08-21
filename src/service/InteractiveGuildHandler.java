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

import java.util.Iterator;

import model.Guild;
import model.GuildBuilding;
import model.MessageGuild;
import model.TroopGuild;
import model.ZPUserInfo;

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
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               //send response error
               send(new ResponseSendNewMessage(ServerConstant.VALIDATE, ServerConstant.ERROR, null), user);
               return;
            }
            
            MessageGuild message = new MessageGuild(packet.type, user.getId(), packet.content, System.currentTimeMillis());
            
            int idGuild = userInfo.id_guild;
            Guild guild = (Guild) Guild.getModel(idGuild, Guild.class);
            
            guild.addMessage(message);
            //Xac nhan add message
            send(new ResponseSendNewMessage(ServerConstant.VALIDATE, ServerConstant.SUCCESS, null), user);
            
            //Send to all members of guild that is online, except sender
            User otherUser;
            for (Integer idUser : guild.list_member.keySet()) {
                //Get user by id
                if(idUser == user.getId()) continue;
                otherUser = BitZeroServer.getInstance().getUserManager().getUserById(idUser);
                send(new ResponseSendNewMessage(ServerConstant.TO_ALL, (short) 0, message), otherUser);
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
               send(new ResponseGiveTroop(ServerConstant.VALIDATE, ServerConstant.ERROR, 0, (short) 0), user);
               return;
            }
            
            GuildBuilding guildBuilding = (GuildBuilding) GuildBuilding.getModel(packet.idUserGet, GuildBuilding.class);
            
            //Check neu nhan them quan se vuot qua guildCapacity
            int guildCapacity = guildBuilding.getGuildCapacity(userInfo);
            int currentGuildCapacity = guildBuilding.getCurrentTroopCapacityGuild();
            int capacityTroopGive = getTroopCapacity(packet.troopType);
            if(currentGuildCapacity + capacityTroopGive > guildCapacity){
                send(new ResponseGiveTroop(ServerConstant.VALIDATE, ServerConstant.ERROR, 0, (short) 0), user);
                return;
            }
            
            //Check So quan ma giveUser da cho trc do
            int amountGave = guildBuilding.userGaveMap.get(user.getId());
            if ((Integer) amountGave != null) {
                if(amountGave >= ServerConstant.MAX_TROOP_AMOUNT_USER_CAN_GIVE){
                    send(new ResponseGiveTroop(ServerConstant.VALIDATE, ServerConstant.ERROR, 0, (short) 0), user);
                    return;
                }
                int newAmount = amountGave++;
                guildBuilding.userGaveMap.put(user.getId(), newAmount);
            } else {
                guildBuilding.userGaveMap.put(user.getId(), 1);
            }
            TroopGuild troopGuild = new TroopGuild(packet.troopType, packet.level);
            guildBuilding.troopGuildList.add(troopGuild);
            
            //Xac nhan give troop cho sender
            send(new ResponseGiveTroop(ServerConstant.VALIDATE, ServerConstant.SUCCESS, 0, (short) 0), user);
            
            //Send to all members of guild except sender
            int idGuild = userInfo.id_guild;
            Guild guild = (Guild) Guild.getModel(idGuild, Guild.class);
            User otherUser;
            for (Integer idUser : guild.list_member.keySet()) {
                //Get user by id
                if(idUser == user.getId()) continue;
                otherUser = BitZeroServer.getInstance().getUserManager().getUserById(idUser);
                int capacityTroop = getTroopCapacity(packet.troopType);
                send(new ResponseGiveTroop(ServerConstant.TO_ALL, (short) 0, packet.idUserGet, (short) capacityTroop), otherUser);
            }
            
            //Neu da full guildCapacity thi reset userGaveMap
            int guildCapacity2 = guildBuilding.getGuildCapacity(userInfo);
            int currentGuildCapacity2 = guildBuilding.getCurrentTroopCapacityGuild();
            if(currentGuildCapacity2 >= guildCapacity2){
                guildBuilding.resetUserGaveMap();
            }
            
            guildBuilding.saveModel(packet.idUserGet);
            
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
            
            send(new ResponseGetInteractionGuild(guildBuilding, guild.list_message), user);
            
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
}
