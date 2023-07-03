package org.nita.notifications.fetchers;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nita.notifications.LinkContainer;

/**
 * Created by Anand on 24-Aug-15.
 */

/* UNUSED */
public class FetcherAllNotice {

    public ArrayList<LinkContainer> get(String url) throws IOException {

        ArrayList<LinkContainer> data = new ArrayList<>();

        //add first Element, which is header
        data.add(new LinkContainer("All Notices", "", true));

        Elements tables = Jsoup.connect(url).timeout(20000).get().select("table.data-table");
        for (Element table : tables) {
            Elements lines = table.select("td");
            for (Element line : lines) {
                final String text = line.text();
                final Element firstLink = line.select("a[href]").first();

                if (firstLink != null) {
                    data.add(new LinkContainer(text, firstLink.attr("abs:href")));
                }
            }
        }

        return data;
    }
}
