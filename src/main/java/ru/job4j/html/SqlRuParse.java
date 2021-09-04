package ru.job4j.html;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.grabber.Parse;
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
        var post = new Post();
        var doc = Jsoup.connect(link).get();
        var postHeader = doc.selectFirst(".messageHeader");
        var postTitle = postHeader.text().replace("[new]", "").trim();
        var postDescription = doc.select(".msgBody").get(1).text();
        var dateFromFooter = doc
                .select(".msgFooter")
                .first()
                .ownText()
                .replace(" [] |", "");
        var parsedDate = dateTimeParser.parse(dateFromFooter);
        post.setLink(link);
        post.setName(postTitle);
        post.setText(postDescription);
        post.setCreated(parsedDate);
        return post;
    }

    public static void main(String[] args) throws IOException {

        var sqlDTParser = new SqlRuDateTimeParser();
        var sqlParser = new SqlRuParse(sqlDTParser);
        String link = "https://www.sql.ru/forum/job-offers";
        var list = sqlParser.list(link);
        var thatPost = list.get(10);
        LOG.info(String.format("Количество постов в листе - %d", list.size()));
        LOG.info(String.format("Название поста - %s", thatPost.getName()));
        LOG.info(String.format("Текст поста - %s", thatPost.getText()));
        LOG.info(String.format("Дата создания поста - %s", thatPost.getCreated().toString()));
    }
}
