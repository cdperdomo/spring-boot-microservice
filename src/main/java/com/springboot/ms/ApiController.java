package com.springboot.ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "ping")
@CrossOrigin
public class ApiController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> ping(HttpServletRequest request) {
        Map<String, String> reqHeaders = new HashMap<>();
        if (request != null) {
            Enumeration<String> h = request.getHeaderNames();
            while (h.hasMoreElements()) {
                String headerName = h.nextElement();
                reqHeaders.put(headerName, request.getHeader(headerName));
            }
        }
        LOGGER.info("Request from Host '{}', Addr '{}', headers '{}'", request.getRemoteHost(), request.getRemoteAddr(), reqHeaders);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> map = new HashMap<>();
        map.put("status", "pong");
        return ResponseEntity.ok().headers(headers).body(map);
    }
}
