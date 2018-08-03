package eventhandler;

import bitzero.server.core.BZEvent;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseServerEventHandler;
import bitzero.server.extensions.ExtensionLogLevel;

import bitzero.util.ExtensionUtility;
import bitzero.util.socialcontroller.bean.UserInfo;

import java.util.HashMap;
import java.util.Map;

import util.Constant;

public class LoginSuccessHandler extends BaseServerEventHandler {
    public LoginSuccessHandler() {
        super();
    }

    public void handleServerEvent(IBZEvent iBZEvent) {
        this.onLoginSuccess((User) iBZEvent.getParameter(BZEventParam.USER));
    }

    private void onLoginSuccess(User user) {
        trace(ExtensionLogLevel.DEBUG, "On Login Success ", user.getName());
        UserInfo uInfo = null;

        uInfo = (UserInfo) user.getProperty(Constant.USER_INFO);

        ExtensionUtility.instance().sendLoginOK(user);
        
        /**
         * dispatch event here
         */
        Map evtParams = new HashMap();
        evtParams.put(BZEventParam.USER, user);
        ExtensionUtility.dispatchEvent(new BZEvent(BZEventType.PRIVATE_MESSAGE, evtParams));

    }

}
