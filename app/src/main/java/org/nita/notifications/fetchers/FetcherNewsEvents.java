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
public class FetcherNewsEvents {

    public ArrayList<LinkContainer> get(String url) throws IOException {

        ArrayList<LinkContainer> data = new ArrayList<>();
        data.add(new LinkContainer(getHeader(), "", true));
        Elements tables = Jsoup.connect(url).timeout(20000)
            .get().select("table.table.table-bordered.data-table");
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

    protected String getHeader() {
        return "Latest News & Events";
    }
}
