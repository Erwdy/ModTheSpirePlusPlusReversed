package com.evacipated.cardcrawl.modthespire.draco.steam;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamUGC;
import com.google.gson.Gson;

public abstract class DSteam_Common {
  protected static final int appId = 646570;
  
  protected static boolean kill;
  
  protected static SteamUGC workshop;
  
  protected static boolean init() {
    try {
      try {
        SteamAPI.loadLibraries();
      } catch (NoSuchMethodError noSuchMethodError) {}
      if (!SteamAPI.init()) {
        System.err.println("Could not connect to Steam. Is it running?");
        System.exit(1);
      } 
    } catch (SteamException e) {
      System.err.println(e.getMessage());
      System.exit(2);
    } 
    return SteamAPI.isSteamRunning(true);
  }
  
  protected static void shutdown() {
    SteamAPI.shutdown();
  }
  
  protected static void sendToMainApp(String s) {
    System.out.println(s);
  }
  
  protected static void sendToMainApp(Object object) {
    sendToMainApp((new Gson()).toJson(object));
  }
  
  protected void update() {}
}
