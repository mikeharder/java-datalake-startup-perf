package com.mycompany.app;

import java.io.Console;
import com.azure.storage.common.*;
import com.azure.storage.file.datalake.*;

public class App {
    public static void main(String[] args) {
        String endpoint = System.getenv("STORAGE_ENDPOINT");
        String name = System.getenv("STORAGE_NAME");
        String key = System.getenv("STORAGE_KEY");
        
        DataLakeServiceClient dataLakeServiceClient = new DataLakeServiceClientBuilder()
        .endpoint(endpoint)
        .credential(new StorageSharedKeyCredential(name, key))
        .buildClient();

        DataLakeFileSystemClient dataLakeFileSystemClient =
             dataLakeServiceClient.getFileSystemClient("myfilesystem");

        try {
            dataLakeFileSystemClient.create();
        }
        catch (Exception e) {
        }

        DataLakeFileClient dataLakeFileClient = dataLakeFileSystemClient.getFileClient("myfile");
    }
}
