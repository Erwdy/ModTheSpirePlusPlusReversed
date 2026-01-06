package com.evacipated.cardcrawl.modthespire.draco.mods;

import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.net.URL;

public class ModInfoExtended {
  @SerializedName("discord_url")
  public String discord_url = "";
  
  @SerializedName("patreon_url")
  public String patreon_url = "";
  
  @SerializedName("loading_phrases")
  public String[] loading_phrases = new String[0];
  
  public void SanitizeData() {
    if (this.discord_url != null && !this.discord_url.isEmpty() && 
      !isValidDiscordLink(this.discord_url))
      this.discord_url = ""; 
    if (this.patreon_url != null && !this.patreon_url.isEmpty() && 
      !isValidPatreonLink(this.patreon_url))
      this.patreon_url = ""; 
  }
  
  private static boolean isValidDiscordLink(String link) {
    try {
      URL url = new URL(link);
      if (!url.getHost().equals("discord.com") && !url.getHost().equals("discord.gg") && !url.getHost().equals("www.discord.com") && !url.getHost().equals("www.discord.gg"))
        return false; 
      return true;
    } catch (IOException e) {
      return false;
    } 
  }
  
  private static boolean isValidPatreonLink(String link) {
    try {
      URL url = new URL(link);
      if (!url.getHost().equals("patreon.com") && !url.getHost().equals("www.patreon.com"))
        return false; 
      return true;
    } catch (IOException e) {
      return false;
    } 
  }
}
