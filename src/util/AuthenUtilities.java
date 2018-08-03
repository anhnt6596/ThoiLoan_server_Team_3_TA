package util;

import bitzero.util.common.business.Debug;

import cmd.ErrorConst;

import cmd.obj.authen.ObjectSessionKey;
import cmd.obj.authen.SessionKeyInfo;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;

public class AuthenUtilities {

    public static final long APP_ID = 100;
    public static final String SECRET_KEY = "zppgsn";

    public AuthenUtilities() {
        super();
    }

    public static String generateSessionKey(SessionKeyInfo ssKey) {
        String info =
            "id=" + ssKey.id + "&username=" + ssKey.username + "&social=" + ssKey.social + "&socialname=" +
            ssKey.socialacc + "&avatar=" + ssKey.avatar + "&time=" + Common.currentTimeInSecond();
        String gameKey = Common.encryptMD5(AuthenUtilities.APP_ID + AuthenUtilities.SECRET_KEY);
        String token = Common.encryptMD5(info + gameKey);
        return new String(Base64.encodeBase64((info + "&tokenKey=" + token).getBytes()));
    }

    public static ObjectSessionKey decodeSessionKey(String sessionKey) {

        try {
            //Debug.info("__begin decode");
            if (!AuthenUtilities.isValidSessionKey(sessionKey))
                return new ObjectSessionKey(ErrorConst.SESSIONKEY_INVALID, 0, "", "", "", "", 0);
            //Debug.info("__decode valid");
            sessionKey = new String(Base64.decodeBase64(sessionKey));
            String[] arr = sessionKey.split("&");
            for (int i = 0; i < arr.length; i++) {
                arr[i] = arr[i].split("=")[1];
            }

            return new ObjectSessionKey(ErrorConst.SUCCESS, Long.valueOf(arr[0]), arr[1], arr[2], arr[3], arr[4],
                                        Long.parseLong(arr[5]));
        } catch (Exception e) {
            Debug.warn("EXCEPTION");
            Debug.warn(e.getMessage());
            Debug.warn(ExceptionUtils.getStackTrace(e));
            return new ObjectSessionKey(ErrorConst.SESSIONKEY_INVALID, 0L, "", "", "", "", 0);
        }

    }


    public static boolean isValidSessionKey(String sessionKey) {
        //Debug.info("__decode isValid");
        try {
            String ssKey = new String(Base64.decodeBase64(sessionKey));
            //System.out.println(ssKey);
            String[] arr = ssKey.split("&");
            for (int i = 0; i < arr.length; i++) {
                arr[i] = arr[i].split("=")[1];
            }
            String info =
                "id=" + arr[0] + "&username=" + arr[1] + "&social=" + arr[2] + "&socialname=" + arr[3] + "&avatar=" +
                arr[4] + "&time=" + arr[5];
            String gameKey = Common.encryptMD5(AuthenUtilities.APP_ID + AuthenUtilities.SECRET_KEY);
            return (Common.encryptMD5(info + gameKey).equals(arr[6]));
        } catch (Exception e) {
            Debug.warn("EXCEPTION");
            Debug.warn("sessionKey = " + sessionKey);
            Debug.warn(ExceptionUtils.getStackTrace(e));
            return false;
        }

    }
}
