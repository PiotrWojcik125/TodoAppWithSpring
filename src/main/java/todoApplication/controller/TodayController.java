package todoApplication.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/today")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public class TodayController {
    @GetMapping
    String today(){
        return "today";
    }
}
