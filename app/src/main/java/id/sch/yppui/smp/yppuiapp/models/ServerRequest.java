package id.sch.yppui.smp.yppuiapp.models;

/**
 * Created by ipin on 4/8/2018.
 */

public class ServerRequest {

    private String operation;
    private User user;

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
