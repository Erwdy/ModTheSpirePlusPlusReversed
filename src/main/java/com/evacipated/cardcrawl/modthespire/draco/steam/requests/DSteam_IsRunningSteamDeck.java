package com.evacipated.cardcrawl.modthespire.draco.steam.requests;

import com.codedisaster.steamworks.SteamUtils;
import com.evacipated.cardcrawl.modthespire.draco.steam.DSteam_Common;

public class DSteam_IsRunningSteamDeck extends DSteam_Common {
  public static void main(String[] args) {
    if (!init())
      return; 
    SteamUtils utils = new SteamUtils(() -> {
        
        });
    sendToMainApp(Boolean.valueOf(utils.isSteamRunningOnSteamDeck()));
    shutdown();
  }
}
