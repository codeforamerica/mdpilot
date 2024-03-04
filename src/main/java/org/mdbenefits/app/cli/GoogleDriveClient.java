package org.mdbenefits.app.cli;

import java.util.List;

public interface GoogleDriveClient {

    // returns string ID of new folder
    String createFolder(String locationId, String folderName, List<String> errors);

    String uploadFile(String folderId, String filename, String mimeType, byte[] data, List<String> errors);
    
    List<String> findDirectory(String name, String parentId);
}
