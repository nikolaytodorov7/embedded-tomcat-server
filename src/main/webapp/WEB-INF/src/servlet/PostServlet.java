package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mapper.PostMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Post;
import model.StatusMessage;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static util.ServletUtility.*;

public class PostServlet extends HttpServlet {
    private static final Pattern ALL_POSTS_PATTERN = Pattern.compile("/posts");
    private static final Pattern POST_WITH_ID_PATTERN = Pattern.compile("/posts/\\d+");
    private static final Pattern COMMENTS_OF_POST_WITH_ID = Pattern.compile("/posts/\\d+/comments");
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private PostMapper mapper;

    public void init() {
        try {
            Properties properties = Resources.getResourceAsProperties("jdbc.properties");
            InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in, properties);
            mapper = new PostMapper(factory);
        } catch (IOException e) {
            System.err.println("Properties error!\n" + e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println();
        String path = buildPath(req);
        if (ALL_POSTS_PATTERN.matcher(path).matches()) {
            mapper.getAllPosts().forEach((p) -> writeAsJson(resp, p));
            return;
        }

        String[] pathParts = path.substring(1).split("/");
        if (POST_WITH_ID_PATTERN.matcher(path).matches()) {
            int id = parseInt(pathParts[1]);
            if (id > 0)
                writeAsJson(resp, mapper.getPostById(id));
            else {
                StatusMessage msg = new StatusMessage(400, "Invalid id given in link.");
                writeAsJson(resp, msg);
            }

            return;
        }

        if (COMMENTS_OF_POST_WITH_ID.matcher(path).matches()) {
            int id = parseInt(pathParts[1]);
            if (id < 0) {
                StatusMessage msg = new StatusMessage(400, "Invalid id given in link.");
                writeAsJson(resp, msg);
                return;
            }

            RequestDispatcher dispatcher = req.getRequestDispatcher("/comments?postId=" + id);
            dispatcher.forward(req, resp);
            return;
        }

        StatusMessage msg = new StatusMessage(400, "Invalid link.");
        writeAsJson(resp, msg);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = buildPath(req);
        if (!ALL_POSTS_PATTERN.matcher(path).matches()) {
            StatusMessage msg = new StatusMessage(400, "Invalid link.");
            writeAsJson(resp, msg);
            return;
        }

        Collector<CharSequence, ?, String> joining = Collectors.joining(System.lineSeparator());
        String body = req.getReader().lines().collect(joining);
        Post post = gson.fromJson(body, Post.class);
        if (post == null) {
            StatusMessage msg = new StatusMessage(400, "Invalid post.");
            writeAsJson(resp, msg);
            return;
        }

        int insertedPosts = mapper.insertPost(post);
        if (insertedPosts == 1) {
            writeAsJson(resp, post);
            return;
        }

        StatusMessage msg = new StatusMessage(400, "Unable to insert post.");
        writeAsJson(resp, msg);
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = buildPath(req);
        if (!POST_WITH_ID_PATTERN.matcher(path).matches()) {
            StatusMessage msg = new StatusMessage(400, "Invalid link.");
            writeAsJson(resp, msg);
            return;
        }

        Collector<CharSequence, ?, String> joining = Collectors.joining(System.lineSeparator());
        String body = req.getReader().lines().collect(joining);
        Post post = gson.fromJson(body, Post.class);
        if (post == null) {
            StatusMessage msg = new StatusMessage(400, "Invalid post.");
            writeAsJson(resp, msg);
            return;
        }

        int updatedPosts = mapper.updatePost(post);
        if (updatedPosts == 1) {
            writeAsJson(resp, post);
            return;
        }

        StatusMessage msg = new StatusMessage(400, "Unable to update post with id=" + post.id);
        writeAsJson(resp, msg);
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = buildPath(req);
        if (!POST_WITH_ID_PATTERN.matcher(path).matches()) {
            StatusMessage msg = new StatusMessage(400, "Invalid link.");
            writeAsJson(resp, msg);
            return;
        }

        String[] pathParts = path.substring(1).split("/");
        if (pathParts.length != 2) {
            StatusMessage msg = new StatusMessage(400, "Invalid link.");
            writeAsJson(resp, msg);
            return;
        }

        int id = parseInt(pathParts[0]);
        if (id > 0) {
            Post post = mapper.getPostById(id);
            if (post == null) {
                StatusMessage msg = new StatusMessage(400, "Invalid post with id=" + post.id);
                writeAsJson(resp, msg);
                return;
            }

            int deletedPosts = mapper.deletePost(id);
            if (deletedPosts != 1) {
                StatusMessage msg = new StatusMessage(400, "Unable to delete post with id=" + post.id);
                writeAsJson(resp, msg);
                return;
            }

            writeAsJson(resp, post);
        } else {
            StatusMessage msg = new StatusMessage(400, "Invalid id given in link.");
            writeAsJson(resp, msg);
        }
    }
}
