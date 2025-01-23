package com.jmy.courseRecommender.domain;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CourseRecommenderController {
    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Hello World";
    }
}
