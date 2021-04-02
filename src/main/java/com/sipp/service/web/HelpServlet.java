package com.sipp.service.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


import java.io.*;
import java.util.*;

@Slf4j
public class HelpServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        log.info("Inside GET in HelpServlet");
        String path = this.getServletContext().getRealPath("/index.html");
        log.info("path: " + path);
        try (BufferedReader reader = new BufferedReader(new FileReader(path))){

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            log.info("File has been read, will be returned to the client");
            resp.getWriter().print(sb.toString());
        } catch (IOException e) {
            resp.sendError(500, "Internal error. Unable to read instruction.");
        }
    }

    static Map<String, String> getQueryStringParams(String queryString) {
        String[] parametersPairs =  queryString.split("&");
        Map<String, String> parameters = new HashMap<>();
        for (String parameterPair : parametersPairs) {
            String key = parameterPair.substring(0, parameterPair.indexOf('='));
            String value = parameterPair.substring(parameterPair.indexOf('=')+1);
            parameters.merge(key, value, (v1, v2) -> v1 + "," + v2);
        }
        return parameters;
    }
}
