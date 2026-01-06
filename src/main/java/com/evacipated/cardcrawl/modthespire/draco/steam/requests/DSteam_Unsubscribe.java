package com.evacipated.cardcrawl.modthespire.draco.steam.requests;

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

public class DSteam_Unsubscribe extends DSteam_Common {
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
      SteamPublishedFileID id = DObjectConverter.FromD_SteamPublishedFileID(did);
      workshop.unsubscribeItem(id);
    } 
    shutdown();
  }
  
  private static class Callback implements SteamUGCCallback {
    private Callback() {}
    
    public void onUGCQueryCompleted(SteamUGCQuery query, int numResultsReturned, int totalMatchingResults, boolean isCachedData, SteamResult result) {}
    
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
