package com.evacipated.cardcrawl.modthespire.draco.mods;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModList;
import com.evacipated.cardcrawl.modthespire.draco.steam.DSteamUtil;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamPublishedFileID;
import com.evacipated.cardcrawl.modthespire.draco.ui.DUIHelp;
import com.evacipated.cardcrawl.modthespire.draco.ui.objects.UserProfile;
import com.evacipated.cardcrawl.modthespire.ui.ModSelectWindow;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class DModSelectWindow {
  public static final String MTS_PP_VER = "d4";
  
  public static final Image APP_ICON_NEW = Toolkit.getDefaultToolkit().createImage(ModSelectWindow.class.getResource("/assets/newIcon.png"));
  
  public static Font FONT_KREON = DUIHelp.loadFont("/fonts/Kreon-Regular.ttf", 30.0F);
  
  public static JLabel visitOnSteam;
  
  public static JLabel joinDiscord;
  
  public static JLabel supportOnPatreon;
  
  public static String cachedModlist = "";
  
  public static void ImportModList(LimitedModInfo[] mods) {
    ArrayList<LimitedModInfo> notFoundMods = new ArrayList<>();
    for (LimitedModInfo i : mods) {
      if (!DJModPanelCheckBoxList.hasMod(Loader.ex.modList, i.ID))
        notFoundMods.add(i); 
    } 
    if (!notFoundMods.isEmpty()) {
      String message = "The following mods are not downloaded: ";
      for (LimitedModInfo i : notFoundMods)
        message = message + "\n • " + i.Name; 
      message = message + "\nWould you like to download the mods from Steam?";
      String[] options = { "Download All", "Ignore" };
      ArrayList<DSteamPublishedFileID> failedDownloads = new ArrayList<>();
      int result = DUIHelp.OpenSimpleButtonDialog((Component)Loader.ex, "Confirm", message, (Object[])options, options[0]);
      if (result == 0) {
        ArrayList<LimitedModInfo> steamIds = new ArrayList<>();
        for (LimitedModInfo i : notFoundMods) {
          DSteamPublishedFileID fileId = i.GetPublishedID();
          if (fileId != null)
            steamIds.add(i); 
        } 
        if (!steamIds.isEmpty())
          failedDownloads = DSteamUtil.DownloadModsAndOpenWindow(Loader.ex, steamIds); 
        RefreshModInfos();
      } 
      if (!failedDownloads.isEmpty()) {
        message = "Could not download following mods: ";
        for (DSteamPublishedFileID failedDownload : failedDownloads) {
          for (LimitedModInfo lmi : notFoundMods) {
            if (lmi.GetPublishedID().equals(failedDownload))
              message = message + "\n • " + lmi.Name; 
          } 
        } 
        JOptionPane.showMessageDialog((Component)Loader.ex, message);
      } 
    } 
    notFoundMods.clear();
    DJModPanelCheckBoxList.deselectAllMods(Loader.ex.modList);
    for (int j = 0; j < mods.length; j++) {
      LimitedModInfo i = mods[j];
      if (DJModPanelCheckBoxList.selectMod(Loader.ex.modList, i.ID)) {
        DJModPanelCheckBoxList.moveModToSlot(Loader.ex.modList, i.ID, j - notFoundMods.size());
      } else {
        notFoundMods.add(i);
      } 
    } 
    if (!notFoundMods.isEmpty()) {
      String message = "Could not enable following mods: ";
      for (LimitedModInfo lmi : notFoundMods)
        message = message + "\n • " + lmi.Name; 
      JOptionPane.showMessageDialog((Component)Loader.ex, message);
    } 
    SaveCurrentModlist();
  }
  
  public static void RefreshModInfos() {
    DLoader.RefreshModList();
    Loader.ex.info = Loader.ALLMODINFOS;
    ModList mods = ModList.loadModLists();
    mods.loadModsInOrder((DefaultListModel)Loader.ex.modList.getModel(), Loader.ex.info, Loader.ex.modList);
  }
  
  public static String[] GetProfileListAsArray() {
    ComboBoxModel<UserProfile> dataModel = Loader.ex.dProfilesList.getModel();
    ArrayList<String> itemsList = new ArrayList<>();
    for (int i = 0; i < Loader.ex.dProfilesList.getItemCount(); i++) {
      UserProfile p = dataModel.getElementAt(i);
      if (!p.properties.noSave)
        itemsList.add(p.getId()); 
    } 
    return itemsList.<String>toArray(new String[0]);
  }
  
  public static void Play() {
    Loader.ex.playBtn.doClick();
  }
  
  public static void SaveCurrentModlist() {
    SaveModlistAs(ModList.getDefaultList());
  }
  
  public static void SaveModlistAs(String id) {
    if (id.isEmpty())
      return; 
    if (!Loader.ex.modList.properties.noSave)
      ModList.save(id, Loader.ex.modList.getCheckedMods()); 
  }
}
