package model;

import java.io.Serializable;

/**
 * Created by qzj_ on 2016/12/10.
 */
public class Like implements Serializable {
    private Boolean isCurrentUser;
    private String userName;
    private String userId;

    public Boolean getIsCurrentUser() {
        return isCurrentUser;
    }

    public void setIsCurrentUser(Boolean currentUser) {
        isCurrentUser = currentUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
