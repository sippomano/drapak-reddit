package com.sipp.service.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipp.model.Post;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sipp.service.web.HelpServlet.getQueryStringParams;

@WebServlet(name = "PostServlet", urlPatterns = {"/api/getPost"})
public class PostServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String> parameters = getQueryStringParams(req.getQueryString());
        //unmodifiable list will be supplied by RService method
        List<Post> posts = new ArrayList<>();
        posts = posts.stream()
                .parallel()
                .filter(p -> !parameters.containsKey("subreddit") || (p.getSubreddit().equals(parameters.get("subreddit"))))
                .filter(p -> !parameters.containsKey("minScore") || (p.getScore() >= Integer.parseInt(parameters.get("minScore"))))
                .filter(p -> !parameters.containsKey("from") || (p.getCreationTime() >= Long.parseLong(parameters.get("from"))))
                .filter(p -> !parameters.containsKey("to") || (p.getCreationTime() <= Long.parseLong(parameters.get("to"))))
                .limit(parameters.containsKey("count") ? Long.parseLong(parameters.get("count")) : posts.size())
                .collect(Collectors.toList());

        ByteArrayOutputStream postStream = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(postStream, posts);
        resp.getWriter().print(postStream.toString());
    }


}
