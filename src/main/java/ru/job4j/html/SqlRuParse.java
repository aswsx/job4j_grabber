package ru.job4j.html;

import org.jsoup.Jsoup;

import java.io.IOException;

import static ru.exxo.jutil.Printer.println;

public class SqlRuParse {
    private static int index = 1;

    public static void main(String[] args) throws Exception {
        String url = "https://www.sql.ru/forum/job-offers/";
        for (int i = 1; i <= 5; i++) {
            parser(url + i, index++);
        }
    }

    private static void parser(String url, int index) throws IOException {
        var i = 1;
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
}