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
import cmd.receive.guild.RequestEditGuildInfo;
import cmd.receive.guild.RequestGetGuildInfo;
import cmd.receive.guild.RequestRemoveMember;

import cmd.receive.guild.RequestSearchGuild;

import cmd.send.guild.ResponseCreateGuild;
import cmd.send.demo.ResponseRequestAddConstruction;

import cmd.send.demo.ResponseRequestCancleConstruction;
import cmd.send.demo.ResponseRequestQuickFinish;
import cmd.send.guild.ResponseAddMember;

import cmd.send.guild.ResponseAddRequestMember;
import cmd.send.guild.ResponseDenyRequestMember;
import cmd.send.guild.ResponseEditGuildInfo;
import cmd.send.guild.ResponseGetGuildInfo;
import cmd.send.guild.ResponseRemoveMember;

import cmd.send.guild.ResponseSearchGuild;

import java.util.List;

import java.util.Map;

import java.util.PriorityQueue;
import java.util.Queue;

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
               case CmdDefine.SEARCH_GUILD_INFO:
                        //System.out.println("GET_SERVER_TIME");
                        RequestSearchGuild search_guild = new RequestSearchGuild(dataCmd);
                        processSearchGuild(user, search_guild);
                        break;
                case CmdDefine.GET_GUILD_INFO:
                        //System.out.println("GET_SERVER_TIME");
                        RequestGetGuildInfo get_guild_info = new RequestGetGuildInfo(dataCmd);
                        processGetGuildInfo(user, get_guild_info);
                        break;
                case CmdDefine.EDIT_GUILD_INFO:
                        //System.out.println("GET_SERVER_TIME");
                        RequestEditGuildInfo edit_guild_info = new RequestEditGuildInfo(dataCmd);
                        processEditGuildInfo(user, edit_guild_info);
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
    private void processEventPrivateMsg(User user){
        /**
         * process event
         */
        logger.info("processEventPrivateMsg, userId = " + user.getId());
    }
    private void processCreateGuild(User user, RequestCreateGuild create_guild) {
        logger.info("*************************processCreateGuild***************");
        
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               ////send response error
                logger.debug("khong ton tai user tao bang");
               send(new ResponseCreateGuild(ServerConstant.ERROR), user);
               return;
            }
            int g_chuyendoi = checkResourceExchange(userInfo,ServerConstant.CREATE_GUILD_COST);
            if (g_chuyendoi>userInfo.getCoin()){
                logger.info("User khong du tien tao bang");
                send(new ResponseCreateGuild(ServerConstant.ERROR), user);
                return;
            }
            userInfo.reduceUserResources(ServerConstant.CREATE_GUILD_COST,0,0,0,"",false);
            logger.info("Tao bang moi voi ten = "+ create_guild.name+" logo_id= "+ create_guild.logo_id);
            ListGuild listGuild = (ListGuild) ListGuild.getModel(1, ListGuild.class);
            if (listGuild ==null){ //neu listGuild chua duoc khoi tao
                listGuild = new ListGuild();
                listGuild.addGuild(create_guild.name);
                listGuild.saveModel(1);
            }
            
            Guild guild = guild = new Guild(listGuild.numberOfGuild,create_guild.name, create_guild.logo_id, create_guild.status, create_guild.require_danh_vong, create_guild.description);
            
            userInfo.addGuildInfo(guild.id, guild.name, guild.logo_id);
            
            userInfo.saveModel(user.getId());
            guild.saveModel(guild.id);
            send(new ResponseCreateGuild(ServerConstant.SUCCESS), user);
            
        } catch (Exception e) {
        }
        
    }
  

    private void processAddMember(User user, RequestAddMember new_member) {
        logger.info("*************************processAddMember***************");        
        try {
            //User new_member = ExtensionUtility.globalUserManager.getUserById(member.id);
//            User user = BitZeroServer.getInstance().getUserManager().getUserById(id);
            ZPUserInfo leader = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (leader == null) {
               ////send response error
                logger.debug("khong ton tai user bang chu");
               send(new ResponseAddMember(ServerConstant.VALIDATE,null, ServerConstant.ERROR), user);
               return;
            }
            ZPUserInfo memberInfo = (ZPUserInfo) ZPUserInfo.getModel(new_member.id, ZPUserInfo.class);
            if (memberInfo == null) {
               ////send response error
                logger.debug("khong ton tai user xin gia nhap");
                send(new ResponseAddMember(ServerConstant.VALIDATE,null, ServerConstant.ERROR), user);
               return;
            }
            
            Guild guild = (Guild) Guild.getModel(leader.id_guild, Guild.class);
            if (guild == null) {
                logger.debug("Khong ton tai guild, id guild = "+ user.getId());
                send(new ResponseAddMember(ServerConstant.VALIDATE,null, ServerConstant.ERROR), user);
                return;
            }
            //Upadte thong tin thanh vien trong guild
            guild.addMember(new_member.id, ServerConstant.guild_member); 
            
            //update thong tin guild trong member info
            memberInfo.addGuildInfo(guild.id, guild.name, guild.logo_id); 
            
            //gui goi tin them thanh vien toi moi nguoi trong bang
            ResponseAddRequestMember rs_addRequest = new  ResponseAddRequestMember(ServerConstant.TO_ALL, memberInfo, (short) 0);            
            for(Map.Entry<Integer, Short> member : guild.list_member.entrySet()) {
                Integer id_member = member.getKey();
                //User Member = BitZeroServer.getInstance().getUserManager().getUserById(id_member);
                User Member = ExtensionUtility.globalUserManager.getUserById(id_member);
                send (rs_addRequest, Member);
            }
            
            memberInfo.saveModel(new_member.id); 
            guild.saveModel(guild.id);
            send(new ResponseAddMember(ServerConstant.VALIDATE,null, ServerConstant.SUCCESS), user);
            
        } catch (Exception e) {
        }
        
    }

    private void processRemoveMember(User user, RequestRemoveMember member_remove) {
        logger.info("*************************processRemoveMember***************");        
        try {
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(user.getId(), ZPUserInfo.class);
            if (userInfo == null) {
               ////send response error
                logger.debug("khong ton tai user");
               send(new ResponseRemoveMember(ServerConstant.VALIDATE,null, ServerConstant.ERROR), user);
               return;
            }
            
            ZPUserInfo memberRemoveInfo = (ZPUserInfo) ZPUserInfo.getModel(member_remove.id, ZPUserInfo.class);
            if (memberRemoveInfo == null) {
               ////send response error
                logger.debug("khong ton tai user out bang");
               send(new ResponseRemoveMember(ServerConstant.VALIDATE,null, ServerConstant.ERROR), user);
               return;
            }
            
            Guild guild = (Guild) Guild.getModel(userInfo.id_guild, Guild.class);
            if (guild == null) {
                logger.debug("Khong ton tai guild, id guild = "+ user.getId());
                send(new ResponseRemoveMember(ServerConstant.VALIDATE,null, ServerConstant.ERROR), user); 
                return;
            }
            int id_leader = guild.getIdLeader();
            if (memberRemoveInfo.id!=userInfo.id && id_leader!=userInfo.id){
                logger.debug("Error! Nguoi ra quyet dinh loai bo khong phai bang chu, cung khong phai nguoi bi out");
                send(new ResponseRemoveMember(ServerConstant.VALIDATE,null, ServerConstant.ERROR), user); 
                return;
            }
            
            memberRemoveInfo.leftGuild();
            //thong bao cho toan the member
            ResponseRemoveMember rs_removeMember = new ResponseRemoveMember(ServerConstant.TO_ALL, memberRemoveInfo, (short) 0);
            for(Map.Entry<Integer, Short> member : guild.list_member.entrySet()) {
                Integer id_member = member.getKey();
                //User Member = BitZeroServer.getInstance().getUserManager().getUserById(id_member);
                User Member = ExtensionUtility.globalUserManager.getUserById(id_member);
                send (rs_removeMember, Member);
            }
            guild.removeMember(member_remove.id);
            
            memberRemoveInfo.saveModel(member_remove.id);
            guild.saveModel(guild.id);
            
            send(new ResponseRemoveMember(ServerConstant.VALIDATE,null, ServerConstant.SUCCESS), user); 
            
        } catch (Exception e) {
        }
    }

    private void processAddRequestMember(User user, RequestAddRequestMember guild_rq) {
        logger.info("*************************processAddRequestMember***************");  
        /*session ben phia nguoi yeu cau vao bang gui len server, goi tin RequestAddRequestMember chua id cua bang ma user muon xin vao
         * server thong bao cho bang chu
         * server cap nhat lai list require member trong Guild
         * */
        int id_member = user.getId();
        try {            
            ZPUserInfo memberInfo = (ZPUserInfo) ZPUserInfo.getModel(id_member, ZPUserInfo.class);
            if (memberInfo == null) {
               ////send response error
                logger.debug("khong ton tai user yeu cau vao bang");
                send(new ResponseAddRequestMember(ServerConstant.VALIDATE,null, ServerConstant.ERROR), user);
               return;
            }
            
            Guild guild = (Guild) Guild.getModel(guild_rq.id, Guild.class);
            if (guild == null) {
                logger.debug("Khong ton tai guild, id guild = "+ guild_rq.id);
                send(new ResponseAddRequestMember(ServerConstant.VALIDATE,null, ServerConstant.ERROR), user);
                return;
            }
            if (guild.checkListRequire(id_member)){
                logger.debug("Member dc them vao da nam trong list request");
                send(new ResponseAddRequestMember(ServerConstant.VALIDATE,null, ServerConstant.ERROR), user);
                return;
            }
            guild.addRequestMember(id_member, memberInfo.name);            
            //thong bao cho bang chu
            int id_leader = guild.getIdLeader();
            User leader = ExtensionUtility.globalUserManager.getUserById(id_leader);
            if (leader!=null){
                send(new ResponseAddRequestMember(ServerConstant.TO_ALL, memberInfo, (short) 0), leader); 
                return;
            }
            guild.saveModel(guild.id);
            
            send(new ResponseAddRequestMember(ServerConstant.VALIDATE,null, ServerConstant.SUCCESS), user);
            
        } catch (Exception e) {
        }
    }

    private void processDenyRequestMember(User user, RequestDenyRequestMember member_deny_rq) {        
        logger.info("*************************processDenyRequestMember***************");       
        /*session ben phia bang chu gui len server, 
         * goi tin processDenyRequestMember chua id cua nguoi ma bang chu khong muon cho vao bang         
         * server cap nhat lai list require member trong Guild
         * server thong bao phan hoi true/false cho bang chu
         * */
        try {
            ZPUserInfo memberInfo = (ZPUserInfo) ZPUserInfo.getModel(member_deny_rq.id, ZPUserInfo.class);
            if (memberInfo == null) {
               ////send response error
                logger.debug("khong ton tai user bi tu choi yeu cau vao bang");
               send(new ResponseDenyRequestMember(ServerConstant.ERROR), user);
               return;
            }
            ZPUserInfo userInfo = (ZPUserInfo) ZPUserInfo.getModel(member_deny_rq.id, ZPUserInfo.class);
            if (userInfo == null) {
               ////send response error
                logger.debug("khong ton tai user gui goi tin len");
               send(new ResponseDenyRequestMember(ServerConstant.ERROR), user);
               return;
            }
            
            Guild guild = (Guild) Guild.getModel(userInfo.id_guild, Guild.class);
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
            logger.info("Tu choi nguoi choi "+ memberInfo.name+ " vao bang co id = "+guild.id);
            
            guild.removeRequestMember(member_deny_rq.id);      
            
            
            send(new ResponseDenyRequestMember(ServerConstant.SUCCESS), user); 
            
        } catch (Exception e) {
        }
    }

    private void processSearchGuild(User user, RequestSearchGuild search_guild) {
        logger.info("*************************processSearchGuild***************");      
        /* xu ly luong tim kiem bang hoi
         * goi tin gui len bao gom < type: search theo id hoac theo ten, id, ten>
         * goi tin tra ve 1 list danh sach cac thong tin bang phu hop
         * */
        try {
            ListGuild listGuild = (ListGuild) ListGuild.getModel(1, ListGuild.class);
            if (listGuild ==null){ //neu listGuild chua duoc khoi tao
                send(new ResponseSearchGuild(ServerConstant.ERROR, null), user); 
                return;
            }
            Queue<Guild> queue;
            if (search_guild.type == ServerConstant.SEARCH_ID){
                Guild suggest_guild = (Guild) Guild.getModel(search_guild.id_search, Guild.class);
                queue  = new PriorityQueue<Guild>();
                queue.add(suggest_guild);
            }else {
                queue = listGuild.searchNameGuild(search_guild.string);    
            }
            
            send(new ResponseSearchGuild(ServerConstant.SUCCESS,queue), user);
            
        } catch (Exception e) {
        }
        
        
        
    }

    private void processGetGuildInfo(User user, RequestGetGuildInfo get_guild_info) {
        logger.info("*************************processGetGuildInfo***************");
        try {
            Guild guild = (Guild) Guild.getModel(get_guild_info.id, Guild.class);
            send(new ResponseGetGuildInfo(guild), user); 
            
        } catch (Exception e) {
            logger.debug("Khong ton tai guild, id = "+ get_guild_info.id);
        }
    }

    private void processEditGuildInfo(User user, RequestEditGuildInfo edit_guild_info) {
        logger.info("*************************processGetGuildInfo***************");
        try {
            Guild guild = (Guild) Guild.getModel(edit_guild_info.id, Guild.class);
            if (guild.getIdLeader()!= user.getId()){
                logger.debug("Nguoi yeu cau edit khong phai la leader, id user= "+guild.getIdLeader()+ "id _leader= "+ user.getId());
                send(new ResponseEditGuildInfo(ServerConstant.ERROR), user); 
                return;
            }
            
            guild.setName(edit_guild_info.name);
            guild.setLogo_id(edit_guild_info.logo_id);
            guild.setStatus(edit_guild_info.status);
            guild.setDanh_vong_require(edit_guild_info.require_danh_vong);
            guild.setDescription(edit_guild_info.description);
            
            guild.saveModel(guild.id);
            send(new ResponseEditGuildInfo(ServerConstant.SUCCESS), user); 
            
        } catch (Exception e) {
            logger.debug("Khong ton tai guild, id = "+ edit_guild_info.id);
            send(new ResponseEditGuildInfo(ServerConstant.ERROR), user); 
        }
    }

    private int checkResourceExchange(ZPUserInfo userInfo, int _gold) {
        int g = 0;
        if (userInfo.gold < _gold){
            g+=goldToG(_gold-userInfo.gold);                    
        };
        return g;
    }
    private int goldToG(int gold_bd) {
        return gold_bd;
    }

    private int elixirToG(int elixir_bd) {
        return elixir_bd;
    }

    private int darkElixirToG(int darkElixir_bd) {
        return darkElixir_bd;
    }
}


