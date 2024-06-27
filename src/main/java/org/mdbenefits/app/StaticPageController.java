package org.mdbenefits.app;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * A controller to render static pages that are not in any flow.
 */
@Controller
public class StaticPageController {
    
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Renders the website index page.
     *
     * @param request The current request, not null
     * @return the static page template
     */
    @GetMapping("/")
    ModelAndView getIndex(HttpServletRequest request) {
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            httpSession.invalidate();
        }
        httpSession = request.getSession(true);

        Map<String, Object> model = new HashMap<>();
        model.put("screen", "/");

        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        if (Arrays.asList(activeProfiles).contains("demo") || Arrays.asList(activeProfiles).contains("staging")) {
            return new ModelAndView("index", model);
        }
        return new ModelAndView("pilot-ended", model);
    }

    @GetMapping("/privacy")
    String getPrivacy() {
        return "privacy";
    }
}
