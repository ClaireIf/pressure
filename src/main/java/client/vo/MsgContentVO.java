package client.vo;


import java.io.Serializable;

/**
 * Description: message body
 * Date: 2019-04-16 16:58
 * Author: Claire
 */

public class MsgContentVO implements Serializable {

    //Message content
    private String content;

    private String thumbnailUrl;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
