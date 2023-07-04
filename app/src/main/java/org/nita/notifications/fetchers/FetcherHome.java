package org.nita.notifications.fetchers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nita.notifications.LinkContainer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Anand on 24-Aug-15.
 */
public class FetcherHome {

    public ArrayList<LinkContainer> get(String url) throws IOException {

        ArrayList<LinkContainer> data = new ArrayList<>();
        Document document = Jsoup.connect(url).timeout(20000).get();

        //add first Element, which is header
        data.add(new LinkContainer("Notice Board", "", true));
        Elements content = document.getElementsByClass("notice_board_overflow");
        for (Element link : content) {
            String dat = link.select("a[href]").attr("abs:href");
            if(dat!=null && !dat.equals(""))
                data.add(new LinkContainer(link.text(), dat));
        }

        data.add(new LinkContainer("Latest Events", "", true));
        Elements newsContent = document.getElementsByClass("news_card").select("div.details");
        for (Element link : newsContent) {
            String newsUrl = link.selectFirst("a[href]").attr("abs:href");
            if(newsUrl!=null && !newsUrl.equals(""))
                data.add(new LinkContainer(link.text(), newsUrl));
        }


        return data;
    }
}
