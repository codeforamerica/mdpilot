package org.mdbenefits.app.cli;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
//@Profile({"production", "staging"})
public class GoogleDriveClientImpl implements GoogleDriveClient {

    // TODO - read these in from somewhere else
    private final String CREDENTIALS_FILE_PATH = "service_account.json";
    private final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final String APPLICATION_NAME = "MD Benefits";

    private Drive service;

    public GoogleDriveClientImpl() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentialsServiceAccount(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private final String GOOGLE_CREDS = System.getenv("GOOGLE_DRIVE_CREDS");

    private HttpRequestInitializer getCredentialsServiceAccount(NetHttpTransport httpTransport) throws IOException {
        InputStream credentials = new ByteArrayInputStream(GOOGLE_CREDS.getBytes(StandardCharsets.UTF_8));

        return GoogleCredential.fromStream(credentials).createScoped(DriveScopes.all());
    }

    /**
     * Create a new file directory in Google Drive in the parent folder specified.
     *
     * @param parentFolderId
     * @param name
     * @return
     */
    public String createFolder(String parentFolderId, String name, List<String> errors) {
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setParents(Collections.singletonList(parentFolderId));
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        try {
            File file = service.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            System.out.println("Folder ID: " + file.getId());
            return file.getId();
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            errors.add("Unable to create folder: " + e.getDetails());
            log.error("Unable to create folder: " + e.getDetails());
        } catch (IOException e) {
            log.error("Unable to create folder: " + e);
        }
        return null;
    }

    /**
     * Upload a byte array as a file in Google Drive
     *
     * @param parentFolderId
     * @param fileName
     * @param mimeType
     * @param fileBytes
     * @return
     */
    public String uploadFile(String parentFolderId, String fileName, String mimeType, byte[] fileBytes, List<String> errors) {

        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList(parentFolderId));

        ByteArrayContent mediaContent = new ByteArrayContent(mimeType, fileBytes);

        try {
            // TODO:  either change to resumeable upload OR craft retries with
            // exponential back off
            File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            log.info("New file has ID: " + file.getId());
            return file.getId();
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            log.error("Unable to upload file: " + e.getDetails());
            return "";
        } catch (IOException e) {
            log.error("Unable to upload file: " + e);
            return "";
        }
    }
    
    public List<String> findDirectory(String name, String parentId) {
        List<String> directories = new ArrayList<>();
        try {
            FileList result = service.files().list()
                    .setQ(String.format("name = '%s' and '%s' in parents and mimeType='application/vnd.google-apps.folder' and trashed = false", name, parentId))
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id)")
                    .execute();
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                log.info("No directories found with name {}.", name);
            } else {
                for (File file : files) {
                    directories.add(file.getId());
                }
            }
        } catch (IOException e) {
            log.error("Unable to find directory: " + e);
        }
        return directories;
    }

    private void listFiles(String folderId, int numberOfFiles) throws IOException {
        FileList result = service.files().list()
                .setQ(String.format("'%s' in parents and trashed = false", folderId))
                .setSpaces("drive")
                .setPageSize(numberOfFiles)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            log.info("No files found.");
        } else {
            log.info("Files:");
            for (File file : files) {
                log.info("{} ({})", file.getName(), file.getId());
            }
        }
    }
}
