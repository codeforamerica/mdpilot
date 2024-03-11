package org.mdbenefits.app.cli;

import com.google.api.services.drive.model.File;
import java.util.List;

public class GoogleDriveFolder {

    /**
     * The list of Google Drive File properties this class is concerned with.
     */
    public static final List<String> properties = List.of("id", "webViewLink");

    private final File file;

    GoogleDriveFolder(File file) {
        this.file = file;
    }

    /**
     * Returns the Google Drive id of the folder
     *
     * @return String containing id of folder
     */
    public String getId() {
        return file.getId();
    }

    /**
     * Returns the URL of the Google Drive folder.
     *
     * @return String containing the URL of the folder.
     */
    public String getUrl() {
        return file.getWebViewLink();
    }
}
