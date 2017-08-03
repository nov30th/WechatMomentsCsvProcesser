package model;

import java.io.Serializable;

/**
 * Created by qzj_ on 2016/12/10.
 */
public class Comment implements Serializable {
    private String content;
    private Boolean isCurrentUser;
    private String authorId;
    private String authorName;
    private String toUserId;
    private String toUserName;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsCurrentUser() {
        return isCurrentUser;
    }

    public void setIsCurrentUser(Boolean currentUser) {
        isCurrentUser = currentUser;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }
}
