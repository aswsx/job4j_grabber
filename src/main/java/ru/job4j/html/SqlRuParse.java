package ru.job4j.html;

import org.jsoup.Jsoup;

import java.io.IOException;

import static ru.exxo.jutil.Printer.println;

public class SqlRuParse {

    public static void main(String[] args) throws Exception {
        String url = "https://www.sql.ru/forum/job-offers/";
        String postURL = "https://www.sql.ru/forum/1325330/"
                + "lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t/";
        println("-------------------------------pageParser----------------------------------");
        for (var i = 1; i <= 5; i++) {
            pageParser(url + i, i);
        }
        postParser(postURL);
    }

    private static void pageParser(String url, int index) throws IOException {
        var doc = Jsoup.connect(url).get();
        var row = doc.select(".postslisttopic");
        println("******************** Страница " + index + " ********************");
        row.forEach(td -> {
            var href = td.child(0);
            println(href.attr("href"));
            println(href.text());
            var date = td.parent().child(5);
            println(date.text());
        });
    }

    private static void postParser(String url) throws IOException {
        var doc = Jsoup.connect(url).get();
        var message = doc.select(".msgBody").get(1);
        var footer = doc.select(".msgFooter");
        var messageText = message.text();
        var footerText = footer.first().ownText().replace(" [] |", "");
        println("===============================postParser===============================");
        println(messageText);
        println(footerText);
    }
}