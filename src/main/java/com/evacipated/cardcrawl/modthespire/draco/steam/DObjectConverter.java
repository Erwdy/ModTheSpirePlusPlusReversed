package com.evacipated.cardcrawl.modthespire.draco.steam;

import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamPublishedFileID;
import com.codedisaster.steamworks.SteamUGC;
import com.codedisaster.steamworks.SteamUGCDetails;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamPublishedFileID;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamUGCDetails;

public class DObjectConverter {
  public static DSteamUGCDetails ToD_SteamUGCDetails(SteamUGCDetails steamUGCDetails, SteamUGC workshop) {
    DSteamUGCDetails toReturn = new DSteamUGCDetails();
    toReturn.publishedFileID = ToD_SteamPublishedFileID(steamUGCDetails.getPublishedFileID());
    toReturn.result = steamUGCDetails.getResult().name();
    toReturn.fileType = steamUGCDetails.getFileType().name();
    toReturn.title = steamUGCDetails.getTitle();
    toReturn.description = steamUGCDetails.getDescription();
    toReturn.ownerID = SteamNativeHandle.getNativeHandle((SteamNativeHandle)steamUGCDetails.getOwnerID());
    toReturn.timeCreated = steamUGCDetails.getTimeCreated();
    toReturn.timeUpdated = steamUGCDetails.getTimeUpdated();
    toReturn.tagsTruncated = steamUGCDetails.areTagsTruncated();
    toReturn.tags = steamUGCDetails.getTags();
    toReturn.fileHandle = SteamNativeHandle.getNativeHandle((SteamNativeHandle)steamUGCDetails.getFileHandle());
    toReturn.previewFileHandle = SteamNativeHandle.getNativeHandle((SteamNativeHandle)steamUGCDetails.getPreviewFileHandle());
    toReturn.fileName = steamUGCDetails.getFileName();
    toReturn.fileSize = steamUGCDetails.getFileSize();
    toReturn.previewFileSize = steamUGCDetails.getPreviewFileSize();
    toReturn.url = steamUGCDetails.getURL();
    toReturn.votesUp = steamUGCDetails.getVotesUp();
    toReturn.votesDown = steamUGCDetails.getVotesDown();
    SteamUGC.ItemInstallInfo info = new SteamUGC.ItemInstallInfo();
    workshop.getItemInstallInfo(steamUGCDetails.getPublishedFileID(), info);
    toReturn.modInstallPath = info.getFolder();
    return toReturn;
  }
  
  public static DSteamPublishedFileID ToD_SteamPublishedFileID(SteamPublishedFileID publishedFileID) {
    DSteamPublishedFileID toReturn = new DSteamPublishedFileID();
    toReturn.handle = SteamNativeHandle.getNativeHandle((SteamNativeHandle)publishedFileID);
    return toReturn;
  }
  
  public static SteamPublishedFileID FromD_SteamPublishedFileID(DSteamPublishedFileID publishedFileID) {
    return new SteamPublishedFileID(publishedFileID.handle);
  }
}
