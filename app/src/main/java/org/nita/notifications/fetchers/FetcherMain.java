package org.nita.notifications.fetchers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nita.notifications.LinkContainer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Anand on 24-Aug-15.
 */
public class FetcherMain {

    public ArrayList<LinkContainer> get(String url) throws IOException {

        ArrayList<LinkContainer> data = new ArrayList<>();

        //add first Element, which is header
        data.add(new LinkContainer("Latest News", "", true));
        Element content = Jsoup.connect(url).timeout(20000).get().getElementById("vmarquee");
        //Elements links = content.select("a[href]");
        Elements links = content.select("p");
        //Log.d("DDDD",content.outerHtml()+"K");
        for (Element link : links) {
            //data.add(new LinkContainer(link.text(), link.attr("abs:href")));
            String dat = link.select("a[href]").attr("abs:href");
            if(dat!=null && !dat.equals(""))
                data.add(new LinkContainer(link.text(), link.select("a[href]").attr("abs:href")));
        }

        return data;
    }
}
