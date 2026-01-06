package com.evacipated.cardcrawl.modthespire;

import com.evacipated.cardcrawl.modthespire.draco.modlist.ModListProperties;
import com.evacipated.cardcrawl.modthespire.lib.ConfigUtils;
import com.evacipated.cardcrawl.modthespire.ui.JModPanelCheckBoxList;
import com.evacipated.cardcrawl.modthespire.ui.ModPanel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ModList {
  private static String OLD_CFG_FILE = ConfigUtils.CONFIG_DIR + File.separator + "mod_order.xml";
  
  private static String CFG_FILE = ConfigUtils.CONFIG_DIR + File.separator + "mod_lists.json";
  
  public static String DEFAULT_LIST = "<Default>";
  
  public static ModListSaveData saveData = null;
  
  private String name;
  
  public List<String> mods;
  
  public ModListProperties properties;
  
  public static class ModListSaveData {
    public String defaultList = ModList.DEFAULT_LIST;
    
    public Map<String, List<String>> lists = new HashMap<>();
  }
  
  private static class ModDescriptor {
    public File mod;
    
    public ModInfo info;
    
    public boolean checked;
    
    public ModDescriptor(File mod, ModInfo info, boolean checked) {
      this.mod = mod;
      this.info = info;
      this.checked = checked;
    }
  }
  
  public static String getDefaultList() {
    if (saveData == null)
      return DEFAULT_LIST; 
    return saveData.defaultList;
  }
  
  public static Collection<String> getAllModListNames() {
    return saveData.lists.keySet();
  }
  
  public static ModList loadModLists() {
    File oldConfig = new File(OLD_CFG_FILE);
    if (oldConfig.exists()) {
      convertOldConfig(oldConfig);
    } else if (Files.exists(Paths.get(CFG_FILE, new String[0]), new java.nio.file.LinkOption[0])) {
      try {
        String data = new String(Files.readAllBytes(Paths.get(CFG_FILE, new String[0])));
        Gson gson = new Gson();
        saveData = (ModListSaveData)gson.fromJson(data, ModListSaveData.class);
      } catch (JsonSyntaxException e) {
        saveData = new ModListSaveData();
      } catch (IOException e) {
        saveData = new ModListSaveData();
        e.printStackTrace();
      } 
    } else {
      saveData = new ModListSaveData();
    } 
    if (saveData == null)
      saveData = new ModListSaveData(); 
    return new ModList(saveData.defaultList);
  }
  
  public static void convertOldConfig(File oldConfig) {
    try {
      Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(oldConfig));
      NodeList modsFromCfg = d.getElementsByTagName("mod");
      saveData = new ModListSaveData();
      List<String> mods = new ArrayList<>();
      for (int i = 0; i < modsFromCfg.getLength(); i++)
        mods.add(modsFromCfg.item(i).getTextContent()); 
      saveData.lists.put(DEFAULT_LIST, mods);
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      String data = gson.toJson(saveData);
      Files.write(Paths.get(CFG_FILE, new String[0]), data.getBytes(), new java.nio.file.OpenOption[0]);
      oldConfig.delete();
    } catch (SAXException|javax.xml.parsers.ParserConfigurationException|IOException e) {
      e.printStackTrace();
    } 
  }
  
  public ModList(String list) {
    this(list, new ModListProperties());
  }
  
  public ModList(String list, ModListProperties properties) {
    this.properties = properties;
    if (saveData == null)
      loadModLists(); 
    this.name = list;
    if (!properties.noSave)
      saveData.lists.putIfAbsent(this.name, Collections.emptyList()); 
    this.mods = saveData.lists.getOrDefault(this.name, Collections.emptyList());
  }
  
  public String getName() {
    return this.name;
  }
  
  public void loadModsInOrder(DefaultListModel<ModPanel> model, ModInfo[] info, JModPanelCheckBoxList parent) {
    model.clear();
    parent.properties = this.properties;
    File[] modFiles = new File[info.length];
    for (int i = 0; i < info.length; i++) {
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
    List<ModDescriptor> loadOrder = new ArrayList<>();
    List<Integer> foundMods = new ArrayList<>();
    int j;
    for (j = 0; j < this.mods.size(); j++) {
      for (int k = 0; k < modFiles.length; k++) {
        if (((String)this.mods.get(j)).equals(modFiles[k].getName())) {
          loadOrder.add(new ModDescriptor(modFiles[k], info[k], true));
          foundMods.add(Integer.valueOf(j));
        } 
      } 
    } 
    for (j = 0; j < this.mods.size(); j++) {
      if (!foundMods.contains(Integer.valueOf(j)))
        System.out.println("could not find mod: " + (String)this.mods.get(j) + " even though it was specified in load order"); 
    } 
    for (j = 0; j < modFiles.length; j++) {
      boolean found = false;
      for (int k = 0; k < loadOrder.size(); k++) {
        ModDescriptor descriptor = loadOrder.get(k);
        if (descriptor.mod == modFiles[j] && descriptor.info == info[j])
          found = true; 
      } 
      if (!found)
        loadOrder.add(new ModDescriptor(modFiles[j], info[j], false)); 
    } 
    for (ModDescriptor descriptor : loadOrder) {
      ModPanel toAdd = new ModPanel(descriptor.info, descriptor.mod, parent);
      if (toAdd.checkBox.isEnabled())
        toAdd.checkBox.setSelected(descriptor.checked); 
      model.addElement(toAdd);
    } 
  }
  
  public static void save(String list, File[] modFiles) {
    saveData.defaultList = list;
    List<String> modList = new ArrayList<>();
    for (File modFile : modFiles)
      modList.add(modFile.getName()); 
    saveData.lists.put(list, modList);
    save();
  }
  
  public static void save() {
    try {
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      String data = gson.toJson(saveData);
      Files.write(Paths.get(CFG_FILE, new String[0]), data.getBytes(), new java.nio.file.OpenOption[0]);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public static void delete(String list) {
    saveData.defaultList = DEFAULT_LIST;
    saveData.lists.remove(list);
    save();
  }
}
