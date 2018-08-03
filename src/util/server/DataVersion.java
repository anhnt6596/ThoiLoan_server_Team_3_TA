package util.server;

import bitzero.util.common.business.Debug;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;

import util.database.DataHandler;

public class DataVersion {
    public DataVersion() {
        super();
    }

    public static void updateDataVersion(int version) {
        try {
            int currentVersion = 0;
            Object verobj = DataHandler.get("PortalDataVersion");
            if (verobj != null)
                currentVersion = Integer.valueOf(verobj.toString());

            if (currentVersion < version) {
                switch (version) {
                case 1:
                    updateVersion_1();
                    break;
                }

                DataHandler.set("PortalDataVersion", version);
            }


        } catch (Exception e) {
            Debug.warn("UPDATE DATA VERSION EXCEPTION " + e.getMessage());
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private static void updateVersion_1() {
        Gson gson = new Gson();
        HashMap<String, String> mapId;
        String SAVE_KEY = ServerConstant.GAME_DATA_KEY_PREFIX + ServerConstant.SEPERATOR + "MapSocialId";
        try {
            Object mapObj = DataHandler.get(SAVE_KEY);

            if (mapObj == null)
                return;
            mapId = gson.fromJson(mapObj.toString(), HashMap.class);
            System.out.println(mapId.size());
            int count = 0;
            for (Map.Entry<String, String> entry : mapId.entrySet()) {
                DataHandler.set(entry.getKey(), entry.getValue());
                count++;
            }
            Debug.warn("UPDATE DATE VERSION 1 SUCCESS");
            Debug.warn("numObject updated: " + count);
        } catch (Exception e) {
            Debug.warn("UPDATE DATA VERSION EXCEPTION 1 " + e.getMessage());
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }

    }
}
