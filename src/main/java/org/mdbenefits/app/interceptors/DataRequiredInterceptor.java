package org.mdbenefits.app.interceptors;

import formflow.library.FormFlowController;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class DataRequiredInterceptor implements HandlerInterceptor {

    public static final String PATH_FORMAT = "/flow/{flow}/{screen}";
    private final SubmissionRepositoryService submissionRepositoryService;
    private static final Map<String, String> REQUIRED_DATA = Map.ofEntries(
            // Step 1
            Map.entry("howToAddDocuments", "firstName"),
            Map.entry("uploadDocuments", "firstName"),
            Map.entry("docSubmitConfirmation", "firstName"),
            Map.entry("finalConfirmation", "firstName")
    );

    public DataRequiredInterceptor(SubmissionRepositoryService submissionRepositoryService) {
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            var parsedUrl = new AntPathMatcher().extractUriTemplateVariables(PATH_FORMAT, request.getRequestURI());
            var requiredData = REQUIRED_DATA.get(parsedUrl.get("screen"));
            String redirect_url = String.format("/flow/laDocUpload/clientInfo?intercepted=%s", parsedUrl.get("screen"));

            if (requiredData == null) {
                return true; // There are no data requirements for this page
            }

            var session = request.getSession(false);
            if (session == null) {
                log.info("No session present (missing field data %s), redirecting to clientInfo page".formatted(requiredData));
                response.sendRedirect(redirect_url);

                return false;
            }

            Map<String, UUID> submissionMap = (Map) session.getAttribute(FormFlowController.SUBMISSION_MAP_NAME);
            UUID submissionId = null;

            if (submissionMap != null && submissionMap.get(parsedUrl.get("flow")) != null) {
                submissionId = submissionMap.get(parsedUrl.get("flow"));
            }

            if (submissionId != null) {
                var submissionMaybe = this.submissionRepositoryService.findById(submissionId);
                if (submissionMaybe.isPresent()) {
                    var submission = submissionMaybe.get();
                    if (submission.getInputData().getOrDefault(requiredData, "").toString().isBlank()) {
                        log.error("Submission %s missing field data %s, redirecting to clientInfo page".formatted(submissionId,
                                requiredData));
                        response.sendRedirect(redirect_url);
                        return false;
                    }
                } else {
                    log.error("Submission %s not found in database (required field %s), redirecting to clientInfo page".formatted(
                            submissionId, requiredData));
                    response.sendRedirect(redirect_url);
                    return false;
                }
            } else {
                log.error("No submission ID in session (required field %s), redirecting to clientInfo page".formatted(
                        requiredData));
                response.sendRedirect(redirect_url);
                return false;
            }

            return true;
        } catch (IllegalStateException e) {
            return true;
        }
    }
}
