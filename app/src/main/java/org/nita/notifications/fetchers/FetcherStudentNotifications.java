package org.nita.notifications.fetchers;

import android.text.TextUtils;
import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nita.notifications.LinkContainer;

/**
 * Created by Anand on 24-Aug-15.
 */
public class FetcherStudentNotifications {

    public ArrayList<LinkContainer> get(String url) throws IOException {

        ArrayList<LinkContainer> data = new ArrayList<>();

        Elements accordionItems = Jsoup.connect(url).timeout(20000)
            .get().select("div.accordion-item.dataTable");
        for (Element accordionItem : accordionItems) {
            String title = accordionItem.selectFirst("span.title").text();
            data.add(new LinkContainer(title, "", true));

            Elements tables = accordionItem.select("table.table.table-bordered.data-table");
            for (Element table : tables) {
                Elements lines = table.select("td");
                for(Element line : lines) {
                    String dat = line.select("a[href]").attr("abs:href");
                    if (dat != null && !dat.equals("")) {
                        String desc = TextUtils.join(" - ", line.siblingElements().eachText());
                        data.add(new LinkContainer(desc , dat));
                    }
                }
            }
        }

        return data;
    }
}
