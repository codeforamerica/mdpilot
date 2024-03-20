package org.mdbenefits.app.cli;

import com.google.api.services.drive.model.File;
import java.util.Map;
import java.util.List;

public interface GoogleDriveClient {

    /**
     * Creates a new folder in the location specified.
     *
     * @param locationId location, or parent folder, where the new folder should be created
     * @param folderName name of new folder
     * @param errors     error map for any errors to be added to
     * @return String id of the new folder
     */
    GoogleDriveFolder createFolder(String locationId, String folderName, Map<String, String> errors);

    /**
     * Uploads file data to the specified folder.
     *
     * @param folderId ID of folder to put the new file into
     * @param filename the name to use for the new file
     * @param mimeType the mimetype of new file
     * @param data     the file data
     * @param fileId   string id for the file to be used for tracking; can be anything useful to caller
     * @param errors   error map for any errors to be added to
     * @return String id of the new file
     */
    String uploadFile(String folderId, String filename, String mimeType, byte[] data, String fileId, Map<String, String> errors);

    /**
     * Searches for directories that exist with a certain name within a certain location.
     *
     * @param name     Name of directory to look for
     * @param parentId id of parent directory to look in
     * @return List of directories that were found with the name
     */
    List<File> findDirectory(String name, String parentId);

    /**
     * Hard deletes the specified directory with the matching directory ID; this bypasses the trash feature. Only "Managers" in a
     * Google Drive team environment can use this call. A "Content Manager" will not have sufficient permissions to use this.
     *
     * @param name        Name of the directory to delete
     * @param directoryId ID of directory to delete
     * @param errors      error map for any errors to be added to
     * @return boolean value indicating success or failure to delete
     */
    boolean deleteDirectory(String name, String directoryId, Map<String, String> errors);

    /**
     * Puts a specified directory into the trash. A Google Drive user with "Content Manager" role can use this method.
     *
     * @param name        Name of the directory to delete
     * @param directoryId ID of directory to delete
     * @param errors      error map for any errors to be added to
     * @return boolean value indicating success or failure to delete
     */
    boolean trashDirectory(String name, String directoryId, Map<String, String> errors);
}
