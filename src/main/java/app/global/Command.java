package app.global;

import java.util.HashMap;
import java.util.Map;

public class Command {
    String actionName;
    Map<String, String> paramMap;

    public Command(String cmd) {
        String[] cmdBits = cmd.split("\\?");
        actionName = cmdBits[0];
        paramMap = new HashMap<>();

        if(cmdBits.length < 2) {
            return;
        }
        String queryString = cmdBits[1];

        String[] params = queryString.split("&");

        for(String param : params) {
            String[] paramBits = param.split("=",2);

            if (paramBits.length < 2) {
                continue;
            }
            paramMap.put(paramBits[0], paramBits[1]);
        }

    }

    public String getActionName() {
        return actionName;
    }

    public String getParam(String key) {
        return paramMap.get(key);
    }

    public int getParamAsInt(String key) {

        try {
            String param = paramMap.get(key);
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
