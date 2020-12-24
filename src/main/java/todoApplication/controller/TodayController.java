package todoApplication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/today")
public class TodayController {
    @GetMapping
    String today(){
        return "today";
    }
}
