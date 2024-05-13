package org.mdbenefits.app.data;

import formflow.library.data.Submission;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@Table(name = "transmissions")
public class Transmission {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    private Submission submission;

    @CreationTimestamp(source = SourceType.DB)
    private OffsetDateTime createdAt;

    @UpdateTimestamp(source = SourceType.DB)
    private OffsetDateTime updatedAt;

    private OffsetDateTime sentAt;

    private int retryCount;

    String status = "QUEUED";

    @Type(JsonType.class)
    private Map<String, String> errors;

    public static Transmission fromSubmission(Submission submission) {
        Transmission transmission = new Transmission();
        transmission.setSubmission(submission);
        return transmission;
    }
}
