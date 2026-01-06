package com.evacipated.cardcrawl.modthespire.draco.ui;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.draco.mods.DJModPanelCheckBoxList;
import com.evacipated.cardcrawl.modthespire.draco.mods.DModUpdater;
import com.evacipated.cardcrawl.modthespire.draco.ui.objects.SettingsWindow;
import com.evacipated.cardcrawl.modthespire.draco.util.DracosUtil;
import com.evacipated.cardcrawl.modthespire.ui.ModPanel;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class ModSelectWindowExtensions {
  static final Icon ICON_UPDATE = new ImageIcon(ModSelectWindowExtensions.class.getResource("/assets/update.gif"));
  
  static final Icon ICON_SETTINGS = new ImageIcon(ModSelectWindowExtensions.class.getResource("/assets/settings.gif"));
  
  static final Icon ICON_CHECKBOX_EMPTY = new ImageIcon(ModSelectWindowExtensions.class.getResource("/assets/checkbox_empty.gif"));
  
  static final Icon ICON_CHECKBOX_SELECTED = new ImageIcon(ModSelectWindowExtensions.class.getResource("/assets/checkbox_selected.gif"));
  
  static final Icon ICON_TRASHCAN = new ImageIcon(ModSelectWindowExtensions.class.getResource("/assets/trashcan.gif"));
  
  static final Icon ICON_PATCHNOTES = new ImageIcon(ModSelectWindowExtensions.class.getResource("/assets/patchnotes.gif"));
  
  static final Icon ICON_LOGS = new ImageIcon(ModSelectWindowExtensions.class.getResource("/assets/logs.gif"));
  
  public static String LOG_DIR = "sendToDevs/logs/";
  
  public static String LOG_FILE = "sendToDevs/logs/SlayTheSpire.log";
  
  public static JPanel makeTopToolbar() {
    JPanel topBar = new JPanel(new FlowLayout(0));
    topBar.add(makeGeneralButtonsSection());
    topBar.add(makeModlistActionsSection());
    topBar.add(makeInfoSection());
    return topBar;
  }
  
  private static JPanel makeGeneralButtonsSection() {
    JPanel topBar = new JPanel(new FlowLayout(0));
    topBar.add(makeCheckForUpdatesButton());
    topBar.add(makeOpenModsDirectoryButton());
    topBar.add(makeSettingsButton());
    topBar.add(new JSeparator(1));
    return topBar;
  }
  
  private static JButton makeCheckForUpdatesButton() {
    JButton updatesBtn = new JButton(ICON_UPDATE);
    updatesBtn.setToolTipText("Check for Mod Updates");
    updatesBtn.addActionListener(event -> DModUpdater.CheckForModUpdates(Loader.ex.info, false));
    updatesBtn.setPreferredSize(new Dimension(30, 30));
    updatesBtn.setMaximumSize(new Dimension(50, 50));
    return updatesBtn;
  }
  
  private static JButton makeOpenModsDirectoryButton() {
    JButton openFolderBtn = new JButton(UIManager.getIcon("FileView.directoryIcon"));
    openFolderBtn.setToolTipText("Open Mods Directory");
    openFolderBtn.addActionListener(event -> {
          try {
            File file = new File(Loader.MOD_DIR);
            if (!file.exists())
              file.mkdir(); 
            Desktop.getDesktop().open(file);
          } catch (IOException e) {
            e.printStackTrace();
          } 
        });
    openFolderBtn.setPreferredSize(new Dimension(30, 30));
    openFolderBtn.setMaximumSize(new Dimension(50, 50));
    return openFolderBtn;
  }
  
  private static JButton makeSettingsButton() {
    JButton settingsBtn = new JButton(ICON_SETTINGS);
    settingsBtn.addActionListener(event -> {
          SettingsWindow settingsWindow = new SettingsWindow();
          settingsWindow.setVisible(true);
        });
    settingsBtn.setPreferredSize(new Dimension(30, 30));
    settingsBtn.setMaximumSize(new Dimension(50, 50));
    return settingsBtn;
  }
  
  private static JPanel makeModlistActionsSection() {
    JPanel topBar = new JPanel(new FlowLayout(0));
    topBar.add(makeEnableAllModsButton());
    topBar.add(makeDisableAllModsButton());
    topBar.add(new JSeparator(1));
    return topBar;
  }
  
  private static JButton makeEnableAllModsButton() {
    JButton enableAllButton = new JButton(ICON_CHECKBOX_SELECTED);
    enableAllButton.setToolTipText("Toggle all mods on");
    enableAllButton.addActionListener(event -> {
          DJModPanelCheckBoxList.selectAllMods(Loader.ex.modList);
          Loader.ex.repaint();
        });
    enableAllButton.setPreferredSize(new Dimension(30, 30));
    enableAllButton.setMaximumSize(new Dimension(50, 50));
    return enableAllButton;
  }
  
  private static JButton makeDisableAllModsButton() {
    JButton disableAllButton = new JButton(ICON_CHECKBOX_EMPTY);
    disableAllButton.setToolTipText("Toggle all mods off");
    disableAllButton.addActionListener(event -> {
          DJModPanelCheckBoxList.deselectAllMods(Loader.ex.modList);
          Loader.ex.repaint();
        });
    disableAllButton.setPreferredSize(new Dimension(30, 30));
    disableAllButton.setMaximumSize(new Dimension(50, 50));
    return disableAllButton;
  }
  
  private static JPanel makeModActionsSection() {
    JPanel topBar = new JPanel(new FlowLayout(0));
    topBar.add(makeUninstallButton());
    topBar.add(new JSeparator(1));
    return topBar;
  }
  
  private static JButton makeUninstallButton() {
    JButton removeButton = new JButton(ICON_TRASHCAN);
    removeButton.setToolTipText("Unsubscribe and remove.");
    removeButton.addActionListener(event -> {
          ModPanel selectedPanel = (ModPanel)Loader.ex.modList.getSelectedValue();
          if (selectedPanel != null)
            DracosUtil.FullyRemoveMod(selectedPanel.info); 
        });
    removeButton.setPreferredSize(new Dimension(30, 30));
    removeButton.setMaximumSize(new Dimension(50, 50));
    return removeButton;
  }
  
  private static JPanel makeInfoSection() {
    JPanel topBar = new JPanel(new FlowLayout(0));
    topBar.add(makeOpenLogsFolderButton());
    topBar.add(makePatchnotesButton());
    topBar.add(new JSeparator(1));
    topBar.add(new JSeparator(1));
    return topBar;
  }
  
  private static JButton makeOpenLogsFolderButton() {
    JButton logsButton = new JButton(ICON_LOGS);
    logsButton.setToolTipText("Open logs folder");
    logsButton.addActionListener(event -> {
          try {
            File file = new File(LOG_DIR);
            if (!file.exists())
              file.mkdir(); 
            Desktop.getDesktop().open(file);
          } catch (IOException e) {
            e.printStackTrace();
          } 
        });
    logsButton.setPreferredSize(new Dimension(30, 30));
    logsButton.setMaximumSize(new Dimension(50, 50));
    return logsButton;
  }
  
  private static JButton makePatchnotesButton() {
    JButton patchnoteButton = new JButton(ICON_PATCHNOTES);
    patchnoteButton.setToolTipText("MtS++ Patchnotes");
    patchnoteButton.addActionListener(event -> {
          JDialog dialog = new JDialog((Frame)Loader.ex, "Patchnotes");
          dialog.setLocationRelativeTo((Component)Loader.ex);
          dialog.setMinimumSize(new Dimension(600, 400));
          JTextArea textArea = new JTextArea();
          textArea.setText(DracosUtil.GetTextFromFile("/files/patchnotes.txt"));
          textArea.setLineWrap(true);
          textArea.setWrapStyleWord(true);
          textArea.setEditable(false);
          JScrollPane scrollPane = new JScrollPane(textArea);
          scrollPane.setVerticalScrollBarPolicy(22);
          dialog.add(scrollPane);
          dialog.setVisible(true);
        });
    patchnoteButton.setPreferredSize(new Dimension(30, 30));
    patchnoteButton.setMaximumSize(new Dimension(50, 50));
    return patchnoteButton;
  }
}
