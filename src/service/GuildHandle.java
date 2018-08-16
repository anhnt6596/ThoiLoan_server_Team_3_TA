package service;

import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.ExtensionUtility;
import bitzero.util.socialcontroller.bean.UserInfo;

import cmd.CmdDefine;
import cmd.receive.guild.RequestAddMember;
import cmd.receive.guild.RequestAddRequestMember;
import cmd.receive.guild.RequestCreateGuild;

import cmd.receive.guild.RequestDenyRequestMember;
import cmd.receive.guild.RequestRemoveMember;

import cmd.send.guild.ResponseCreateGuild;
import cmd.send.demo.ResponseRequestAddConstruction;

import cmd.send.demo.ResponseRequestCancleConstruction;
import cmd.send.demo.ResponseRequestQuickFinish;
import cmd.send.guild.ResponseAddMember;

import cmd.send.guild.ResponseAddRequestMember;
import cmd.send.guild.ResponseDenyRequestMember;
import cmd.send.guild.ResponseRemoveMember;

import java.util.List;

import java.util.Map;

import model.Building;
import model.Guild;
import model.ListGuild;
import model.MapInfo;
import model.ZPUserInfo;
import org.apache.commons.lang.exception.ExceptionUtils;

import org.json.JSONException;

import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.server.ServerConstant;

public class GuildHandle extends BaseClientRequestHandler {
    
    public static short GUILD_MULTI_IDS = 5000;
    private final Logger logger = LoggerFactory.getLogger("GuildHandle");    
    
    public GuildHandle() {
        super();
    }
    
    public void init() {
        getParentExtension().addEventListener(BZEventType.PRIVATE_MESSAGE, this);        
    }

    @Override
    public void handleClientRequest(User user, DataCmd dataCmd) {
        try {
            switch (dataCmd.getId()) {
                case CmdDefine.CREATE_GUILD:
                        //System.out.println("GET_SERVER_TIME");
                        RequestCreateGuild create_guild = new RequestCreateGuild(dataCmd);
                        processCreateGuild(user, create_guild);
                        break;
                case CmdDefine.ADD_MEMBER:
                        //System.out.println("GET_SERVER_TIME");
                        RequestAddMember member_add = new RequestAddMember(dataCmd);
                        processAddMember(user, member_add);
                        break;
                case CmdDefine.REMOVE_MEMBER:
                        //System.out.println("GET_SERVER_TIME");
                        RequestRemoveMember member_remove = new RequestRemoveMember(dataCmd);
                        processRemoveMember(user, member_remove);
                        break;
                case CmdDefine.ADD_REQUEST_MEMBER:
                        //System.out.println("GET_SERVER_TIME");
                        RequestAddRequestMember member_add_rq = new RequestAddRequestMember(dataCmd);
                        processAddRequestMember(user, member_add_rq);
                        break;                
                case CmdDefine.DENY_REQUEST_MEMBER:
                        //System.out.println("GET_SERVER_TIME");
                        RequestDenyRequestMember member_deny_rq = new RequestDenyRequestMember(dataCmd);
                        processDenyRequestMember(user, member_deny_rq);
                        break;
//               case CmdDefine.GET_GUILD_INFO:
//                        //System.out.println("GET_SERVER_TIME");
//                        RequestCreateGuild create_guild = new RequestCreateGuild(dataCmd);
//                        processCreateGuild(user, create_guild);
//                        break;
//                case CmdDefine.EDIT_GUILD_INFO:
//                        //System.out.println("GET_SERVER_TIME");
//                        RequestCreateGuild create_guild = new RequestCreateGuild(dataCmd);
//                        processCreateGuild(user, create_guild);
//                        break;
                
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
    private void processEventPrivateMsg(User user){
        /**
         * process event
         */
        logger.info("processEventPrivateMsg, userId = " + user.getId());
    }
    private void processCreateGuild(User user, RequestCreateGuild create_guild) {
        logger.info("*************************processCreateGuild***************");
        
        try {
            ZPUserInfo memberInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (memberInfo == null) {
               ////send response error
                logger.debug("khong ton tai user xin gia nhap");
               send(new ResponseCreateGuild(ServerConstant.ERROR), user);
               return;
            }
            ListGuild listGuild = (ListGuild) ListGuild.getModel(1, ListGuild.class);
            if (listGuild ==null){
                listGuild = new ListGuild();
                listGuild.saveModel(1);
            }
            
            Guild guild = guild = new Guild(user.getId(),create_guild.name, create_guild.logo_id);
            
            memberInfo.addGuildInfo(guild.id, guild.name, guild.logo_id);
            
            memberInfo.saveModel(user.getId());
            guild.saveModel(user.getId());
            send(new ResponseCreateGuild(ServerConstant.SUCCESS), user);
            
        } catch (Exception e) {
        }
        
    }
  

    private void processAddMember(User user, RequestAddMember member) {
        logger.info("*************************processAddMember***************");        
        try {
            User new_member = ExtensionUtility.globalUserManager.getUserById(member.id);
//            User user = BitZeroServer.getInstance().getUserManager().getUserById(id);
            
            ZPUserInfo memberInfo = (ZPUserInfo) ZPUserInfo.getModel(member.id, ZPUserInfo.class);
            if (memberInfo == null) {
               ////send response error
                logger.debug("khong ton tai user xin gia nhap");
               send(new ResponseAddMember(ServerConstant.ERROR), user);
               return;
            }
            
            Guild guild = (Guild) Guild.getModel(user.getId(), Guild.class);
            if (guild == null) {
                logger.debug("Khong ton tai guild, id guild = "+ user.getId());
                send(new ResponseAddMember(ServerConstant.ERROR), user); 
                return;
            }
            guild.addMember(member.id, ServerConstant.guild_member);
            memberInfo.addGuildInfo(guild.id, guild.name, guild.logo_id);
            
            memberInfo.saveModel(member.id);
            guild.saveModel(guild.id);
            
            send(new ResponseAddMember(ServerConstant.SUCCESS), user); 
            
        } catch (Exception e) {
        }
        
    }

    private void processRemoveMember(User user, RequestRemoveMember member_remove) {
        logger.info("*************************processRemoveMember***************");        
        try {
            ZPUserInfo memberInfo = (ZPUserInfo) ZPUserInfo.getModel(member_remove.id, ZPUserInfo.class);
            if (memberInfo == null) {
               ////send response error
                logger.debug("khong ton tai user bi kick");
               send(new ResponseRemoveMember(ServerConstant.ERROR), user);
               return;
            }
            
            Guild guild = (Guild) Guild.getModel(user.getId(), Guild.class);
            if (guild == null) {
                logger.debug("Khong ton tai guild, id guild = "+ user.getId());
                send(new ResponseRemoveMember(ServerConstant.ERROR), user); 
                return;
            }
            guild.removeMember(member_remove.id);
            memberInfo.leftGuild();
            
            memberInfo.saveModel(member_remove.id);
            guild.saveModel(guild.id);
            
            send(new ResponseAddMember(ServerConstant.SUCCESS), user); 
            
        } catch (Exception e) {
        }
    }

    private void processAddRequestMember(User user, RequestAddRequestMember member_add_rq) {
        logger.info("*************************processAddRequestMember***************");        
        try {
            ZPUserInfo memberInfo = (ZPUserInfo) ZPUserInfo.getModel(member_add_rq.id, ZPUserInfo.class);
            if (memberInfo == null) {
               ////send response error
                logger.debug("khong ton tai user yeu cau vao bang");
               send(new ResponseAddRequestMember(ServerConstant.ERROR), user);
               return;
            }
            
            Guild guild = (Guild) Guild.getModel(user.getId(), Guild.class);
            if (guild == null) {
                logger.debug("Khong ton tai guild, id guild = "+ user.getId());
                send(new ResponseAddRequestMember(ServerConstant.ERROR), user); 
                return;
            }
            if (!guild.checkListRequire(member_add_rq.id)){
                logger.debug("Member dc them vao khong nam trong list request");
                send(new ResponseAddRequestMember(ServerConstant.ERROR), user); 
                return;
            }
            guild.addRequestMember(member_add_rq.id, memberInfo.name);            
            
            guild.saveModel(guild.id);
            
            send(new ResponseAddRequestMember(ServerConstant.SUCCESS), user); 
            
        } catch (Exception e) {
        }
    }

    private void processDenyRequestMember(User user, RequestDenyRequestMember member_deny_rq) {
        logger.info("*************************processDenyRequestMember***************");        
        try {
            ZPUserInfo memberInfo = (ZPUserInfo) ZPUserInfo.getModel(member_deny_rq.id, ZPUserInfo.class);
            if (memberInfo == null) {
               ////send response error
                logger.debug("khong ton tai user bi tu choi yeu cau vao bang");
               send(new ResponseDenyRequestMember(ServerConstant.ERROR), user);
               return;
            }
            
            Guild guild = (Guild) Guild.getModel(user.getId(), Guild.class);
            if (guild == null) {
                logger.debug("Khong ton tai guild, id guild = "+ user.getId());
                send(new ResponseDenyRequestMember(ServerConstant.ERROR), user); 
                return;
            }
            if (!guild.checkListRequire(member_deny_rq.id)){
                logger.debug("Member bi tu choi khong nam trong list request");
                send(new ResponseDenyRequestMember(ServerConstant.ERROR), user); 
                return;
            }
            guild.removeRequestMember(member_deny_rq.id);            
            
            guild.saveModel(guild.id);
            
            send(new ResponseDenyRequestMember(ServerConstant.SUCCESS), user); 
            
        } catch (Exception e) {
        }
    }
}


