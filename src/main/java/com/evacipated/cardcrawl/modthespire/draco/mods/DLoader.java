package com.evacipated.cardcrawl.modthespire.draco.mods;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.draco.modcompatibility.ModCompatibilityManager;
import com.evacipated.cardcrawl.modthespire.draco.modcompatibility.MtSModIntegration;
import com.evacipated.cardcrawl.modthespire.draco.modcompatibility.settings.TogetherInSpire;
import com.evacipated.cardcrawl.modthespire.draco.steam.DSteamUtil;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamPublishedFileID;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamUGCDetails;
import com.evacipated.cardcrawl.modthespire.draco.ui.DUIHelp;
import com.evacipated.cardcrawl.modthespire.draco.ui.objects.RaidUserProfile;
import com.evacipated.cardcrawl.modthespire.draco.util.DracosUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DLoader {
  public static void RefreshModList() {
    if (Loader.ex == null) {
      RefreshWorkshopInfos();
      RefreshModInfos();
    } 
    if (Loader.ex != null) {
      JDialog dialog = DUIHelp.CreateModalTextPopup((JFrame)Loader.ex, "Refreshing", "Refreshing mod list");
      SwingUtilities.invokeLater(() -> {
            RefreshWorkshopInfos();
            RefreshModInfos();
            dialog.dispose();
          });
      dialog.setVisible(true);
    } 
  }
  
  private static void RefreshWorkshopInfos() {
    List<DSteamUGCDetails> workshopInfos = new ArrayList<>();
    System.out.println("Searching for Workshop items...");
    workshopInfos = DSteamUtil.GetSubscribedModDetails();
    workshopInfos.removeIf(i -> (i.getTags().contains("tool") || i.getTags().contains("tools")));
    workshopInfos.removeIf(i -> i.publishedFileID.equals(new DSteamPublishedFileID(3013499111L)));
    boolean steamDeck = DSteamUtil.IsRunningSteamDeck();
    Loader.LWJGL3_ENABLED = (Loader.LWJGL3_ENABLED || steamDeck);
    System.out.println("Got " + workshopInfos.size() + " workshop items");
    if (!workshopInfos.isEmpty()) {
      DModUtils.cacheWorkshopData(workshopInfos);
    } else {
      workshopInfos = DModUtils.loadCachedWorkshopData();
    } 
    Loader.NEW_WORKSHOP_INFOS = workshopInfos;
  }
  
  private static void RefreshModInfos() {
    ModInfo[] modInfos = DModUtils.getAllMods(Loader.NEW_WORKSHOP_INFOS);
    ArrayList<ModInfo> modInfosFilteredList = new ArrayList<>();
    for (ModInfo modInfo : modInfos) {
      if (!modInfo.ID.equals("ModTheSpirePlusPlusPatcher"))
        modInfosFilteredList.add(modInfo); 
    } 
    Loader.ALLMODINFOS = new ModInfo[modInfosFilteredList.size()];
    for (int i = 0; i < modInfosFilteredList.size(); i++) {
      ModInfo filteredModInfo = modInfosFilteredList.get(i);
      Loader.ALLMODINFOS[i] = filteredModInfo;
    } 
    try {
      for (DSteamUGCDetails d : Loader.NEW_WORKSHOP_INFOS) {
        for (int j = 0; j < Loader.ALLMODINFOS.length; j++) {
          String derivedModID = DracosUtil.FolderNameFromFileName((Loader.ALLMODINFOS[j]).jarURL.toString());
          if (Objects.equals(derivedModID, d.publishedFileID.ToPublicID()))
            (Loader.ALLMODINFOS[j]).steamWorkshopDetails = d; 
        } 
      } 
      if (Loader.ex != null)
        Loader.ex.info = Loader.ALLMODINFOS; 
    } catch (Exception exception) {}
  }
  
  public static void InitializeModCompatibility() {
    for (ModInfo mInfo : Loader.ALLMODINFOS) {
      if (mInfo.steamWorkshopDetails != null)
        if (mInfo.steamWorkshopDetails.publishedFileID.equals(new DSteamPublishedFileID(2384072973L)) || mInfo.steamWorkshopDetails.publishedFileID
          .equals(new DSteamPublishedFileID(2385028403L)))
          ModCompatibilityManager.Register((MtSModIntegration)new TogetherInSpire());  
    } 
    RaidUserProfile.makeRaidModlistJson();
  }
}
