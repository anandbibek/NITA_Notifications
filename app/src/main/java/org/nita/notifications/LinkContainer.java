package org.nita.notifications;

import java.io.Serializable;

/**
 * Created by Anand on 24-Aug-15.
 */
public class LinkContainer implements Serializable{
    public String url, txt;
    boolean isHeader = false;

    public LinkContainer(){
        url = "";
        txt = "";
    }

    public LinkContainer(String urlText, String urlAddress){
        txt = urlText;
        url = urlAddress;
    }

    public LinkContainer(String urlText, String urlAddress, boolean header){
        txt = urlText;
        url = urlAddress;
        isHeader = header;
    }
}
