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
public class FetcherEvents {

    public ArrayList<LinkContainer> get(String url) throws IOException {

        ArrayList<LinkContainer> data = new ArrayList<>();
        data.add(new LinkContainer("Events", "", true));
        Elements boxes = Jsoup.connect(url).timeout(20000).get().select("div[class=CollapsiblePanel]");
        for (Element box : boxes) {
            //data.add(new LinkContainer(box.select("div[tabindex=0]").text(), "NONE", true));
            Elements lines = box.select("div[class=CollapsiblePanelTab],div[class=CollapsiblePanelContent]");
            for (Element line : lines) {
                final String text = line.text();
                final Element firstLink = line.select("a[href]").first();

                if ((line.attr("tabindex") != null && line.attr("tabindex").equals("0"))
                        || firstLink == null)
                    data.add(new LinkContainer(text, "", true));
                else {
                    data.add(new LinkContainer(text, firstLink.attr("abs:href")));
                }
            }
        }

        return data;
    }
}
