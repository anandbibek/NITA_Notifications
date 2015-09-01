package anandbibek.com.nitanotifications;

/**
 * Created by Anand on 24-Aug-15.
 */
public class LinkContainer {
    public String url, txt;

    LinkContainer(){
        url = "";
        txt = "";
    }

    LinkContainer(String urlText, String urlAddress){
        txt = urlText;
        url = urlAddress;
    }
}
