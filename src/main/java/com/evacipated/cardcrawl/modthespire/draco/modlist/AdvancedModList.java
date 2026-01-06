package com.evacipated.cardcrawl.modthespire.draco.modlist;

import com.evacipated.cardcrawl.modthespire.draco.mods.LimitedModInfo;
import com.google.gson.Gson;
import java.io.Serializable;
import java.util.ArrayList;

public class AdvancedModList extends ShareableModList implements Serializable {
  private static final long serialVersionUID = 1L;
  
  public LimitedModInfo[] requiredMods;
  
  public ArrayList<String> allowedMods = new ArrayList<>();
  
  public ArrayList<LimitedModInfo> forbiddenMods = new ArrayList<>();
  
  public boolean IsModRequired(String modId) {
    for (LimitedModInfo i : this.requiredMods) {
      if (i.ID.equals(modId))
        return true; 
    } 
    return false;
  }
  
  public String toJson() {
    return (new Gson()).toJson(this);
  }
  
  public static AdvancedModList fromJson(String input) {
    return (AdvancedModList)(new Gson()).fromJson(input, AdvancedModList.class);
  }
}
