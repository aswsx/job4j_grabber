package ru.job4j.grabber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.html.Post;
import ru.job4j.html.SqlRuParse;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Класс PsqlStore
 *
 * @author Alex Gutorov
 * @version 1.9
 */
public class PsqlStore implements Store, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());
    private Connection cnn;
  //  private final SqlRuDateTimeParser dateTimeParser;

    /**
     * Метод создает соединение с базой данных
     *
     * @param cfg файл, содержащий параметры для подключения к базе
     * @throws SQLException Исключение, получаемое при некорректных параметрах
     */
    public PsqlStore(Properties cfg) throws SQLException {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("jdbc.url"),
                    cfg.getProperty("jdbc.username"),
                    cfg.getProperty("jdbc.password")
            );
        } catch (IllegalArgumentException | ClassNotFoundException e) {
            LOG.error("Connection failed...", e);
        }
      //  dateTimeParser = null;

    }

    /**
     * Метод сохраняет выбранный пост в базу данных
     *
     * @param post принимаемый пост
     */
    @Override
    public void save(Post post) {
        try (var ps = cnn.prepareStatement(
                "INSERT INTO post(name, link, text, created) VALUES(?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getLink());
            ps.setString(3, post.getText());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            ps.execute();
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    post.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException se) {
            LOG.error("Post save failed", se);
        }
    }

    /**
     * Метод показывает все посты из базы данных
     *
     * @return возвращаемый лист постов
     */
    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList<>();
        try (var ps = cnn.prepareStatement("SELECT * FROM post")) {
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    rsl.add(postFactory(new Post(), rs));
                }
            }
        } catch (SQLException se) {
            LOG.error("getAll fail", se);
        }
        return rsl;
    }

    /**
     * Метод показывает все объявления из базы данных
     *
     * @param id принимаемый на входе id поста
     * @return возвращаемый найденный пост
     */
    @Override
    public Post findById(int id) {
        var post = new Post();
        try (var ps = cnn.prepareStatement("SELECT * FROM post WHERE id=?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    postFactory(post, rs);
                }
            }
        } catch (SQLException se) {
            LOG.error("findById fail", se);
        }
        return post;
    }

    private Post postFactory(Post post, ResultSet rs) throws SQLException {
        post.setName(rs.getString("name"));
        post.setText(rs.getString("text"));
        post.setLink(rs.getString("link"));
//        post.setCreated(dateTimeParser.parse(rs
//                .getTimestamp("created")
//                .toString()));
        post.setCreated(rs.getTimestamp("created").toLocalDateTime());
        post.setId(rs.getInt("id"));
        return post;
    }

    /**
     * Метод показывает объявление из базы данных с указанным id
     */
    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    private static Properties getProperties(String path) {
        Properties properties = new Properties();
        try (var stream =
                     PsqlStore.class
                             .getClassLoader()
                             .getResourceAsStream(path)
        ) {
            properties.load(stream);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void main(String[] args) throws Exception {
        var sqlDTParser = new SqlRuDateTimeParser();
        var sqlRuParse = new SqlRuParse(sqlDTParser);
        try (PsqlStore store = new PsqlStore(getProperties("app.properties"))) {
            for (Post post : sqlRuParse.list("https://www.sql.ru/forum/job-offers")) {
                store.save(post);
            }
            String all = store.getAll().toString();
            String byId = store.findById(25).toString();
            LOG.info(all);
            LOG.info(byId);
        } catch (IOException e) {
            LOG.error("main failed", e);
        }
    }
}
