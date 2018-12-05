package com.autentia.helloworld.Dto;

public class FileDto {

    private  String folderIdParent;

    private String contentType;

    private  String customFileName;

    public String getFolderIdParent() {
        return folderIdParent;
    }

    public void setFolderIdParent(String folderIdParent) {
        this.folderIdParent = folderIdParent;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCustomFileName() {
        return customFileName;
    }

    public void setCustomFileName(String customFileName) {
        this.customFileName = customFileName;
    }
}
