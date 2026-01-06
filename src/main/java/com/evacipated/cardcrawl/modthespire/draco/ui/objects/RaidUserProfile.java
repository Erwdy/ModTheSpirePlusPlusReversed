package com.evacipated.cardcrawl.modthespire.draco.ui.objects;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.draco.modlist.AdvancedModList;
import com.evacipated.cardcrawl.modthespire.draco.mods.DModPanel;
import com.evacipated.cardcrawl.modthespire.draco.mods.DModSelectWindow;
import com.evacipated.cardcrawl.modthespire.draco.mods.LimitedModInfo;
import com.evacipated.cardcrawl.modthespire.draco.ui.DUIHelp;
import com.evacipated.cardcrawl.modthespire.draco.util.DracosUtil;
import com.evacipated.cardcrawl.modthespire.ui.ModPanel;
import com.google.gson.Gson;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class RaidUserProfile extends UserProfile {
  public AdvancedModList modList;
  
  public RaidUserProfile() {
    super("Raid the Spire");
    this.id = "raidthespire" + UUID.randomUUID();
    this.properties.noSave = true;
    this.backgroundColor = Color.decode("#FFDB82");
  }
  
  public void Load() {
    JDialog dialog = DUIHelp.CreateModalTextPopup((JFrame)Loader.ex, "Loading", "Getting modlist data");
    SwingUtilities.invokeLater(() -> {
          String modlist = DracosUtil.GetTextFromURL("https://tis.gg/misc/raid_modlist.json");
          ArrayList<String> clientMods = DracosUtil.GetTextAsArrayFromURL("https://raw.githubusercontent.com/Draco9990/togetherinspire/gh-pages/clientmods.txt");
          if (modlist == null || clientMods == null) {
            JOptionPane.showMessageDialog((Component)Loader.ex, "Failed to get raid data");
            dialog.dispose();
            return;
          } 
          AdvancedModList advancedModList = (AdvancedModList)(new Gson()).fromJson(modlist, AdvancedModList.class);
          if (advancedModList == null) {
            JOptionPane.showMessageDialog((Component)Loader.ex, "Could not convert raid data");
            dialog.dispose();
            return;
          } 
          advancedModList.allowedMods = clientMods;
          dialog.dispose();
          DModSelectWindow.ImportModList(advancedModList.requiredMods);
          Loader.ex.modList.properties = ((UserProfile)Loader.ex.dProfilesList.getSelectedItem()).properties;
          DefaultListModel<ModPanel> modPanelListModel = (DefaultListModel<ModPanel>)Loader.ex.modList.getModel();
          for (int i = 0; i < modPanelListModel.getSize(); i++) {
            ModPanel panel = modPanelListModel.getElementAt(i);
            if (advancedModList.IsModRequired(panel.info.ID)) {
              DModPanel.setRequired(panel);
              panel.recalcModWarnings(Loader.ex.modList);
            } else if (!advancedModList.allowedMods.contains(panel.info.ID)) {
              modPanelListModel.removeElementAt(i);
              i--;
            } 
          } 
        });
    dialog.setVisible(true);
  }
  
  public static void makeRaidModlistJson() {
    String[] modsToInclude = { 
        "basemod", "stslib", "spireTogether", "downfall", "MarisaContinued", "skulmod", "PirateMod", "TheUnchainedMod", "anniv5", "GifTheSpireLib", 
        "LevelheadMod" };
    LimitedModInfo[] requiredMods = new LimitedModInfo[modsToInclude.length];
    for (int i = 0; i < modsToInclude.length; i++) {
      String mod = modsToInclude[i];
      for (ModInfo existingMod : Loader.ALLMODINFOS) {
        if (existingMod.ID.equals(modsToInclude[i]))
          requiredMods[i] = new LimitedModInfo(existingMod); 
      } 
    } 
    AdvancedModList advancedModList = new AdvancedModList();
    advancedModList.requiredMods = requiredMods;
    DracosUtil.CopyToClipboard(advancedModList.toJson());
  }
}
