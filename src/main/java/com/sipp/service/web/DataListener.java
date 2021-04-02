package com.sipp.service.web;

import com.sipp.service.RService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class DataListener implements ServletContextListener {

    private ExecutorService executorService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        executorService = Executors.newSingleThreadExecutor();

        Callable<String> c = () -> {
            try {
                RService.loadDataToCache();
                while (true) {
                    RService.fetchData();
                    log.info("Data has been fetched from reddit and database has been updated");
                    //TBD optimize to only load data from some(how long?) period of time
                    long timestamp = RService.loadDataToCache();
                    log.info("Data cache has been updated at: " + timestamp);
                }
            } catch (InterruptedException e) {
                log.info("data thread has been interrupted, data flow has been stopped");
                return "DataListener data thread has been stopped";
            }
        };
        executorService.submit(c);
        log.info("Data thread has been started");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        executorService.shutdownNow();
    }
}
