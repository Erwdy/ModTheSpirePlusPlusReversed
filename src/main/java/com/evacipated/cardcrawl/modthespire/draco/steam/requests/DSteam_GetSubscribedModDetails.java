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
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamUGCDetails;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DSteam_GetSubscribedModDetails extends DSteam_Common {
  public static List<DSteamUGCDetails> detailsToReturn = Collections.synchronizedList(new ArrayList<>());
  
  public static int receivedQueryResults = 0;
  
  public static void main(String[] args) {
    if (!init())
      return; 
    workshop = new SteamUGC(new Callback());
    int items = workshop.getNumSubscribedItems();
    SteamPublishedFileID[] publishedFileIDS = new SteamPublishedFileID[items];
    workshop.getSubscribedItems(publishedFileIDS);
    SteamUGCQuery query = workshop.createQueryUGCDetailsRequest(Arrays.asList(publishedFileIDS));
    workshop.sendQueryUGCRequest(query);
    while (SteamAPI.isSteamRunning()) {
      try {
        Thread.sleep(66L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } 
      SteamAPI.runCallbacks();
      if (kill)
        break; 
    } 
    shutdown();
  }
  
  private static class Callback implements SteamUGCCallback {
    private Callback() {}
    
    public void onUGCQueryCompleted(SteamUGCQuery query, int numResultsReturned, int totalMatchingResults, boolean isCachedData, SteamResult result) {
      if (query.isValid())
        for (int i = 0; i < numResultsReturned; i++) {
          SteamUGCDetails details = new SteamUGCDetails();
          if (DSteam_GetSubscribedModDetails.workshop.getQueryUGCResult(query, i, details)) {
            Collection<SteamUGC.ItemState> state = DSteam_GetSubscribedModDetails.workshop.getItemState(details.getPublishedFileID());
            if (state.contains(SteamUGC.ItemState.Installed))
              DSteam_GetSubscribedModDetails.detailsToReturn.add(DObjectConverter.ToD_SteamUGCDetails(details, DSteam_GetSubscribedModDetails.workshop)); 
          } 
        }  
      DSteam_GetSubscribedModDetails.receivedQueryResults += numResultsReturned;
      if (DSteam_GetSubscribedModDetails.receivedQueryResults >= totalMatchingResults) {
        DSteam_GetSubscribedModDetails.sendToMainApp(DSteam_GetSubscribedModDetails.detailsToReturn);
        DSteam_GetSubscribedModDetails.kill = true;
      } 
    }
    
    public void onSubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {}
    
    public void onUnsubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {}
    
    public void onRequestUGCDetails(SteamUGCDetails details, SteamResult result) {}
    
    public void onCreateItem(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {}
    
    public void onSubmitItemUpdate(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {}
    
    public void onDownloadItemResult(int appID, SteamPublishedFileID publishedFileID, SteamResult result) {}
    
    public void onUserFavoriteItemsListChanged(SteamPublishedFileID publishedFileID, boolean wasAddRequest, SteamResult result) {}
    
    public void onSetUserItemVote(SteamPublishedFileID publishedFileID, boolean voteUp, SteamResult result) {}
    
    public void onGetUserItemVote(SteamPublishedFileID publishedFileID, boolean votedUp, boolean votedDown, boolean voteSkipped, SteamResult result) {}
    
    public void onStartPlaytimeTracking(SteamResult result) {}
    
    public void onStopPlaytimeTracking(SteamResult result) {}
    
    public void onStopPlaytimeTrackingForAllItems(SteamResult result) {}
    
    public void onDeleteItem(SteamPublishedFileID publishedFileID, SteamResult result) {}
  }
}
