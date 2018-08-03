package cmd.receive.log;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestLog extends BaseCmd {
    public int actionId = 0;
    public int accountType = 0; //0 google, 1 facebook, 2 zingplay, 3 zing me
    public String deviceId = ""; //chuoi id may
    public String openAccount = ""; //tai khoan mail...
    public long zingId = 0;
    public String zingName = "";
    public long exp = 0;
    public long currentExp = 0;
    public long currentLevel = 0;
    public long quantity = 0;
    public String itemId = "";
    public String itemType = "";
    public String targetId = "";
    public String targetType = "";
    public int platform = 0; //0 Android, 1 iOs, 2 web, 3 zingme, 4 yahoo
    public int networkType = 0; //0 la chua biet, 1 la 3G, 2 la wifi
    public int signalStrength = 0; //tu 1-5
    public String location = "";
    public String deviceType = "";
    public String osVersion = "";

    public RequestLog(DataCmd dataCmd) {
        super(dataCmd);
    }

    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            actionId = readInt(bf);
            accountType = readInt(bf);
            deviceId = readString(bf);
            openAccount = readString(bf);
            zingId = readLong(bf);
            zingName = readString(bf);
            exp = readLong(bf);
            currentExp = readLong(bf);
            currentLevel = readLong(bf);
            quantity = readLong(bf);
            itemId = readString(bf);
            itemType = readString(bf);
            targetId = readString(bf);
            targetType = readString(bf);
            platform = readInt(bf);
            networkType = readInt(bf);
            signalStrength = readInt(bf);
            location = readString(bf);
            deviceType = readString(bf);
            osVersion = readString(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        Object[] logValues = new Object[] {
            actionId, accountType, deviceId, openAccount, zingId, zingName, exp, currentExp, currentLevel, quantity,
            itemId, itemType, targetId, targetType, platform, networkType, signalStrength, location, deviceType,
            osVersion
        };

        String formatLog = "";
        for (int i = 0; i < logValues.length - 1; i++) {
            formatLog += "%s|";
        }
        formatLog += "%s";

        String logMessage = String.format(formatLog, logValues);
        return logMessage;
    }
}
