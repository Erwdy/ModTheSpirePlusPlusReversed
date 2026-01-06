package com.evacipated.cardcrawl.modthespire.draco.mods;

import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamPublishedFileID;
import java.io.Serializable;
import java.util.ArrayList;

public class LimitedModInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  
  public String ID;
  
  public String Name;
  
  public String ModVersion;
  
  private Long ModId;
  
  public LimitedModInfo(ModInfo mod) {
    this.ID = mod.ID;
    this.Name = mod.Name;
    this.ModVersion = mod.ModVersion.toString();
    if (mod.steamWorkshopDetails != null)
      this.ModId = Long.valueOf(mod.steamWorkshopDetails.publishedFileID.GetHandle()); 
  }
  
  public static LimitedModInfo[] ArrayFromModInfos(ModInfo[] infos) {
    LimitedModInfo[] limitedModInfos = new LimitedModInfo[infos.length];
    for (int i = 0; i < limitedModInfos.length; i++)
      limitedModInfos[i] = new LimitedModInfo(infos[i]); 
    return limitedModInfos;
  }
  
  public static ArrayList<LimitedModInfo> ListFromModInfos(ArrayList<ModInfo> infos) {
    ArrayList<LimitedModInfo> toReturn = new ArrayList<>();
    infos.forEach(i -> toReturn.add(new LimitedModInfo(i)));
    return toReturn;
  }
  
  public String GetModPublicId() {
    if (this.ModId == null)
      return ""; 
    return String.valueOf(this.ModId);
  }
  
  public DSteamPublishedFileID GetPublishedID() {
    if (this.ModId == null)
      return null; 
    return new DSteamPublishedFileID(this.ModId.longValue());
  }
}
