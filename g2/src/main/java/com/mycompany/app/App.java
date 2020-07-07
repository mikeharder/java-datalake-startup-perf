package com.mycompany.app;

import java.io.Console;
import java.util.Random;
import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.storage.common.*;
import com.azure.storage.file.datalake.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) {
        int size = 1024 * 1024;
        if (args.length > 0) {
            size = Integer.parseInt(args[0]) * 1024 * 1024;
        }

        String auth = "aad";
        if (args.length > 1) {
            auth = args[1];
        }

        String endpoint = System.getenv("STORAGE_ENDPOINT");
        
        byte[] buffer = new byte[size];
        (new Random(0)).nextBytes(buffer);
        
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(null, null);
            Files.write(tempFile, buffer);
        }
        catch (Exception e) {
        }

        
        DataLakeServiceClientBuilder builder = new DataLakeServiceClientBuilder()
        .endpoint(endpoint);

        if (auth.equals("aad")) {
            String tenantId = System.getenv("AAD_TENANT_ID");
            String clientId = System.getenv("AAD_CLIENT_ID");
            String clientSecret = System.getenv("AAD_CLIENT_SECRET");

            TokenCredential tokenCredential = new ClientSecretCredentialBuilder()
            .tenantId(tenantId)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .build();

            builder = builder.credential(tokenCredential);
        } else if (auth.equals("sharedkey")) {
            String name = System.getenv("STORAGE_NAME");
            String key = System.getenv("STORAGE_KEY");
            builder = builder.credential(new StorageSharedKeyCredential(name, key));
        }
        else {
            System.out.println("Invalid auth method: '" + auth + "'");
        }

        DataLakeServiceClient serviceClient = builder.buildClient();

        DataLakeFileSystemClient fileSystemClient =
             serviceClient.getFileSystemClient("myfilesystem");

        try {
            fileSystemClient.create();
        }
        catch (Exception e) {
        }

        DataLakeFileClient fileClient = fileSystemClient.getFileClient("myfile");

        System.out.println(String.format("Uploading %d bytes using %s...", size, auth));
        long t1 = System.currentTimeMillis();
        fileClient.uploadFromFile(tempFile.toString(), true);
        long t2 = System.currentTimeMillis();
        System.out.println(String.format("Completed in %d milliseconds", t2 - t1));
    }
}
