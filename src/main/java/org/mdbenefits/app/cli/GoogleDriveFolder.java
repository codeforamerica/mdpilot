package org.mdbenefits.app.cli;

import com.google.api.services.drive.model.File;

public class GoogleDriveFolder {


    private final File file;

    GoogleDriveFolder(File file) {
        this.file = file;
    }

    public String getId() {
        return file.getId();
    }

    public String getUrl() {
        return file.getWebViewLink();
    }
}
