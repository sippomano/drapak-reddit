package com.sipp.dao.sql;

import com.sipp.dao.PostDao;
import com.sipp.model.Post;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class PostDaoSql implements PostDao {

    private static DataSource ds = DataSourceSupplier.get();

    private static final String ADD_POSTS = "INSERT INTO posts (title, text, score, permalink, flair, comments_count, awards_count, author, creation_time) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)" +
            " ON CONFLICT (permalink) DO UPDATE SET text=?, score=?, comments_count=?, awards_count=? WHERE posts.permalink=?";
    public static final String SELECT_POST = "SELECT * FROM posts WHERE permalink=?";

    @Override
    public void addPosts(List<Post> posts) {
        try (PreparedStatement ps = ds.getConnection().prepareStatement(ADD_POSTS)){
            log.info("posts list length: " + posts.size());
            for (Post post : posts) {
                ps.setString(1, post.getTitle());
                ps.setString(2, post.getText());
                ps.setInt(3, post.getScore());
                ps.setString(4, post.getPermalink());
                ps.setString(5, post.getFlair());
                ps.setInt(6, post.getCommentsCount());
                ps.setInt(7, post.getAwardsCount());
                ps.setString(8, post.getAuthor());
                ps.setLong(9, post.getCreationTime());
                ps.setString(10, post.getText());
                ps.setInt(11, post.getScore());
                ps.setInt(12, post.getCommentsCount());
                ps.setInt(13, post.getAwardsCount());
                ps.setString(14, post.getPermalink());

                int rowsUpdated = ps.executeUpdate();
                log.info("Adding post object to database. Updated rows: " + rowsUpdated);
            }
        } catch (SQLException e) {
            log.error("create/update operation failed: " + e.getMessage());
        }
    }

    @Override
    public Optional<Post> getPost(String permalink) {
        Post post = null;
        try (PreparedStatement ps = ds.getConnection().prepareStatement(SELECT_POST)) {
            ps.setString(1, permalink);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    post = createPostsFromResultSet(rs);
                    count++;
                }
                if (count > 1) {
                    log.error("Incorrect number of rows returned: " + count);
                    throw new SQLException("Incorrect number of rows returned. Should return one, returned: " + rs.getRow());
                }
            }
        } catch (SQLException e) {
            log.error("read operation failed: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.ofNullable(post);
    }

    @Override
    public List<Post> getPosts() {
        return null;
    }

    @Override
    public List<Post> getPosts(long since) {
        return null;
    }

    @Override
    public List<Post> getPosts(long since, long until) {
        return null;
    }

    private Post createPostsFromResultSet(ResultSet rs) throws SQLException{
        Post post = new Post();
        post.setAuthor(rs.getString("author"));
        post.setText(rs.getString("text"));
        post.setSubreddit(rs.getString("permalink").replaceAll("/r/|/comments/.*", ""));
        post.setPermalink(rs.getString("permalink"));
        post.setTitle(rs.getString("title"));
        post.setFlair(rs.getString("flair"));
        post.setCommentsCount(rs.getInt("comments_count"));
        post.setAwardsCount(rs.getInt("awards_count"));
        post.setScore(rs.getInt("score"));
        post.setCreationTime(rs.getLong("creation_time"));
        //TBD post.setComments()
        log.info("Post created: " + post.toString());
        return post;
    }
}
