package app.global;

public class Command {
    String actionName;
    public Command(String cmd) {
        String[] cmdBits = cmd.split("\\?");
        actionName = cmdBits[0];
    }

    public String getActionName() {
        return actionName;
    }

    public int getParam() {
        return 1;
    }
}
