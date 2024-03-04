package org.mdbenefits.app.cli;

import com.google.api.services.drive.model.File;
import java.util.Map;
import java.util.List;

public interface GoogleDriveClient {

    // returns string ID of new folder
    String createFolder(String locationId, String folderName, Map<String, String> errors);

    String uploadFile(String folderId, String filename, String mimeType, byte[] data, Map<String, String> errors);

    List<File> findDirectory(String name, String parentId);

    boolean deleteDirectory(String name, String directoryId, Map<String, String> errors);
}
