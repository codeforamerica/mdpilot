package org.mdbenefits.app;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * A controller to render static pages that are not in any flow.
 */
@Controller
public class StaticPageController {

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

        Map<String, Object> model = new HashMap<>();
        model.put("screen", "/");
        model.put("sessionBad", request.getParameter("sessionBad"));
        return new ModelAndView("index", model);
    }

    @GetMapping("/privacy")
    String getPrivacy() {
        return "privacy";
    }
}
