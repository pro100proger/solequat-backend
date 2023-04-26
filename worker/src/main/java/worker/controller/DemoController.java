package worker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("api/v1/demo")
public class DemoController {


    @GetMapping
    public ResponseEntity<String> sayHello() {
        log.info("Demo");
        return ResponseEntity.ok("Hello!");
    }
}
