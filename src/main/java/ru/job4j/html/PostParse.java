package ru.job4j.html;

import org.jsoup.Jsoup;

import java.io.IOException;

import static ru.exxo.jutil.Printer.println;

public class PostParse {

    public static void main(String[] args) throws Exception {
        String url = "https://www.sql.ru/forum/1325330/"
                + "lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t/";
        parser(url);
    }

    private static void parser(String url) throws IOException {
        var doc = Jsoup.connect(url).get();
        var element = doc.select("msgBody");
          // var href = element.child(0);
            //println(href.attr("href"));
            println(element.text());
          //  var date = td.parent().child(5);
         //   println(date.text());
            }
}
