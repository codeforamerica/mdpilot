//package org.mdbenefits.app.submission.actions;
//
//import formflow.library.config.submission.Action;
//import formflow.library.data.FormSubmission;
//import formflow.library.data.Submission;
//import java.util.HashMap;
//import java.util.Map;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
///**
// *
// */
//@Component
//@Slf4j
//public class MailingAddressSetToHomeAddress implements Action {
//
//    @Override
//    public void run(FormSubmission formSubmission, Submission submission) {
//        Map<String, Object> inputData = submission.getInputData();
//
//        if (submission.getInputData().get("sameAsHomeAddress[]").toString().equals("[true]")) {
//            formSubmission.formData.put("mailingAddressZip", inputData.get("homeAddressZip").toString());
//            formSubmission.formData.put("mailingAddressState", inputData.get("homeAddressState").toString());
//            formSubmission.formData.put("mailingAddressCity", inputData.get("homeAddressCity").toString());
//            formSubmission.formData.put("mailingAddressStreetAddress1", inputData.get("homeAddressStreetAddress1").toString());
//            formSubmission.formData.put("mailingAddressStreetAddress2", inputData.get("homeAddressStreetAddress2").toString());
////            formSubmission.setFormData(formData);
//        }
//    }
//}
