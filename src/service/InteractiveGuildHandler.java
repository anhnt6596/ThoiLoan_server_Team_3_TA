package service;

import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventType;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.data.DataCmd;

import cmd.CmdDefine;

import cmd.receive.guild.RequestGiveTroop;
import cmd.receive.guild.RequestSendNewMessage;
import cmd.receive.train.RequestCancelTrainTroop;
import cmd.receive.train.RequestFinishTimeTrainTroop;
import cmd.receive.train.RequestTrainTroop;
import cmd.receive.troop.RequestQuickFinishTrainTroop;

import cmd.send.demo.ResponseRequestQuickFinish;
import cmd.send.guild.ResponseGiveTroop;
import cmd.send.guild.ResponseSendNewMessage;
import cmd.send.train.ResponseRequestBarrackQueueInfo;

import model.Guild;
import model.GuildBuilding;
import model.MessageGuild;
import model.TroopGuild;
import model.ZPUserInfo;

import model.train.BarrackQueueInfo;

import org.apache.commons.lang.exception.ExceptionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.database.DataModel;

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
            
            //Send to all members of guild except sender
            User otherUser;
            for (Integer idUser : guild.list_member.keySet()) {
                //Get user by id
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
               //send response error
               send(new ResponseGiveTroop(ServerConstant.VALIDATE, ServerConstant.ERROR, 0, (short) 0), user);
               return;
            }
            
            int idGuild = userInfo.id_guild;
            Guild guild = (Guild) Guild.getModel(idGuild, Guild.class);
            
            TroopGuild troopGuild = new TroopGuild(packet.troopType, packet.level);
            
            GuildBuilding guildBuilding = (GuildBuilding) GuildBuilding.getModel(user.getId(), GuildBuilding.class);
            
            int levelGuildBuilding = guildBuilding.getLevelGuildBuilding(userInfo);
            
            JSONObject guildConfig = ServerConstant.configClanCastle.getJSONObject("CLC_1");
            int troopCapacity;
            try {
                troopCapacity = guildConfig.getJSONObject(Integer.toString(levelGuildBuilding)).getInt("troopCapacity");
            } catch (JSONException e) {
                return;
            }
            
            
            guild.saveModel(idGuild);
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
