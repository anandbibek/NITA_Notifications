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
public class FetcherEvents {

    public ArrayList<LinkContainer> get(String url) throws IOException {

        ArrayList<LinkContainer> data = new ArrayList<>();
        Elements boxes = Jsoup.connect(url).timeout(20000).get().select("div[class=CollapsiblePanel]");
        for (Element box : boxes) {
            //data.add(new LinkContainer(box.select("div[tabindex=0]").text(), "NONE", true));
            Elements lines = box.select("div[class=CollapsiblePanelTab],div[class=CollapsiblePanelContent]");
            for (Element line : lines) {
                if(line.attr("tabindex")!=null && line.attr("tabindex").equals("0"))
                    data.add(new LinkContainer(line.text(), "NONE", true));
                else
                    //FIXME possible NPE here
                    data.add(new LinkContainer(line.text(), line.select("a[href]").first().attr("abs:href")));
            }
        }

        return data;
    }
}
