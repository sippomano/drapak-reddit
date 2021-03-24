package com.sipp.dao.sql;

import com.sipp.dao.CommentDao;
import com.sipp.model.Comment;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class CommentDaoSql implements CommentDao {

    private static CommentDaoSql instance = new CommentDaoSql();
    private static DataSource ds = DataSourceSupplier.get();

    private CommentDaoSql() {
    }

    private static final String ADD_COMMENTS = "INSERT INTO comments (author, text, permalink, post_permalink, awards_count, score, creation_time, parent_permalink) VALUES(?, ?, ?, ?, ?, ?, ?, ?)" +
            " ON CONFLICT (permalink) DO UPDATE SET text=?, score=?, awards_count=? WHERE comments.permalink=?";
    private static final String SELECT_COMMENT = "SELECT * FROM comments WHERE permalink=?";
    private static final String SELECT_COMMENTS_ALL = "SELECT * FROM comments";
    private static final String SELECT_COMMENTS_ALL_SINCE = "SELECT * FROM comments WHERE creation_time>?";
    private static final String SELECT_COMMENTS_ALL_SINCE_UNTIL = "SELECT * FROM comments WHERE creation_time>? AND creation_time<?";
    private static final String SELECT_COMMENTS_FOR_POST = "SELECT * FROM comments WHERE post_permalink=?";

    @Override
    public void addComments(List<Comment> comments) {
        try (PreparedStatement ps = ds.getConnection().prepareStatement(ADD_COMMENTS)){
            log.info("comments list length: " + comments.size());
            for (Comment comment : comments) {
                ps.setString(1, comment.getAuthor());
                ps.setString(2, comment.getText());
                ps.setString(3, comment.getPermalink());
                ps.setString(4, comment.getPostPermalink());
                ps.setInt(5, comment.getAwardsCount());
                ps.setInt(6, comment.getScore());
                ps.setLong(7, comment.getCreationTime());
                ps.setString(8, comment.getParentPermalink());
                ps.setString(9, comment.getText());
                ps.setInt(10, comment.getScore());
                ps.setInt(11, comment.getAwardsCount());
                ps.setString(12, comment.getPermalink());

                int rowsUpdated = ps.executeUpdate();
                log.info("Adding comment object to database. Updated rows: " + rowsUpdated);
            }
        } catch (SQLException e) {
            log.error("create/update operation failed: " + e.getMessage());
        }
    }

    @Override
    public Optional<Comment> getComment(String permalink) {
        Comment comment = null;
        try (PreparedStatement ps = ds.getConnection().prepareStatement(SELECT_COMMENT)) {
            ps.setString(1, permalink);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    comment = createCommentFromResultSet(rs);
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
        return Optional.ofNullable(comment);
    }

    @Override
    public List<Comment> getComments() {
        List<Comment> comments = new ArrayList<>();
        try (ResultSet rs = ds.getConnection().prepareStatement(SELECT_COMMENTS_ALL).executeQuery()) {
            while (rs.next()) {
                comments.add(createCommentFromResultSet(rs));
            }
            log.info("Number of comments in list: " + comments.size());
        } catch (SQLException e) {
            log.error("read operation failed: " + e.getMessage());
            e.printStackTrace();
        }
        return comments;
    }

    @Override
    public List<Comment> getComments(long since) {
        List<Comment> comments = new ArrayList<>();
        try (PreparedStatement ps = ds.getConnection().prepareStatement(SELECT_COMMENTS_ALL_SINCE)) {
            ps.setLong(1, since);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(createCommentFromResultSet(rs));
                }
            }
            log.info("Number of comments in list: " + comments.size());
        } catch (SQLException e) {
            log.error("read operation failed: " + e.getMessage());
            e.printStackTrace();
        }
        return comments;
    }

    public List<Comment> getComments(ZonedDateTime since) {
        long sinceEpoch = since.toEpochSecond();
        return getComments(sinceEpoch);
    }

    @Override
    public List<Comment> getComments(long since, long until) {
        List<Comment> comments = new ArrayList<>();
        try (PreparedStatement ps = ds.getConnection().prepareStatement(SELECT_COMMENTS_ALL_SINCE_UNTIL)) {
            ps.setLong(1, since);
            ps.setLong(2, until);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(createCommentFromResultSet(rs));
                }
            }
            log.info("Number of comments in list: " + comments.size());
        } catch (SQLException e) {
            log.error("read operation failed: " + e.getMessage());
            e.printStackTrace();
        }
        return comments;
    }

    public List<Comment> getComments(ZonedDateTime since, ZonedDateTime until) {
        long sinceEpoch = since.toEpochSecond();
        long untilEpoch = until.toEpochSecond();
        return getComments(sinceEpoch, untilEpoch);
    }

    public List<Comment> getCommentsForPost(String postPermalink) {
        List<Comment> comments = new ArrayList<>();
        try (PreparedStatement ps = ds.getConnection().prepareStatement(SELECT_COMMENTS_FOR_POST)) {
            ps.setString(1, postPermalink);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(createCommentFromResultSet(rs));
                }
            }
            log.info("Number of comments in list: " + comments.size());
        } catch (SQLException e) {
            log.error("read operation failed: " + e.getMessage());
            e.printStackTrace();
        }
        return comments;
    }

    private Comment createCommentFromResultSet(ResultSet rs) throws SQLException{
        Comment comment = new Comment();

        comment.setAuthor(rs.getString("author"));
        comment.setText(rs.getString("text"));
        comment.setPermalink(rs.getString("permalink"));
        comment.setSubreddit(rs.getString("permalink").replaceAll("/r/|/comments/.*", ""));
        comment.setPostPermalink(rs.getString("post_permalink"));
        comment.setAwardsCount(rs.getInt("awards_count"));
        comment.setScore(rs.getInt("score"));
        comment.setCreationTime(rs.getInt("creation_time"));
        comment.setParentPermalink(rs.getString("parent_permalink"));

        log.info("Comment created: " + comment.toString());
        return comment;
    }

    public static CommentDaoSql getInstance() {
        return instance;
    }

}
