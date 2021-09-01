package ru.job4j.html;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    private static final Logger LOG = LoggerFactory.getLogger(SqlRuParse.class.getName());
    private final SqlRuDateTimeParser dateTimeParser;

    public SqlRuParse(SqlRuDateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> postList = new ArrayList<>();
        var doc = Jsoup.connect(link).get();
        var row = doc.select(".postslisttopic");
        row.forEach(td -> {
            var href = td.child(0);
            try {
                postList.add(detail(href.attr("href")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return postList;
    }

    @Override
    public Post detail(String link) throws IOException {
        var doc = Jsoup.connect(link).get();
        var postHeader = doc.selectFirst(".messageHeader");
        var postName = postHeader.text().replace("[new]", "").trim();
        var postText = doc.select(".msgBody").get(1).text();
        var dateFromFooter = doc
                .select(".msgFooter")
                .first()
                .ownText()
                .replace(" [] |", "");
        var parsedDate = dateTimeParser.parse(dateFromFooter);
        return new Post(postName, link, postText, parsedDate);
    }

    public static void main(String[] args) throws IOException {

        var sqlDTParser = new SqlRuDateTimeParser();
        var sqlParser = new SqlRuParse(sqlDTParser);
        String link = "https://www.sql.ru/forum/job-offers";
        var list = sqlParser.list(link);
        var thatPost = list.get(10);
        LOG.info(String.format("Количество постов в листе %d", list.size()));
        LOG.info(String.format("Название поста %s", thatPost.getTitle()));
        LOG.info(String.format("Текст поста %s", thatPost.getDescription()));
        LOG.info(String.format("Дата создания поста %s", thatPost.getDate().toString()));
    }
}
