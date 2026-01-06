package com.evacipated.cardcrawl.modthespire.draco.steam.requests;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamPublishedFileID;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUGC;
import com.codedisaster.steamworks.SteamUGCCallback;
import com.codedisaster.steamworks.SteamUGCDetails;
import com.codedisaster.steamworks.SteamUGCQuery;
import com.evacipated.cardcrawl.modthespire.draco.steam.DObjectConverter;
import com.evacipated.cardcrawl.modthespire.draco.steam.DSteam_Common;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamPublishedFileID;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DSteam_SubscribeAndDownloadItems extends DSteam_Common {
  public static List<DSteamPublishedFileID> pendingDownloads = Collections.synchronizedList(new ArrayList<>());
  
  public static List<DSteamPublishedFileID> completedDownloads = Collections.synchronizedList(new ArrayList<>());
  
  public static List<DSteamPublishedFileID> failedDownloads = Collections.synchronizedList(new ArrayList<>());
  
  public static void main(String[] args) {
    if (!init())
      return; 
    workshop = new SteamUGC(new Callback());
    if (args.length != 1)
      return; 
    String request = args[0];
    ArrayList<DSteamPublishedFileID> itemIds = (ArrayList<DSteamPublishedFileID>)(new Gson()).fromJson(request, (new TypeToken<ArrayList<DSteamPublishedFileID>>() {
        
        }).getType());
    for (DSteamPublishedFileID did : itemIds) {
      sendToMainApp("DOWN" + did.handle);
      SteamPublishedFileID id = DObjectConverter.FromD_SteamPublishedFileID(did);
      boolean downloading = SubscribeAndDownloadItem(id);
      if (downloading) {
        pendingDownloads.add(did);
      } else {
        completedDownloads.add(did);
      } 
      while (SteamAPI.isSteamRunning()) {
        try {
          Thread.sleep(66L);
        } catch (InterruptedException e) {
          e.printStackTrace();
        } 
        SteamAPI.runCallbacks();
        SteamUGC.ItemDownloadInfo downloadInfo = new SteamUGC.ItemDownloadInfo();
        workshop.getItemDownloadInfo(id, downloadInfo);
        long top = downloadInfo.getBytesDownloaded() * 100L;
        long bottom = downloadInfo.getBytesTotal();
        if (bottom == 0L) {
          sendToMainApp("PERC100");
        } else {
          sendToMainApp("PERC" + (top / bottom));
        } 
        if (pendingDownloads.isEmpty())
          break; 
      } 
    } 
    shutdown();
  }
  
  private static boolean SubscribeAndDownloadItem(SteamPublishedFileID itemId) {
    if (workshop == null)
      init(); 
    Collection<SteamUGC.ItemState> states = workshop.getItemState(itemId);
    if (!states.contains(SteamUGC.ItemState.Subscribed))
      workshop.subscribeItem(itemId); 
    return workshop.downloadItem(itemId, true);
  }
  
  private static class Callback implements SteamUGCCallback {
    private Callback() {}
    
    public void onUGCQueryCompleted(SteamUGCQuery query, int numResultsReturned, int totalMatchingResults, boolean isCachedData, SteamResult result) {}
    
    public void onSubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {}
    
    public void onUnsubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {}
    
    public void onRequestUGCDetails(SteamUGCDetails details, SteamResult result) {}
    
    public void onCreateItem(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {}
    
    public void onSubmitItemUpdate(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {}
    
    public void onDownloadItemResult(int appID, SteamPublishedFileID publishedFileID, SteamResult result) {
      boolean removed = DSteam_SubscribeAndDownloadItems.pendingDownloads.remove(DObjectConverter.ToD_SteamPublishedFileID(publishedFileID));
      if (result == SteamResult.OK) {
        if (removed)
          DSteam_SubscribeAndDownloadItems.completedDownloads.add(DObjectConverter.ToD_SteamPublishedFileID(publishedFileID)); 
      } else if (removed) {
        DSteam_SubscribeAndDownloadItems.failedDownloads.add(DObjectConverter.ToD_SteamPublishedFileID(publishedFileID));
      } 
    }
    
    public void onUserFavoriteItemsListChanged(SteamPublishedFileID publishedFileID, boolean wasAddRequest, SteamResult result) {}
    
    public void onSetUserItemVote(SteamPublishedFileID publishedFileID, boolean voteUp, SteamResult result) {}
    
    public void onGetUserItemVote(SteamPublishedFileID publishedFileID, boolean votedUp, boolean votedDown, boolean voteSkipped, SteamResult result) {}
    
    public void onStartPlaytimeTracking(SteamResult result) {}
    
    public void onStopPlaytimeTracking(SteamResult result) {}
    
    public void onStopPlaytimeTrackingForAllItems(SteamResult result) {}
    
    public void onDeleteItem(SteamPublishedFileID publishedFileID, SteamResult result) {}
  }
}
