package com.evacipated.cardcrawl.modthespire.draco.steam.objects;

public class DSteamUGCDetails {
  public DSteamPublishedFileID publishedFileID;
  
  public String result;
  
  public String fileType;
  
  public String title;
  
  public String description;
  
  public long ownerID;
  
  public int timeCreated;
  
  public int timeUpdated;
  
  public boolean tagsTruncated;
  
  public String tags;
  
  public long fileHandle;
  
  public long previewFileHandle;
  
  public String fileName;
  
  public int fileSize;
  
  public int previewFileSize;
  
  public String url;
  
  public int votesUp;
  
  public int votesDown;
  
  public String modInstallPath;
  
  public String getDescription() {
    return this.description;
  }
  
  public String getTags() {
    return this.tags;
  }
  
  public String getURL() {
    return this.url;
  }
}
