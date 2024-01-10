package org.mdbenefits.app.cli;

import java.io.IOException;

public interface PGPEncryptor {

  byte[] signAndEncryptPayload(String filename) throws IOException;
}
