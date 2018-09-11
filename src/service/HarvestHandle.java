package service;
 import bitzero.server.core.BZEventType;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseClientRequestHandler;
 import bitzero.server.extensions.data.DataCmd;
 import cmd.CmdDefine;
 import cmd.receive.harvest.RequestDoHarvest;
import cmd.receive.train.RequestTrainTroop;
 import cmd.receive.troop.RequestResearch;
 import cmd.send.harvest.ResponseDoHarvest;
import cmd.send.demo.ResponseRequestQuickFinish;
 import cmd.send.demo.ResponseResearch;
 import model.MapInfo;
import model.ZPUserInfo;
 import model.train.BarrackQueueInfo;
 import org.apache.commons.lang.exception.ExceptionUtils;
 import org.json.JSONException;
import org.json.JSONObject;
 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 import util.server.ServerConstant;
 public class HarvestHandle extends BaseClientRequestHandler {
    public static short HARVEST_MULTI_IDS = 5000;
    private final Logger logger = LoggerFactory.getLogger("HarvestHandle");
    public HarvestHandle() {
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
                case CmdDefine.DO_HARVEST:
                    RequestDoHarvest do_harvest = new RequestDoHarvest(dataCmd);
                    processDoHarvest(user, do_harvest);
                    break;
                
            }
        } catch (Exception e) {
            logger.warn("HARVEST HANDLER EXCEPTION " + e.getMessage());
            logger.warn(ExceptionUtils.getStackTrace(e));
        }
    }
     private void processDoHarvest(User user, RequestDoHarvest do_harvest) {
        logger.debug("Thu hoach mo "+do_harvest.id);
        
        try {
            MapInfo mapInfo = (MapInfo) MapInfo.getModel(user.getId(), MapInfo.class);
            if (mapInfo == null) {
                //send response error
                logger.info("Khong ton tai mapInfo");
                send(new ResponseDoHarvest(ServerConstant.ERROR, "", 0), user);
                return;
            }
            
            
        } catch (Exception e) {
            logger.info("Khong thu hoach duoc");
            send(new ResponseDoHarvest(ServerConstant.ERROR, "", 0), user);
        }
         
    }
}