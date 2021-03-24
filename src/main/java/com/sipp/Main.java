package com.sipp;

import com.sipp.dao.CommentDao;
import com.sipp.dao.sql.CommentDaoSql;
import com.sipp.dao.sql.DataSourceSupplier;
import com.sipp.dao.sql.PostDaoSql;
import com.sipp.model.Comment;
import com.sipp.model.Post;
import com.sipp.request.Requests;
import com.sipp.request.ResponseParser;
import com.sipp.service.RService;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        RService.fetchData();
    }
}
