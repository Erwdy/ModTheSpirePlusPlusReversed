package com.evacipated.cardcrawl.modthespire.draco.mods;

import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.ModList;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DModList {
  public static ArrayList<ModInfo> ToModInfo(ModList modlist, ModInfo[] info) {
    ArrayList<ModInfo> toReturn = new ArrayList<>();
    File[] modFiles = new File[info.length];
    int i;
    for (i = 0; i < info.length; i++) {
      if ((info[i]).jarURL == null) {
        System.out.println("ERROR: jarURL is null?: " + (info[i]).Name);
      } else {
        try {
          modFiles[i] = new File((info[i]).jarURL.toURI());
        } catch (URISyntaxException e) {
          System.out.println("Problem with: " + (info[i]).jarURL);
          e.printStackTrace();
        } 
      } 
    } 
    for (i = 0; i < modlist.mods.size(); i++) {
      for (int j = 0; j < modFiles.length; j++) {
        if (((String)modlist.mods.get(i)).equals(modFiles[j].getName()))
          toReturn.add(info[j]); 
      } 
    } 
    return toReturn;
  }
  
  public static void rename(String oldName, String newName) {
    ModList.saveData.defaultList = newName;
    Map<String, List<String>> newList = new HashMap<>(ModList.saveData.lists);
    ModList.saveData.lists.clear();
    for (Map.Entry<String, List<String>> entry : newList.entrySet()) {
      if (((String)entry.getKey()).equals(oldName)) {
        ModList.saveData.lists.put(newName, entry.getValue());
        continue;
      } 
      ModList.saveData.lists.put(entry.getKey(), entry.getValue());
    } 
    ModList.save();
  }
}
