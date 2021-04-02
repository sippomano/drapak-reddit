package com.sipp.service.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipp.model.Post;
import com.sipp.service.RService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.sipp.service.web.HelpServlet.getQueryStringParams;

@Slf4j
public class PostServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Inside GET in PostServlet");
        resp.setContentType("application/json");
        try {
            Map<String, String> parameters = getQueryStringParams(req.getQueryString());
            //unmodifiable list will be supplied by RService method
            Set<Post> posts = RService.getPostCache();
            posts = posts.stream()
                    .parallel()
                    .filter(p -> !parameters.containsKey("subreddit") || (Arrays.asList(parameters.get("subreddit").split(","))).contains(p.getSubreddit()))
                    .filter(p -> !parameters.containsKey("minScore") || (p.getScore() >= Integer.parseInt(parameters.get("minScore"))))
                    .filter(p -> !parameters.containsKey("from") || (p.getCreationTime() >= Long.parseLong(parameters.get("from"))))
                    .filter(p -> !parameters.containsKey("to") || (p.getCreationTime() <= Long.parseLong(parameters.get("to"))))
                    .limit(parameters.containsKey("count") ? Long.parseLong(parameters.get("count")) : posts.size())
                    .collect(Collectors.toSet());

            ByteArrayOutputStream postStream = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();

            mapper.writeValue(postStream, posts);
            resp.getWriter().print(postStream.toString());
        } catch (NumberFormatException e) {
            resp.sendError(400, "Invalid request. Multiple parameters could have been passed when only one is allowed. Check if date is in unix epoch timestamp and other numbers are correct");
        } catch (Exception e) {
            resp.sendError(500, "Unknown error occurred. Check the query string");
        }
    }


}
