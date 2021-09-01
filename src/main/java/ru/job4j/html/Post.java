package ru.job4j.html;

import java.time.LocalDateTime;
import java.util.Objects;

public class Post {
    private int id;
    private final String title;
    private final String link;
    private final String description;
    private final LocalDateTime date;

    public Post(String title, String link, String description, LocalDateTime date) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.date = date;
    }

    public Post(int id, String title, String link, String description, LocalDateTime date) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }
        Post post = (Post) o;
        return id == post.id && Objects.equals(title, post.title) && Objects
                .equals(link, post.link) && Objects.equals(description, post.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, link, description);
    }

    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", link='" + link + '\''
                + ", description='" + description + '\''
                + ", date=" + date
                + '}';
    }
}
