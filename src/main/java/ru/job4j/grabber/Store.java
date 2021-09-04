package ru.job4j.grabber;

import ru.job4j.html.Post;

import java.sql.SQLException;
import java.util.List;

public interface Store {
    void save(Post post) throws SQLException;

    List<Post> getAll();

    Post findById(int id);
}