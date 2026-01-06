package com.evacipated.cardcrawl.modthespire.draco.mods;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.ui.ModPanel;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import javax.swing.UIManager;

public class DModPanel {
  public static void setRequired(final ModPanel panel) {
    panel.checkBox.addItemListener(new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == 2)
              panel.checkBox.setSelected(true); 
          }
        });
    panel.required = true;
  }
  
  public static boolean getRequired(ModPanel panel) {
    return panel.required;
  }
  
  public static void preRecalcModWarning(ModPanel panel) {
    panel.checkBox.setBackground(UIManager.getColor("ComboBox.background"));
    panel.infoPanel.setBackground(UIManager.getColor("ComboBox.background"));
    if (panel.required) {
      panel.checkBox.setEnabled(true);
      panel.checkBox.setBackground(Color.LIGHT_GRAY);
      panel.infoPanel.setBackground(Color.LIGHT_GRAY);
    } 
  }
  
  public static String filter_inner(ModPanel panel) {
    if (panel.info.steamWorkshopDetails != null) {
      ModInfo workshopInfo = Arrays.<ModInfo>stream(Loader.ALLMODINFOS).filter(i -> (i.steamWorkshopDetails == null) ? false : i.steamWorkshopDetails.publishedFileID.equals(panel.info.steamWorkshopDetails.publishedFileID)).findFirst().orElse(null);
      if (workshopInfo != null)
        return String.format("%s %s", new Object[] { workshopInfo.steamWorkshopDetails.title, String.join(" ", new CharSequence[] { workshopInfo.steamWorkshopDetails.getTags() }) }); 
    } 
    return "";
  }
}
