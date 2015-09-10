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
public class FetcherAcademic {

    public ArrayList<LinkContainer> get(String url) throws IOException {

        ArrayList<LinkContainer> data = new ArrayList<>();
        Elements stories = Jsoup.connect(url).timeout(20000).get().select("div[id=alumni-stories]");
        for (Element story : stories) {
            data.add(new LinkContainer(story.select("h3,h4").html(),"NONE", true));
            Elements links = story.select("a[href]");
            for (Element link : links) {
                data.add(new LinkContainer(link.text(), link.attr("abs:href")));
            }
        }

        return data;
    }
}
