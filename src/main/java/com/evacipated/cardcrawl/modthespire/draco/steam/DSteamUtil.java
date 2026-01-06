package com.evacipated.cardcrawl.modthespire.draco.steam;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.draco.mods.LimitedModInfo;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamPublishedFileID;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamUGCDetails;
import com.evacipated.cardcrawl.modthespire.draco.steam.requests.DSteam_CheckModUpdates;
import com.evacipated.cardcrawl.modthespire.draco.steam.requests.DSteam_GetSubscribedModDetails;
import com.evacipated.cardcrawl.modthespire.draco.steam.requests.DSteam_IsRunningSteamDeck;
import com.evacipated.cardcrawl.modthespire.draco.steam.requests.DSteam_SubscribeAndDownloadItems;
import com.evacipated.cardcrawl.modthespire.draco.steam.requests.DSteam_Unsubscribe;
import com.evacipated.cardcrawl.modthespire.steam.SteamSearch;
import com.evacipated.cardcrawl.modthespire.ui.ModSelectWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class DSteamUtil {
  public static ArrayList<DSteamUGCDetails> GetSubscribedModDetails() {
    final Object ret = RunSimpleSteamRequest(DSteam_GetSubscribedModDetails.class, new TypeToken<ArrayList<DSteamUGCDetails>>() {}.getType(), new String[0]);
    if (ret == null) {
      return new ArrayList<DSteamUGCDetails>();
    }
    return (ArrayList<DSteamUGCDetails>)ret;
  }

  public static ArrayList<DSteamPublishedFileID> CheckModUpdates() {
    final Object ret = RunSimpleSteamRequest(DSteam_CheckModUpdates.class, new TypeToken<ArrayList<DSteamPublishedFileID>>() {}.getType(), new String[0]);
    if (ret == null) {
      return new ArrayList<DSteamPublishedFileID>();
    }
    return (ArrayList<DSteamPublishedFileID>)ret;
  }

  public static void Unsubscribe(final ArrayList<DSteamPublishedFileID> mods) {
    RunAdvancedSteamRequest(DSteam_Unsubscribe.class, new Gson().toJson(mods));
  }

  public static boolean IsRunningSteamDeck() {
    final Object val = RunSimpleSteamRequest(DSteam_IsRunningSteamDeck.class, Boolean.class, new String[0]);
    return val != null && (boolean)val;
  }

  public static BufferedReader SubscribeAndDownloadMods(final ArrayList<DSteamPublishedFileID> mods) {
    return RunAdvancedSteamRequest(DSteam_SubscribeAndDownloadItems.class, new Gson().toJson(mods));
  }
  
  public static Object RunSimpleSteamRequest(Class<? extends DSteam_Common> commandClass, Type returnClass, String... args) {
    try {
      BufferedReader in = RunAdvancedSteamRequest(commandClass, args);
      String jsonString = in.readLine();
      Gson gson = new Gson();
      in.close();
      return gson.fromJson(jsonString, returnClass);
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public static BufferedReader RunAdvancedSteamRequest(Class<? extends DSteam_Common> commandClass, String... args) {
    try {
      String path = commandClass.getProtectionDomain().getCodeSource().getLocation().getPath();
      path = URLDecoder.decode(path, "utf-8");
      path = (new File(path)).getPath();
      String[] fullArgs = new String[args.length + 4];
      fullArgs[0] = SteamSearch.findJRE();
      fullArgs[1] = "-cp";
      fullArgs[2] = path + File.pathSeparator + Loader.STS_JAR;
      fullArgs[3] = commandClass.getName();
      for (int i = 0; i < args.length; i++)
        fullArgs[i + 4] = args[i]; 
      ProcessBuilder pb = (new ProcessBuilder(fullArgs)).redirectError(ProcessBuilder.Redirect.INHERIT);
      Process p = pb.start();
      return new BufferedReader(new InputStreamReader(p.getInputStream()));
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public static ArrayList<DSteamPublishedFileID> DownloadModsAndOpenWindow(ModSelectWindow window, final ArrayList<LimitedModInfo> mods) {
    final JDialog dialog = new JDialog((Frame)window, "Downloading mods", true);
    dialog.setDefaultCloseOperation(0);
    dialog.setLayout(new BorderLayout());
    dialog.setPreferredSize(new Dimension(500, 100));
    final JLabel label = new JLabel("Downloading...");
    label.setHorizontalAlignment(0);
    final JProgressBar progressBar = new JProgressBar();
    progressBar.setValue(0);
    progressBar.setStringPainted(true);
    dialog.add(label, "North");
    dialog.add(progressBar, "Center");
    dialog.pack();
    dialog.setLocationRelativeTo((Component)window);
    final ArrayList[] failedDownloads = { new ArrayList() };
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
        protected Void doInBackground() throws Exception {
          try {
            ArrayList<DSteamPublishedFileID> modIds = new ArrayList<>();
            mods.forEach(m -> modIds.add(m.GetPublishedID()));
            BufferedReader r = DSteamUtil.SubscribeAndDownloadMods(modIds);
            String line = null;
            while ((line = r.readLine()) != null) {
              if (line.startsWith("PERC")) {
                line = line.substring(4);
                int perc = Integer.parseInt(line);
                progressBar.setValue(perc);
                continue;
              } 
              if (line.startsWith("DOWN")) {
                line = line.substring(4);
                long modId = Long.parseLong(line);
                mods.forEach(m -> {
                      if (m.GetPublishedID().equals(new DSteamPublishedFileID(modId)))
                        label.setText("Downloading " + m.Name); 
                    });
                continue;
              } 
              try {
                failedDownloads[0] = (ArrayList)(new Gson()).fromJson(line, (new TypeToken<ArrayList<DSteamPublishedFileID>>() {
                    
                    }).getType());
              } catch (Exception exception) {}
            } 
            r.close();
          } catch (Exception exception) {}
          return null;
        }
        
        protected void done() {
          dialog.dispose();
        }
      };
    worker.execute();
    dialog.setVisible(true);
    return failedDownloads[0];
  }
}
