package com.sipp;

import com.sipp.model.Post;
import com.sipp.request.Requests;
import com.sipp.request.ResponseParser;
import com.sipp.service.RService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        RService.fetchData();
    }
}
