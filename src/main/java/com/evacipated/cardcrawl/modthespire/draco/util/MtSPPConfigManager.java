package com.evacipated.cardcrawl.modthespire.draco.util;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

public class MtSPPConfigManager {
  public static SpireConfig MTSPP_CONFIG;
  
  public static void init() {
    try {
      MTSPP_CONFIG = new SpireConfig(null, "ModTheSpirePlusPlus");
    } catch (Exception e) {
      System.err.println("Failed to initialize MTSPP Config due to " + e.getLocalizedMessage());
      e.printStackTrace();
    } 
  }
}
