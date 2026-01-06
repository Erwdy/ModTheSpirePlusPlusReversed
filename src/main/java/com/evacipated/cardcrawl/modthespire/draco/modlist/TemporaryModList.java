package com.evacipated.cardcrawl.modthespire.draco.modlist;

import com.evacipated.cardcrawl.modthespire.ModList;
import java.util.List;
import java.util.UUID;

public class TemporaryModList extends ModList {
  public TemporaryModList(List<String> mods) {
    super("dtemp" + UUID.randomUUID(), new ModListProperties(true));
    this.mods = mods;
  }
}
