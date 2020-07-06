package com.mycompany.app;

import java.io.Console;
import java.util.Random;
import com.azure.storage.common.*;
import com.azure.storage.file.datalake.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    // 100 MB
    private static final int _size = 100 * 1024 * 1024;

    public static void main(String[] args) {
        String endpoint = System.getenv("STORAGE_ENDPOINT");
        String name = System.getenv("STORAGE_NAME");
        String key = System.getenv("STORAGE_KEY");
        
        byte[] buffer = new byte[_size];
        (new Random(0)).nextBytes(buffer);
        
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(null, null);
            Files.write(tempFile, buffer);
        }
        catch (Exception e) {
        }

        DataLakeServiceClient dataLakeServiceClient = new DataLakeServiceClientBuilder()
        .endpoint(endpoint)
        .credential(new StorageSharedKeyCredential(name, key))
        .buildClient();

        DataLakeFileSystemClient fileSystemClient =
             dataLakeServiceClient.getFileSystemClient("myfilesystem");

        try {
            fileSystemClient.create();
        }
        catch (Exception e) {
        }

        DataLakeFileClient fileClient = fileSystemClient.getFileClient("myfile");

        System.out.println(String.format("Uploading %d bytes ...", _size));
        long t1 = System.currentTimeMillis();
        fileClient.uploadFromFile(tempFile.toString(), true);
        long t2 = System.currentTimeMillis();
        System.out.println(String.format("Completed in %d milliseconds", t2 - t1));
    }
}
