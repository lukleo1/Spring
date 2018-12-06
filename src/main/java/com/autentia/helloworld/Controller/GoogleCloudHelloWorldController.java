package com.autentia.helloworld.Controller;

import com.autentia.helloworld.Dto.FileDto;
import com.autentia.helloworld.Dto.FolderDto;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import static com.autentia.helloworld.Util.CreateFolder.createGoogleFolder;
import static com.autentia.helloworld.Util.FindFilesByName.getGoogleFilesByName;
import static com.autentia.helloworld.Util.GetSubFolders.getGoogleRootFolders;
import static com.autentia.helloworld.Util.GetSubFoldersByName.getGoogleRootFoldersByName;
import static com.autentia.helloworld.Util.ShareGoogleFile.createPermissionForEmail;

@RestController
public class GoogleCloudHelloWorldController {

    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // Directory to store user credentials for this application.
//    private static final java.io.File CREDENTIALS_FOLDER //
//            = new java.io.File(System.getProperty("user.home"), "credentials");

    private static final String CLIENT_SECRET_FILE_NAME = "client_secret.json";

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        java.io.File clientSecretFilePath= ResourceUtils.getFile("classpath:credentials/"+CLIENT_SECRET_FILE_NAME);

        java.io.File credentials_folder= ResourceUtils.getFile("classpath:credentials");

        if (!clientSecretFilePath.exists()) {
            throw new FileNotFoundException("Please copy " + CLIENT_SECRET_FILE_NAME //
                    + " to folder: " + credentials_folder.getAbsolutePath());
        }

        // Load client secrets.
        InputStream in = new FileInputStream(clientSecretFilePath);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(credentials_folder))
                .setAccessType("offline").build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
    @GetMapping(value = "/")
    public String hello() throws IOException, GeneralSecurityException {
        java.io.File credentials_folder= ResourceUtils.getFile("classpath:credentials");

        System.out.println("CREDENTIALS_FOLDER: " + credentials_folder.getAbsolutePath());

        // 1: Create CREDENTIALS_FOLDER
        if (!credentials_folder.exists()) {
            credentials_folder.mkdirs();

            System.out.println("Created Folder: " + credentials_folder.getAbsolutePath());
            System.out.println("Copy file " + CLIENT_SECRET_FILE_NAME + " into folder above.. and rerun this class!!");
            return "Carpeta Creada";
        }

        // 2: Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        // 3: Read client_secret.json file & create Credential object.
        Credential credential = getCredentials(HTTP_TRANSPORT);

        // 5: Create Google Drive Service.
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential) //
                .setApplicationName(APPLICATION_NAME).build();

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list().setPageSize(10).setFields("nextPageToken, files(id, name)").execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
        return "Hola Mundo Entelgy Google Cloud";
    }

//    @PostMapping(path = "/file", consumes = "application/json", produces = "application/json")
//    public void createFile(@RequestBody FileDto fileDto) throws IOException, GeneralSecurityException {
//
//        java.io.File uploadFile = new java.io.File(CREDENTIALS_FOLDER.getAbsolutePath()+"/test.txt");
//
//        // Create Google File:
//
//        File googleFile = createGoogleFile(fileDto.getFolderIdParent(), fileDto.getContentType(), fileDto.getCustomFileName(), uploadFile);
//
//        System.out.println("Created Google file!");
//        System.out.println("WebContentLink: " + googleFile.getWebContentLink() );
//        System.out.println("WebViewLink: " + googleFile.getWebViewLink() );
//
//        System.out.println("Done!");
//
//    }

    @GetMapping(value = "/folder/list")
    public String getSubFolders() throws IOException, GeneralSecurityException {
        List<File> googleRootFolders = getGoogleRootFolders();
        for (File folder : googleRootFolders) {

            System.out.println("Folder ID: " + folder.getId() + " --- Name: " + folder.getName());
        }
        return "Lista de Sub Directorios";
    }

    @GetMapping(value = "/folder/name/{name}")
    public String getSubFolderByName( @PathVariable("name") String name) throws IOException, GeneralSecurityException {
        List<File> rootGoogleFolders = getGoogleRootFoldersByName(name);
        for (File folder : rootGoogleFolders) {

            System.out.println("Folder ID: " + folder.getId() + " --- Name: " + folder.getName());
        }
        return "Lista de Sub Directorios por nombre";
    }

    @GetMapping(value = "/file/name/{name}")
    public String getFileByName( @PathVariable("name") String name) throws IOException, GeneralSecurityException {
        List<File> rootGoogleFolders = getGoogleFilesByName(name);
        for (File folder : rootGoogleFolders) {

            System.out.println("ID:"+folder.getId()+" --- Mime Type: " + folder.getMimeType() + " --- Name: " + folder.getName());
        }

        System.out.println("Done!");
        return "Archivo por nombre";
    }

    @PostMapping(path = "/folder", consumes = "application/json", produces = "application/json")
    public void createFolder(@RequestBody FolderDto folderDto) throws IOException, GeneralSecurityException {

        File folder = createGoogleFolder(folderDto.getFolderIdParent(), folderDto.getFolderName());

        System.out.println("Created folder with id= "+ folder.getId());
        System.out.println("                    name= "+ folder.getName());

        System.out.println("Done!");

    }

    @PostMapping(path = "/file/share", consumes = "application/json", produces = "application/json")
    public void shareFile(String googleFileId1, String googleEmail) throws IOException, GeneralSecurityException {

        // Share for a User
        createPermissionForEmail(googleFileId1, googleEmail);

//        String googleFileId2 = "some-google-file-id-2";

        // Share for everyone
//        createPublicPermission(googleFileId2);

        System.out.println("Done!");

    }
}
