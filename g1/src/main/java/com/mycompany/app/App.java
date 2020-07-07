// https://docs.microsoft.com/en-us/java/api/overview/azure/datalake?view=azure-java-stable

package com.mycompany.app;

import java.io.*;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Path;
import com.microsoft.azure.datalake.store.*;
import com.microsoft.azure.datalake.store.oauth2.*;

public class App {
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    public static void main(String[] args) {
        int size = 1024 * 1024;
        if (args.length > 0) {
            size = Integer.parseInt(args[0]) * 1024 * 1024;
        }

        String endpoint = System.getenv("STORAGE_ENDPOINT_G1");

        String tenantId = System.getenv("AAD_TENANT_ID");
        String clientId = System.getenv("AAD_CLIENT_ID");
        String clientSecret = System.getenv("AAD_CLIENT_SECRET");
        
        byte[] buffer = new byte[size];
        (new Random(0)).nextBytes(buffer);
        
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(null, null);
            Files.write(tempFile, buffer);
        }
        catch (Exception e) {
        }

        String authTokenEndpoint = "https://login.microsoftonline.com/" + tenantId + "/oauth2/token";
        AccessTokenProvider provider = new ClientCredsTokenProvider(authTokenEndpoint, clientId, clientSecret);
        ADLStoreClient adlStoreClient = ADLStoreClient.createClient(endpoint, provider);
        
        try {
            adlStoreClient.createDirectory("myfilesystem");

            ADLFileOutputStream outStream = adlStoreClient.createFile("myfile", IfExists.OVERWRITE);
            InputStream inputStream = new FileInputStream(tempFile.toString());

            System.out.println(String.format("Uploading %d bytes ...", size));
            long t1 = System.currentTimeMillis();
            copyStream(inputStream, outStream);
            outStream.close();
            long t2 = System.currentTimeMillis();
            System.out.println(String.format("Completed in %d milliseconds", t2 - t1));
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static long copyStream(InputStream input, OutputStream out) throws IOException {
        long transferred = 0;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;
        while ((read = input.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
            out.write(buffer, 0, read);
            transferred += read;
        }
        return transferred;
    }
}
