package org.mdbenefits.app.cli;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Create;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile({"dev", "staging", "prod"})
public class GoogleDriveClientImpl implements GoogleDriveClient {

    private final String GOOGLE_CREDS = System.getenv("GOOGLE_DRIVE_CREDS");
    private final Drive service;

    private final String teamDriveId;

    public GoogleDriveClientImpl(@Value("${transmission.google-drive-directory-id.team-drive:''}") String teamDriveId)
            throws IOException, GeneralSecurityException {
        this.teamDriveId = teamDriveId;
        // Build a new authorized API client service.
        service = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                getCredentialsServiceAccount())
                .setApplicationName("MD Benefits")
                .build();
    }

    private HttpRequestInitializer getCredentialsServiceAccount() throws IOException {
        InputStream credentials = new ByteArrayInputStream(GOOGLE_CREDS.getBytes(StandardCharsets.UTF_8));

        return GoogleCredential.fromStream(credentials).createScoped(DriveScopes.all());
    }

    @Override
    public GoogleDriveFolder createFolder(String parentFolderId, String name, Map<String, String> errors) {
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setDriveId(teamDriveId);
        fileMetadata.setParents(Collections.singletonList(parentFolderId));
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        try {
            String properties = String.join(",", GoogleDriveFolder.properties);
            File file = service.files()
                    .create(fileMetadata)
                    .setFields(properties)
                    .setSupportsAllDrives(true) // necessary for team drives
                    .execute();
            log.info("Created Google Drive Folder with ID: " + file.getId());
            return new GoogleDriveFolder(file);
        } catch (Exception e) {
            String error = String.format("Unable to create folder %s in %s", name, parentFolderId);
            if (e instanceof GoogleJsonResponseException) {
                error += ": " + ((GoogleJsonResponseException) e).getDetails();
            }
            errors.put("createFolder", error);
            log.error(error);
        }
        return null;
    }

    @Override
    public String uploadFile(String parentFolderId, String fileName, String mimeType, byte[] fileBytes, String fileId,
            Map<String, String> errors) {

        File fileMetadata = new File();
        fileMetadata.setName(fileName)
                .setParents(Collections.singletonList(parentFolderId))
                .setDriveId(teamDriveId);

        ByteArrayContent mediaContent = new ByteArrayContent(mimeType, fileBytes);

        try {
            Create createRequest = service.files().create(fileMetadata, mediaContent)
                    .setSupportsAllDrives(true)
                    .setSupportsTeamDrives(true)
                    .setFields("id");

            createRequest.getMediaHttpUploader()
                    .setDirectUploadEnabled(false)
                    .setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE);

            File file = createRequest.execute();

            log.info("Uploaded file with ID: " + file.getId());
            return file.getId();
        } catch (Exception e) {
            String error = String.format("Unable to upload file %s (ID: %s): %s", fileName, fileId, e.getMessage());
            if (e instanceof GoogleJsonResponseException) {
                error += ": " + ((GoogleJsonResponseException) e).getDetails();
            }
            errors.put("uploadFile", error);
            log.error(error);
        }
        return "";
    }

    @Override
    public List<File> findDirectory(String name, String parentId) {
        try {
            Files.List fileList = service.files().list()
                    .setQ(String.format(
                            "name = '%s' and '%s' in parents and mimeType='application/vnd.google-apps.folder' and trashed = false",
                            name, parentId));
            if (teamDriveId.isBlank()) {
                fileList.setSpaces("drive");
            } else {
                fileList.setSupportsAllDrives(true)
                        .setIncludeItemsFromAllDrives(true)
                        .setCorpora("drive")
                        .setDriveId(teamDriveId);
            }

            FileList result = fileList.setFields("nextPageToken, files(id, name)")
                    .execute();

            List<File> dirs = result.getFiles();
            if (dirs == null || dirs.isEmpty()) {
                log.info("No directories found with name {}.", name);
            }
            return dirs;
        } catch (IOException e) {
            log.error("Unable to find directory: {}", e.getMessage());
        }
        return List.of();
    }

    @Override
    public boolean trashDirectory(String name, String directoryId, Map<String, String> errors) {
        try {
            File metadata = new File();
            metadata.setTrashed(true);
            service.files()
                    .update(directoryId, metadata)
                    .setSupportsTeamDrives(true)
                    .setSupportsAllDrives(true)
                    .execute();
            log.info("Deleted directory {} ({})", name, directoryId);
        } catch (IOException e) {
            String error = String.format("Unable to delete directory %s: %s", directoryId, e.getMessage());
            log.error(error);
            errors.put("delete", error);
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteDirectory(String name, String directoryId, Map<String, String> errors) {
        try {
            service.files()
                    .delete(directoryId)
                    .setSupportsTeamDrives(true)
                    .setSupportsAllDrives(true)
                    .execute();
            log.info("Deleted directory {} ({})", name, directoryId);
        } catch (IOException e) {
            String error = String.format("Unable to delete directory %s: %s", directoryId, e.getMessage());
            log.error(error);
            errors.put("delete", error);
            return false;
        }
        return true;
    }

    private void listFiles(String folderId, int numberOfFiles) throws IOException {
        FileList result = service.files().list()
                .setQ(String.format("'%s' in parents and trashed = false", folderId))
                .setSpaces("drive")
                .setPageSize(numberOfFiles)
                .setDriveId(teamDriveId)
                .setCorpora("drive")
                .setSupportsAllDrives(true)
                .setSupportsTeamDrives(true)
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
