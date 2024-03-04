package org.mdbenefits.app.data;

import formflow.library.data.Submission;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface TransmissionRepository extends CrudRepository<Transmission, UUID> {

    List<Transmission> findTransmissionsByStatus(String status);

    Transmission findTransmissionBySubmission(Submission submission);
}
