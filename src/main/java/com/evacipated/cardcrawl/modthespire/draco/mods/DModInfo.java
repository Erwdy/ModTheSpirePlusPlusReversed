package com.evacipated.cardcrawl.modthespire.draco.mods;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;

public class DModInfo {
  public static ModInfoExtended loadExtendedModInfo(URLClassLoader loader, Gson gson) {
    ModInfoExtended modInfoExtended = null;
    InputStream in = loader.getResourceAsStream("ModTheSpire.json");
    if (in != null) {
      modInfoExtended = (ModInfoExtended)gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), ModInfoExtended.class);
      modInfoExtended.SanitizeData();
    } 
    if (modInfoExtended == null)
      modInfoExtended = new ModInfoExtended(); 
    try {
      in.close();
    } catch (Exception exception) {}
    return modInfoExtended;
  }
}
