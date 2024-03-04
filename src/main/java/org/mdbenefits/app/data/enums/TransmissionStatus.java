package org.mdbenefits.app.data.enums;

import lombok.Getter;

@Getter
public enum TransmissionStatus {
    QUEUED,
    TRANSMITTING,
    FAILED,
    COMPLETED;
}
