package com.evacipated.cardcrawl.modthespire.draco.mods;

import com.evacipated.cardcrawl.modthespire.DownloadAndRestarter;
import com.evacipated.cardcrawl.modthespire.GithubUpdateChecker;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.ModUpdate;
import com.evacipated.cardcrawl.modthespire.draco.steam.DSteamUtil;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamPublishedFileID;
import com.evacipated.cardcrawl.modthespire.draco.ui.DUIHelp;
import com.evacipated.cardcrawl.modthespire.draco.ui.objects.SettingsWindow;
import com.evacipated.cardcrawl.modthespire.draco.util.MtSPPConfigManager;
import com.evacipated.cardcrawl.modthespire.ui.ModSelectWindow;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class DModUpdater {
  public static void onStartup() {
    if (MtSPPConfigManager.MTSPP_CONFIG.getBool(SettingsWindow.settings_checkforupdatesonstartup))
      CheckForModUpdates(Loader.ALLMODINFOS, true); 
  }
  
  public static void CheckForModUpdates(ModInfo[] mods, boolean silent) {
    List<ModInfo> fullModList = Arrays.asList(mods);
    boolean anyUpdateNeeded = false;
    boolean anyUpdateDone = false;
    ArrayList<ModInfo> steamUpdates = CheckForSteamUpdates(mods);
    if (!steamUpdates.isEmpty()) {
      anyUpdateNeeded = true;
      String message = "Found Steam updates for following mods: ";
      for (ModInfo i : steamUpdates)
        message = message + "\n • " + i.Name; 
      message = message + "\nWould you like to update them?";
      String[] options = { "Update All", "Ignore" };
      int result = DUIHelp.OpenSimpleButtonDialog((Component)Loader.ex, "Updates Available", message, (Object[])options, options[0]);
      if (result == 0) {
        ArrayList<LimitedModInfo> steamIds = LimitedModInfo.ListFromModInfos(steamUpdates);
        if (!steamIds.isEmpty()) {
          ArrayList<DSteamPublishedFileID> failedDownloads = DSteamUtil.DownloadModsAndOpenWindow(Loader.ex, steamIds);
          steamUpdates.removeIf(i -> !failedDownloads.contains(i.steamWorkshopDetails.publishedFileID));
          if (!failedDownloads.isEmpty()) {
            String failedMessage = "Failed to update following mods through Steam: ";
            for (ModInfo i : steamUpdates)
              failedMessage = failedMessage + "\n • " + i.Name; 
            JOptionPane.showMessageDialog((Component)Loader.ex, failedMessage);
          } 
        } 
        fullModList.removeIf(m -> !steamUpdates.contains(m));
        anyUpdateDone = true;
      } 
    } 
    ArrayList<ModUpdate> githubUpdates = CheckForGithubUpdates(fullModList);
    if (!githubUpdates.isEmpty()) {
      anyUpdateNeeded = true;
      String message = "Found GitHub updates for following mods: ";
      for (ModInfo i : steamUpdates)
        message = message + "\n • " + i.Name; 
      message = message + "\nWould you like to update them?";
      String[] options = { "Update All", "Open in browser", "Ignore" };
      int result = DUIHelp.OpenSimpleButtonDialog((Component)Loader.ex, "Update", message, (Object[])options, options[0]);
      if (result == 0) {
        DownloadGithubMods(Loader.ex, githubUpdates);
        URL[] downloadURLs = new URL[githubUpdates.size()];
        for (int i = 0; i < githubUpdates.size(); i++)
          downloadURLs[i] = ((ModUpdate)githubUpdates.get(i)).downloadURL; 
        try {
          DownloadAndRestarter.downloadAndRestart(downloadURLs);
        } catch (IOException|java.net.URISyntaxException e) {
          e.printStackTrace();
        } 
        fullModList.removeIf(m -> {
              for (ModUpdate u : githubUpdates) {
                if (u.info.ID.equals(m.ID))
                  return true; 
              } 
              return false;
            });
      } 
    } 
    if (anyUpdateDone) {
      DLoader.RefreshModList();
      if (fullModList.isEmpty()) {
        JOptionPane.showMessageDialog((Component)Loader.ex, "Successfully updated all mods!");
      } else {
        String message = "Failed to update following mods:";
        for (ModInfo i : fullModList)
          message = message + "\n • " + i.Name; 
        message = message + "\nWould you like to update them?";
        JOptionPane.showMessageDialog((Component)Loader.ex, message);
      } 
    } 
    if (!anyUpdateNeeded && !silent)
      JOptionPane.showMessageDialog((Component)Loader.ex, "All mods are up to date!"); 
  }
  
  private static ArrayList<ModInfo> CheckForSteamUpdates(ModInfo[] mods) {
    ArrayList<DSteamPublishedFileID> modIds = new ArrayList<>();
    for (ModInfo m : mods) {
      if (m.steamWorkshopDetails != null)
        modIds.add(m.steamWorkshopDetails.publishedFileID); 
    } 
    if (modIds.isEmpty())
      return new ArrayList<>(); 
    JDialog dialog = DUIHelp.CreateModalTextPopup((JFrame)Loader.ex, "Update check", "Checking for Steam updates");
    ArrayList<ModInfo> needsUpdates = new ArrayList<>();
    SwingUtilities.invokeLater(() -> {
        ArrayList<DSteamPublishedFileID> needUpdatesIds = DSteamUtil.CheckModUpdates();
        needUpdatesIds.forEach(mId -> {
            for (int i = 0; i < mods.length; ++i) {
                ModInfo mod = mods[i];
                if (mod.steamWorkshopDetails != null && mod.steamWorkshopDetails.publishedFileID.equals(mId)) {
                    needsUpdates.add(mod);
                }
            }
        });
        dialog.dispose();
        });
    dialog.setVisible(true);
    return needsUpdates;
  }
  
  private static ArrayList<ModUpdate> CheckForGithubUpdates(List<ModInfo> mods) {
    JDialog dialog = DUIHelp.CreateModalTextPopup((JFrame)Loader.ex, "Update check", "Checking for GitHub updates");
    ArrayList<ModUpdate> needsUpdate = new ArrayList<>();
    SwingUtilities.invokeLater(() -> {
          for (ModInfo info : mods) {
            try {
              GithubUpdateChecker githubUpdateChecker = new GithubUpdateChecker(info.UpdateJSON);
              if (githubUpdateChecker.isNewerVersionAvailable(info.ModVersion))
                needsUpdate.add(new ModUpdate(info, githubUpdateChecker.getLatestReleaseURL(), githubUpdateChecker.getLatestDownloadURL())); 
            } catch (Exception exception) {}
          } 
          dialog.dispose();
        });
    dialog.setVisible(true);
    return needsUpdate;
  }
  
  public static ArrayList<DSteamPublishedFileID> DownloadGithubMods(ModSelectWindow window, final ArrayList<ModUpdate> mods) {
    final JDialog dialog = new JDialog((Frame)window, "Downloading mods", true);
    dialog.setDefaultCloseOperation(0);
    dialog.setLayout(new BorderLayout());
    dialog.setPreferredSize(new Dimension(500, 100));
    final JLabel label = new JLabel("Downloading...");
    label.setHorizontalAlignment(0);
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    dialog.add(label, "North");
    dialog.add(progressBar, "Center");
    dialog.pack();
    dialog.setLocationRelativeTo((Component)window);
    ArrayList[] arrayOfArrayList = { new ArrayList() };
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
        protected Void doInBackground() throws Exception {
          try {
            for (ModUpdate m : mods) {
              label.setText("Downloading " + m.info.Name);
              DownloadAndRestarter.downloadOne(m.downloadURL);
            } 
          } catch (Exception exception) {}
          return null;
        }
        
        protected void done() {
          dialog.dispose();
        }
      };
    worker.execute();
    dialog.setVisible(true);
    return arrayOfArrayList[0];
  }
}
