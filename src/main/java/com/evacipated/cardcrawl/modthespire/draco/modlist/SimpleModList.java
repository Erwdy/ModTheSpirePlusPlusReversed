package com.evacipated.cardcrawl.modthespire.draco.modlist;

import com.evacipated.cardcrawl.modthespire.draco.mods.LimitedModInfo;
import java.io.Serializable;

public class SimpleModList extends ShareableModList implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private LimitedModInfo[] mods;
  
  public void SetMods(LimitedModInfo[] mods) {
    this.mods = mods;
  }
  
  public LimitedModInfo[] GetMods() {
    return this.mods;
  }
}
