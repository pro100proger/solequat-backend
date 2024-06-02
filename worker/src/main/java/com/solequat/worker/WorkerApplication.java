package com.solequat.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.core")
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(excludeFilters = {@ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.CUSTOM, classes = {org.springframework.boot.context.TypeExcludeFilter.class}),@ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.CUSTOM, classes = {org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter.class})})
public class WorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }

}
