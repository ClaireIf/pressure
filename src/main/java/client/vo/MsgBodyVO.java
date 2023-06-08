package client.vo;

import java.io.Serializable;

/**
 * Description: message body
 * Date: 2019-04-17 11:03
 * Author: Claire
 */
public class MsgBodyVO implements Serializable {

    //private key
    private String prk ;


    //message body
    private String data ;

    public String getPrk() {
        return prk;
    }

    public void setPrk(String prk) {
        this.prk = prk;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
