package com.spark.test;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api")
@Controller
public class EtfController {

	@Autowired
    Main main;

	@CrossOrigin
	@RequestMapping("test")
    public ResponseEntity<String> words() throws IOException {
        return new ResponseEntity<String>(main.main(0, 50), HttpStatus.OK);
    }
}
