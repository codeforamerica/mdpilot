package org.mdbenefits.app.cli;

import com.google.api.services.drive.model.File;
import java.io.IOException;
import java.util.List;

public interface GoogleDriveClient {

    // returns string ID of new folder
    String createFolder(String locationId, String folderName);

    String uploadFile(String folderId, String filename, String mimeType, byte[] data);
}
