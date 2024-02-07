package org.mdbenefits.app.submission.actions;

import static formflow.library.inputs.FieldNameMarkers.DYNAMIC_FIELD_MARKER;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * This is a parent class for `beforeSaveAction` that will move data from a wildcard field into the household member's entry that
 * it belongs with.
 */
@Slf4j
public abstract class MoveDataIntoSubflowData implements Action {

    /**
     * A protected class that will aid in moving data from wildcard fields into their actual subflow's data. This will then REMOVE
     * the `fromFieldName` field from the data once the data is moved.
     *
     * @param submission    The submission to modify
     * @param subflow       The subflow the data should go into
     * @param fromFieldName The input field to pull the data from
     * @param toFieldName   The household input field to store the data in
     */
    protected void moveDataToSubflowData(Submission submission,
        String subflow,
        String fromFieldName,
        String toFieldName) {

        List<Map<String, Object>> subflowData = (List) submission.getInputData().getOrDefault(subflow, new ArrayList<>());
        Map<String, Object> inputData = submission.getInputData();

        subflowData.forEach(item -> {
            String uuid = (String) item.get("uuid");

            String inputName = fromFieldName + DYNAMIC_FIELD_MARKER + uuid;
            String fieldValue = (String) inputData.get(inputName);

            if (fieldValue != null || !fieldValue.isBlank()) {
                item.put(toFieldName, fieldValue);
                inputData.remove(inputName);
            } else {
                log.warn("Cannot find dynamic field data for field name '{}'", inputName);
            }
        });
    }
}
