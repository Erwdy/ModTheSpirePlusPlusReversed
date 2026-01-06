package com.evacipated.cardcrawl.modthespire.draco.util;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.draco.mods.DJModPanelCheckBoxList;
import com.evacipated.cardcrawl.modthespire.draco.steam.DSteamUtil;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamPublishedFileID;
import com.evacipated.cardcrawl.modthespire.ui.JModPanelCheckBoxList;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DracosUtil {
  public static void CopyToClipboard(String s) {
    StringSelection selection = new StringSelection(s);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(selection, selection);
  }
  
  public static ModInfo[] FileListToModInfo(File[] modJars) {
    ModInfo[] infos = new ModInfo[modJars.length];
    for (int i = 0; i < modJars.length; i++)
      infos[i] = ModInfo.ReadModInfo(modJars[i]); 
    return infos;
  }
  
  public static String FolderNameFromFileName(String filePath) {
    File file = new File(filePath);
    return file.getParentFile().getName();
  }
  
  public static String GetModPublicId(ModInfo info) {
    if (info.steamWorkshopDetails == null)
      return null; 
    return info.steamWorkshopDetails.publishedFileID.ToPublicID();
  }
  
  public static String GetTextFromURL(String urlString) {
    try {
      URL url = new URL(urlString);
      URLConnection connection = url.openConnection();
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = in.readLine()) != null) {
        sb.append(line);
        sb.append("\n");
      } 
      in.close();
      return sb.toString();
    } catch (Exception e) {
      return null;
    } 
  }
  
  public static ArrayList<String> GetTextAsArrayFromURL(String urlString) {
    try {
      URL url = new URL(urlString);
      URLConnection connection = url.openConnection();
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      ArrayList<String> lines = new ArrayList<>();
      String line;
      while ((line = in.readLine()) != null)
        lines.add(line); 
      in.close();
      return lines;
    } catch (Exception e) {
      return null;
    } 
  }
  
  public static ArrayList<String> GetTextAsArrayFromFile(String path) {
    ArrayList<String> lines = new ArrayList<>();
    try(InputStream is = Loader.ex.getClass().getResourceAsStream(path); 
        BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      String line;
      while ((line = reader.readLine()) != null)
        lines.add(line); 
    } catch (IOException e) {
      e.printStackTrace();
    } 
    return lines;
  }
  
  public static String GetTextFromFile(String path) {
    String text = "";
    for (String s : GetTextAsArrayFromFile(path))
      text = text + s + "\n"; 
    return text;
  }
  
  public static void SaveStringToFile(String text) {
    JFileChooser fileChooser = new JFileChooser();
    int returnValue = fileChooser.showSaveDialog(null);
    if (returnValue == 0) {
      File selectedFile = fileChooser.getSelectedFile();
      String filePath = selectedFile.getAbsolutePath();
      if (!filePath.endsWith(".mtspp")) {
        filePath = filePath + ".mtspp";
        selectedFile = new File(filePath);
      } 
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
        writer.write(text);
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
  }
  
  public static String LoadStringFromFile() {
    JFileChooser fileChooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter("MTSPP files", new String[] { "mtspp" });
    fileChooser.setFileFilter(filter);
    int returnValue = fileChooser.showOpenDialog(null);
    if (returnValue == 0) {
      File selectedFile = fileChooser.getSelectedFile();
      try {
        return new String(Files.readAllBytes(selectedFile.toPath()));
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
    return null;
  }
  
  public static void FullyRemoveMod(ModInfo mod) {
    if (mod.isWorkshop && mod.steamWorkshopDetails != null && mod.steamWorkshopDetails.publishedFileID != null) {
      ArrayList<DSteamPublishedFileID> toRemove = new ArrayList<>();
      toRemove.add(mod.steamWorkshopDetails.publishedFileID);
      DSteamUtil.Unsubscribe(toRemove);
    } else {
      try {
        Files.deleteIfExists(Paths.get(mod.jarURL.getPath(), new String[0]));
      } catch (Exception e) {
        JOptionPane.showMessageDialog((Component)Loader.ex, "Failed to remove mod due to " + e.getMessage() + "\n" + Arrays.toString((Object[])e.getStackTrace()));
      } 
    } 
    ArrayList<ModInfo> modInfos = new ArrayList<>(Arrays.asList(Loader.ALLMODINFOS));
    modInfos.removeIf(modInfo -> modInfo.ID.equals(mod.ID));
    Loader.ALLMODINFOS = new ModInfo[modInfos.size()];
    for (int j = 0; j < modInfos.size(); j++) {
      ModInfo modInfo = modInfos.get(j);
      Loader.ALLMODINFOS[j] = modInfo;
    } 
    JModPanelCheckBoxList mods = Loader.ex.modList;
    DJModPanelCheckBoxList.removeMod(mods, mod);
  }
}
