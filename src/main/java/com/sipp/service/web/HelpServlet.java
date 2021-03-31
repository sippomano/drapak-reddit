package com.sipp.service.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "HelpServlet", urlPatterns = {"/"})
public class HelpServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("src\\main\\resources\\web\\instruction.html"))){
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            resp.getWriter().print(sb.toString());
        } catch (IOException e) {
            //send proper code with message
        }
    }

    static Map<String, String> getQueryStringParams(String queryString) {
        String[] parametersPairs =  queryString.split("&");
        Map<String, String> parameters = new HashMap<>();
        for (String parameter : parametersPairs) {
            String key = parameter.split("=")[0];
            String value = parameter.split("=")[1];

            parameters.merge(key, value, (v1, v2) -> v1 +"," + v2);
        }
        return parameters;
    }
}
