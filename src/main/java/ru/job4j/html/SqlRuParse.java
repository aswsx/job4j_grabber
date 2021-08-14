package ru.job4j.html;

import org.jsoup.Jsoup;

import static ru.exxo.jutil.Printer.println;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        var doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        var row = doc.select(".postslisttopic");
        row.forEach(td -> {
            var href = td.child(0);
            println(href.attr("href"));
            println(href.text());
            var date = td.parent().child(5);
            println(date.text());
        });
    }
}