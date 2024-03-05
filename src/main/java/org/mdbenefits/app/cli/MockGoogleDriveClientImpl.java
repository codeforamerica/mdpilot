package org.mdbenefits.app.cli;

import com.google.api.services.drive.model.File;
import formflow.library.data.UserFile;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile({"test"})
public class MockGoogleDriveClientImpl implements GoogleDriveClient {

    @Override
    public String createFolder(String locationId, String folderName, Map<String, String> errors) {
        log.info("Mock create folder {}", folderName);
        return "12345";
    }

    @Override
    public String uploadFile(String folderId, String filename, String mimeType, byte[] data, String fileId,
            Map<String, String> errors) {
        log.info("Mock uploadFile {}", filename);
        return "23456";
    }

    @Override
    public List<File> findDirectory(String name, String parentId) {
        log.info("Mock finding directories named {}", name);
        return List.of();
    }

    @Override
    public boolean deleteDirectory(String name, String directoryId, Map<String, String> errors) {
        log.info("Pretending to delete directory.");
        return true;
    }

}
