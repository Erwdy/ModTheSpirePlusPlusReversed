package com.evacipated.cardcrawl.modthespire.draco.mods;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamUGCDetails;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.steam.SteamSearch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

public class DModUtils {
  public static ModInfo[] getAllMods(List<DSteamUGCDetails> workshopInfos) {
    List<ModInfo> modInfos = new ArrayList<>();
    if (Loader.STS_BETA)
      for (File f : Loader.getAllModFiles(Loader.MOD_DIR + Loader.BETA_SUBDIR)) {
        ModInfo info = ModInfo.ReadModInfo(f);
        if (info != null && 
          modInfos.stream().noneMatch(i -> (i.ID == null || i.ID.equals(info.ID))))
          modInfos.add(info); 
      }  
    for (File f : Loader.getAllModFiles(Loader.MOD_DIR)) {
      ModInfo info = ModInfo.ReadModInfo(f);
      if (info != null && 
        modInfos.stream().noneMatch(i -> (i.ID == null || i.ID.equals(info.ID))))
        modInfos.add(info); 
    } 
    BiConsumer<File, Boolean> lambda = (f, beta) -> {
        ModInfo info = ModInfo.ReadModInfo(f);
        if (info != null) {
          info.UpdateJSON = null;
          info.isWorkshop = true;
          boolean doAdd = true;
          Iterator<ModInfo> it = modInfos.iterator();
          while (it.hasNext()) {
            ModInfo modInfo = it.next();
            if (modInfo.ID != null && modInfo.ID.equals(info.ID)) {
              if (modInfo.ModVersion == null || info.ModVersion == null) {
                doAdd = false;
                break;
              } 
              if (info.ModVersion.isGreaterThan(modInfo.ModVersion)) {
                it.remove();
                continue;
              } 
              doAdd = false;
              break;
            } 
          } 
          if (doAdd)
            modInfos.add(info); 
        } 
      };
    for (DSteamUGCDetails workshopInfo : workshopInfos) {
      String installPath = workshopInfo.modInstallPath;
      for (File f : Loader.getAllModFiles(installPath))
        lambda.accept(f, Boolean.valueOf(false)); 
      if (Loader.STS_BETA)
        for (File f : Loader.getAllModFiles(Paths.get(installPath, new String[] { Loader.BETA_SUBDIR }).toString()))
          lambda.accept(f, Boolean.valueOf(true));  
    } 
    modInfos.sort(Comparator.comparing(m -> m.Name));
    return modInfos.<ModInfo>toArray(new ModInfo[0]);
  }
  
  public static List<DSteamUGCDetails> loadCachedWorkshopData() {
    List<DSteamUGCDetails> toReturn = new ArrayList<>();
    try {
      String path = SpireConfig.makeFilePath(null, "DWorkshopInfo", "json");
      if ((new File(path)).isFile()) {
        String data = new String(Files.readAllBytes(Paths.get(path, new String[0])));
        Gson gson = new Gson();
        Type type = (new TypeToken<List<SteamSearch.WorkshopInfo>>() {
          
          }).getType();
        toReturn = (List<DSteamUGCDetails>)gson.fromJson(data, type);
        return toReturn;
      } 
    } catch (Exception exception) {}
    return new ArrayList<>();
  }
  
  public static ModInfo GetModInfoForId(String ID) {
    for (ModInfo modInfo : Loader.ALLMODINFOS) {
      if (modInfo.ID.equals(ID))
        return modInfo; 
    } 
    return null;
  }
  
  public static void cacheWorkshopData(List<DSteamUGCDetails> workshopInfos) {
    if (workshopInfos.isEmpty())
      return; 
    try {
      String pathUpdated = SpireConfig.makeFilePath(null, "WorkshopUpdated", "json");
      String pathLocations = SpireConfig.makeFilePath(null, "WorkshopLocations", "json");
      Files.deleteIfExists(Paths.get(pathUpdated, new String[0]));
      Files.deleteIfExists(Paths.get(pathLocations, new String[0]));
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      String data = gson.toJson(workshopInfos);
      Files.write(Paths.get(SpireConfig.makeFilePath(null, "DWorkshopInfo", "json"), new String[0]), data.getBytes(), new java.nio.file.OpenOption[0]);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}
