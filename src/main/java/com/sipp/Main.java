package com.sipp;

import com.sipp.service.RService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        RService.fetchData();
    }
}
