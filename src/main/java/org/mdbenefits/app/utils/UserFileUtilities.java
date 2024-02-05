package org.mdbenefits.app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import formflow.library.data.UserFile;
import formflow.library.utils.UserFileMap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.util.unit.DataSize;

public class UserFileUtilities {

    /**
     * Formats the application.yaml `accepted-file-type` string to be more human-readable.
     *
     * @param fileTypeStr a string containing the file types this system can support
     * @return  A formatted string of the file types
     */
    public static String formatAcceptedFileTypeString(String fileTypeStr) {
        List<String> types = List.of(fileTypeStr.split(","));

        String fileTypes = types.stream()
            .map(String::trim)
            .collect(Collectors.joining(", "));

        int lastComma = fileTypes.lastIndexOf(",");
        return fileTypes.replace(fileTypes.substring(lastComma), ", and" + fileTypes.substring(lastComma+1));
    }


    /**
     * Returns a list of file information drawn from the userFiles and supplemented by the session's file information.
     *
     * @param userFileMapString the UserFileMap to extract files from.
     * @param userFiles         the list of user files to start with
     * @param flow              the flow the files of interest are in
     * @param inputName         name of input the files are associated with
     * @return
     * @throws JsonProcessingException
     */
    public static Map<UUID, Map<String, String>> getUserFileInfo(String userFileMapString, List<UserFile> userFiles, String flow,
            String inputName) throws JsonProcessingException {
        Map<UUID, Map<String, String>> fileData = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        UserFileMap userFileMap = objectMapper.readValue(userFileMapString, UserFileMap.class);
        Map<UUID, Map<String, String>> sessionFiles = userFileMap.getFiles(flow, inputName);

        userFiles.forEach(uFile -> {
            Map<String, String> fileFromSession = sessionFiles.get(uFile.getFileId());
            fileData.put(uFile.getFileId(), UserFile.createFileInfo(uFile, fileFromSession.get("thumbnailUrl")));
        });

        return fileData;
    }

    public static List<UUID> getUserFileIdsInOrder(List<UserFile> userFiles) {
        return userFiles.stream()
                .sorted(Comparator.comparing(UserFile::getCreatedAt).reversed())
                .map(UserFile::getFileId)
                .collect(Collectors.toList());
    }

    /**
     * Returns a string value of the `bytes` converted to MB and rounded to one decimal place.k
     *
     * @param bytes number of bytes to convert and round
     * @return String of bytes in MB, rounded to one decimal place
     */
    public static String getFileMbFromBytes(String bytes) {
        DataSize megaBytes = DataSize.ofMegabytes(1L);
        BigDecimal bd = BigDecimal.valueOf(Double.parseDouble(bytes) / megaBytes.toBytes())
                .setScale(1, RoundingMode.CEILING);
        return bd.toString();
    }
}
