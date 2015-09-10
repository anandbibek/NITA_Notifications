package org.nita.notifications.fetchers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import org.nita.notifications.LinkContainer;

/**
 * Created by Anand on 24-Aug-15.
 */
public class FetcherMain {

    public ArrayList<LinkContainer> get(String url, String div, String markers) throws IOException {

        ArrayList<LinkContainer> data = new ArrayList<>();
        Elements links = Jsoup.connect(url).timeout(20000).get().getElementById(div).select(markers);
        for (Element link : links) {
            data.add(new LinkContainer(link.text(), link.attr("abs:href")));
        }

        return data;
    }
}
