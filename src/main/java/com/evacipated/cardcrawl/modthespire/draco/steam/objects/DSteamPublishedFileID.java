package com.evacipated.cardcrawl.modthespire.draco.steam.objects;

public class DSteamPublishedFileID {
  public long handle;
  
  public DSteamPublishedFileID() {}
  
  public DSteamPublishedFileID(long handle) {
    this.handle = handle;
  }
  
  public String ToPublicID() {
    return String.valueOf(this.handle);
  }
  
  public long GetHandle() {
    return this.handle;
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof DSteamPublishedFileID))
      return false; 
    return (this.handle == ((DSteamPublishedFileID)obj).handle);
  }
}
