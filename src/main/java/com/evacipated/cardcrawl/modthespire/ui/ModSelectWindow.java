package com.evacipated.cardcrawl.modthespire.ui;

import com.evacipated.cardcrawl.modthespire.GithubUpdateChecker;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.ModList;
import com.evacipated.cardcrawl.modthespire.ModUpdate;
import com.evacipated.cardcrawl.modthespire.draco.loadingscreen.LoadingScreenBuilder;
import com.evacipated.cardcrawl.modthespire.draco.mods.DModSelectWindow;
import com.evacipated.cardcrawl.modthespire.draco.mods.DModUpdater;
import com.evacipated.cardcrawl.modthespire.draco.ui.DUIHelp;
import com.evacipated.cardcrawl.modthespire.draco.ui.ModSelectWindowExtensions;
import com.evacipated.cardcrawl.modthespire.draco.ui.objects.RaidUserProfile;
import com.evacipated.cardcrawl.modthespire.draco.ui.objects.SettingsWindow;
import com.evacipated.cardcrawl.modthespire.draco.ui.objects.UserProfile;
import com.evacipated.cardcrawl.modthespire.draco.util.DracosUtil;
import com.evacipated.cardcrawl.modthespire.draco.util.MtSPPConfigManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ModSelectWindow extends JFrame {
  private static final long serialVersionUID = -8232997068791248057L;
  
  private static final int DEFAULT_WIDTH = 800;
  
  private static final int DEFAULT_HEIGHT = 500;
  
  private static final String DEBUG_OPTION = "Debug";
  
  private static final String PLAY_OPTION = "Play";
  
  private static final String JAR_DUMP_OPTION = "Dump Patched Jar";
  
  private static final String PACKAGE_OPTION = "Package";
  
  static final Image APP_ICON = Toolkit.getDefaultToolkit().createImage(ModSelectWindow.class.getResource("/assets/icon.png"));
  
  static final Icon ICON_UPDATE = new ImageIcon(ModSelectWindow.class.getResource("/assets/update.gif"));
  
  static final Icon ICON_LOAD = new ImageIcon(ModSelectWindow.class.getResource("/assets/ajax-loader.gif"));
  
  static final Icon ICON_GOOD = new ImageIcon(ModSelectWindow.class.getResource("/assets/good.gif"));
  
  static final Icon ICON_WARNING = new ImageIcon(ModSelectWindow.class.getResource("/assets/warning.gif"));
  
  static final Icon ICON_ERROR = new ImageIcon(ModSelectWindow.class.getResource("/assets/error.gif"));
  
  static final Icon ICON_WORKSHOP = new ImageIcon(ModSelectWindow.class.getResource("/assets/workshop.gif"));
  
  public ModInfo[] info;
  
  private boolean showingLog = false;
  
  private boolean isMaximized = false;
  
  private boolean isCentered = false;
  
  private Rectangle location;
  
  public JButton playBtn;
  
  public JModPanelCheckBoxList modList;
  
  private ModInfo currentModInfo;
  
  private TitledBorder name;
  
  private JTextArea authors;
  
  private JLabel modVersion;
  
  private JTextArea status;
  
  private JLabel mtsVersion;
  
  private JLabel stsVersion;
  
  private JTextArea description;
  
  private JTextArea credits;
  
  public JComboBox<UserProfile> dProfilesList;
  
  private JPanel bannerNoticePanel;
  
  private JLabel mtsUpdateBanner;
  
  private JLabel betaWarningBanner;
  
  private JPanel modBannerNoticePanel;
  
  private JLabel modUpdateBanner;
  
  static List<ModUpdate> MODUPDATES;
  
  public enum UpdateIconType {
    NONE, CAN_CHECK, CHECKING, UPDATE_AVAILABLE, UPTODATE, WORKSHOP;
  }
  
  public static Properties getDefaults() {
    Properties properties = new Properties();
    properties.setProperty("x", "center");
    properties.setProperty("y", "center");
    properties.setProperty("width", Integer.toString(800));
    properties.setProperty("height", Integer.toString(500));
    properties.setProperty("maximize", Boolean.toString(false));
    return properties;
  }
  
  public ModSelectWindow(ModInfo[] modInfos, boolean skipLauncher) {
    setIconImage(DModSelectWindow.APP_ICON_NEW);
    this.info = modInfos;
    readWindowPosSize();
    setupDetectMaximize();
    initUI(skipLauncher);
    if (Loader.MTS_CONFIG.getBool("maximize")) {
      this.isMaximized = true;
      setExtendedState(getExtendedState() | 0x6);
    } 
    DModUpdater.onStartup();
    addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            DModSelectWindow.SaveCurrentModlist();
          }
        });
  }
  
  private void readWindowPosSize() {
    if (Loader.MTS_CONFIG.getInt("width") < 800)
      Loader.MTS_CONFIG.setInt("width", 800); 
    if (Loader.MTS_CONFIG.getInt("height") < 500)
      Loader.MTS_CONFIG.setInt("height", 500); 
    this.location = new Rectangle();
    this.location.width = Loader.MTS_CONFIG.getInt("width");
    this.location.height = Loader.MTS_CONFIG.getInt("height");
    if (Loader.MTS_CONFIG.getString("x").equals("center") || Loader.MTS_CONFIG.getString("y").equals("center")) {
      this.isCentered = true;
    } else {
      this.isCentered = false;
      this.location.x = Loader.MTS_CONFIG.getInt("x");
      this.location.y = Loader.MTS_CONFIG.getInt("y");
      if (!isInScreenBounds(this.location)) {
        Loader.MTS_CONFIG.setString("x", "center");
        Loader.MTS_CONFIG.setString("y", "center");
        this.isCentered = true;
      } 
    } 
    try {
      Loader.MTS_CONFIG.save();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  private void setupDetectMaximize() {
    final ModSelectWindow tmpthis = this;
    addComponentListener(new ComponentAdapter() {
          public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            if (!ModSelectWindow.this.showingLog) {
              Dimension d = tmpthis.getContentPane().getSize();
              if (!ModSelectWindow.this.isMaximized)
                ModSelectWindow.this.saveWindowDimensions(d); 
            } 
          }
          
          int skipMoves = 2;
          
          public void componentMoved(ComponentEvent e) {
            super.componentMoved(e);
            if (!ModSelectWindow.this.showingLog && this.skipMoves == 0) {
              if (ModSelectWindow.this.isInScreenBounds(ModSelectWindow.this.getLocationOnScreen(), ModSelectWindow.this.getBounds()))
                ModSelectWindow.this.saveWindowLocation(); 
              ModSelectWindow.this.isCentered = false;
            } else if (this.skipMoves > 0) {
              this.skipMoves--;
            } 
          }
        });
    addWindowStateListener(new WindowAdapter() {
          public void windowStateChanged(WindowEvent e) {
            super.windowStateChanged(e);
            if (!ModSelectWindow.this.showingLog)
              if ((e.getNewState() & 0x6) != 0) {
                ModSelectWindow.this.isMaximized = true;
                ModSelectWindow.this.saveWindowMaximize();
              } else {
                ModSelectWindow.this.isMaximized = false;
                ModSelectWindow.this.saveWindowMaximize();
              }  
          }
        });
  }
  
  private void initUI(boolean skipLauncher) {
    setTitle("ModTheSpire++ " + Loader.MTS_VERSION + " - " + "d4");
    setDefaultCloseOperation(3);
    setResizable(true);
    this.rootPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setLayout(new BorderLayout());
    getContentPane().setPreferredSize(new Dimension(this.location.width, this.location.height));
    JSplitPane splitPane = new JSplitPane(1, makeModListPanel(), makeInfoPanel());
    splitPane.setContinuousLayout(true);
    getContentPane().add(splitPane, "Center");
    getContentPane().add(makeTopPanel(), "North");
    pack();
    if (this.isCentered) {
      setLocationRelativeTo((Component)null);
    } else {
      setLocation(this.location.getLocation());
    } 
    if (skipLauncher) {
      this.playBtn.doClick();
    } else {
      JRootPane rootPane = SwingUtilities.getRootPane(this.playBtn);
      rootPane.setDefaultButton(this.playBtn);
      EventQueue.invokeLater(this.playBtn::requestFocusInWindow);
    } 
  }
  
  private JPanel makeModListPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setPreferredSize(new Dimension(220, 300));
    panel.setMinimumSize(new Dimension(150, 300));
    DefaultListModel<ModPanel> model = new DefaultListModel<>();
    this.modList = new JModPanelCheckBoxList(this, model);
    ModList mods = ModList.loadModLists();
    mods.loadModsInOrder(model, this.info, this.modList);
    this.modList.publishBoxChecked();
    JScrollPane modScroller = new JScrollPane(this.modList);
    panel.add(modScroller, "Center");
    this.playBtn = new JButton(Loader.PACKAGE ? "Package" : (Loader.OUT_JAR ? "Dump Patched Jar" : "Play"));
    this.playBtn.addActionListener(event -> {
          this.showingLog = true;
          this.playBtn.setEnabled(false);
          getContentPane().removeAll();
          Loader.SKIP_INTRO = MtSPPConfigManager.MTSPP_CONFIG.getBool(SettingsWindow.settings_skipmegacritintro);
          LoadingScreenBuilder.buildLoadingScreen(this);
          setResizable(true);
          pack();
          if (this.isCentered)
            setLocationRelativeTo((Component)null); 
          Thread tCfg = new Thread(DModSelectWindow::SaveCurrentModlist);
          tCfg.start();
          Thread t = new Thread(() -> {
            File[] selectedMods;
            if (Loader.manualModIds != null) {
              selectedMods = this.modList.getAllMods();
            }
            else {
              selectedMods = this.modList.getCheckedMods();
            }
            Loader.runMods(selectedMods);
            if (Loader.CLOSE_WHEN_FINISHED) {
              Loader.closeWindow();
            }
          });
          t.start();
        });
    if (Loader.STS_BETA && !Loader.allowBeta)
      this.playBtn.setEnabled(false); 
    JPanel bottomPanel = new JPanel(new GridLayout(0, 1));
    JPanel importExportPanel = new JPanel(new GridLayout(1, 0));
    importExportPanel.add(DUIHelp.MakeExportModListButton(this));
    JButton importModlistBttn = DUIHelp.MakeImportModListButton(this);
    importExportPanel.add(importModlistBttn);
    bottomPanel.add(importExportPanel);
    bottomPanel.add(this.playBtn);
    panel.add(bottomPanel, "South");
    this.dProfilesList = new JComboBox<>(UserProfile.FromStringArray((String[])ModList.getAllModListNames().toArray((Object[])new String[0])));
    JButton addProfile = new JButton("+");
    JButton delProfile = new JButton("-");
    JButton profileEditButton = DUIHelp.MakeEditProfileButton(this);
    TextFieldWithPlaceholder filter = new TextFieldWithPlaceholder();
    filter.setPlaceholder("Filter...");
    this.dProfilesList.addActionListener(event -> {
          DModSelectWindow.SaveModlistAs(DModSelectWindow.cachedModlist);
          DModSelectWindow.cachedModlist = ((UserProfile)Objects.requireNonNull(this.dProfilesList.getSelectedItem())).id;
          UserProfile profile = (UserProfile)this.dProfilesList.getSelectedItem();
          delProfile.setEnabled((!ModList.DEFAULT_LIST.equals(profile.getId()) && !profile.properties.noSave));
          profileEditButton.setEnabled((!ModList.DEFAULT_LIST.equals(profile.getId()) && !profile.properties.noSave));
          importModlistBttn.setEnabled(!profile.properties.noSave);
          ModList newList = new ModList(profile.getId(), profile.properties);
          DefaultListModel<ModPanel> newModel = (DefaultListModel<ModPanel>)this.modList.getModel();
          newList.loadModsInOrder(newModel, this.info, this.modList);
          filter.setText("");
          if (profile instanceof RaidUserProfile)
            ((RaidUserProfile)profile).Load(); 
          if (!profile.properties.noSave) {
            Thread tCfg = new Thread(() -> ModList.save(profile.getId(), this.modList.getCheckedMods()));
            tCfg.start();
          } 
        });
    this.dProfilesList.setRenderer(new ListCellRenderer<UserProfile>() {
          public Component getListCellRendererComponent(JList<? extends UserProfile> list, UserProfile value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = new JLabel(value.getDisplayName());
            Color backgroundColor = value.getBackgroundColor();
            if (backgroundColor != null) {
              label.setBackground(backgroundColor);
            } else {
              label.setBackground(list.getBackground());
            } 
            if (isSelected) {
              label.setForeground(list.getSelectionForeground());
              label.setBackground(list.getSelectionBackground());
            } else {
              label.setForeground(list.getForeground());
            } 
            label.setOpaque(true);
            return label;
          }
        });
    if (Loader.profileArg != null) {
      this.dProfilesList.setSelectedItem(new UserProfile(Loader.profileArg));
    } else {
      this.dProfilesList.setSelectedItem(new UserProfile(ModList.getDefaultList()));
    } 
    JPanel profilesPanel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = 1;
    c.ipady = 2;
    profilesPanel.add(profileEditButton, c);
    c.weightx = 0.9D;
    c.ipady = 0;
    profilesPanel.add(this.dProfilesList, c);
    c.weightx = 0.0D;
    c.ipady = 2;
    addProfile.setToolTipText("Add new profile");
    addProfile.addActionListener(event -> {
          String s = JOptionPane.showInputDialog(this, "Profile Name:", "New Profile", -1);
          if (s != null && !s.isEmpty()) {
            this.dProfilesList.addItem(new UserProfile(s));
            this.dProfilesList.setSelectedIndex(this.dProfilesList.getItemCount() - 1);
          } 
        });
    profilesPanel.add(addProfile, c);
    delProfile.setToolTipText("Delete profile");
    delProfile.addActionListener(event -> {
          UserProfile p = (UserProfile)this.dProfilesList.getSelectedItem();
          int n = JOptionPane.showConfirmDialog(this, "Are you sure?\nThis action cannot be undone.", "Delete Profile \"" + p.getDisplayName() + "\"", 0);
          if (n == 0) {
            this.dProfilesList.removeItem(this.dProfilesList.getSelectedItem());
            this.dProfilesList.setSelectedItem(new UserProfile(ModList.DEFAULT_LIST));
            ModList.delete(p.getId());
          } 
        });
    profilesPanel.add(delProfile, c);
    final Runnable filterModList = () -> {
        String filterText = filter.getText().trim().toLowerCase();
        String[] filterKeys = (filterText.length() == 0) ? null : filterText.split("\\s+");
        for (int i = 0; i < model.size(); i++) {
          ModPanel modPanel = model.getElementAt(i);
          modPanel.filter(filterKeys);
        } 
        this.modList.updateUI();
      };
    filter.getDocument().addDocumentListener(new DocumentListener() {
          public void insertUpdate(DocumentEvent e) {
            filterModList.run();
          }
          
          public void removeUpdate(DocumentEvent e) {
            filterModList.run();
          }
          
          public void changedUpdate(DocumentEvent e) {
            filterModList.run();
          }
        });
    JPanel topPanel = new JPanel(new GridLayout(0, 1));
    topPanel.add(profilesPanel);
    topPanel.add(filter);
    panel.add(topPanel, "North");
    return panel;
  }
  
  private JPanel makeInfoPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setMinimumSize(new Dimension(350, 300));
    panel.add(makeModBannerPanel(), "North");
    panel.add(makeStatusPanel(), "South");
    JPanel infoPanel = new JPanel();
    this.name = BorderFactory.createTitledBorder("Mod Info");
    this.name.setTitleFont(this.name.getTitleFont().deriveFont(1));
    infoPanel.setBorder(BorderFactory.createCompoundBorder(this.name, 
          
          BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    infoPanel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = 1;
    c.gridx = 1;
    c.gridy = 0;
    c.anchor = 17;
    c.weightx = 1.0D;
    this.authors = makeInfoTextAreaField("Author(s)", " ");
    infoPanel.add(this.authors, c);
    c.gridy = 1;
    this.modVersion = makeInfoLabelField("ModVersion", " ");
    infoPanel.add(this.modVersion, c);
    c.gridy = 2;
    this.mtsVersion = makeInfoLabelField("ModTheSpire Version", " ");
    infoPanel.add(this.mtsVersion, c);
    c.gridy = 3;
    this.stsVersion = makeInfoLabelField("Slay the Spire Version", " ");
    infoPanel.add(this.stsVersion, c);
    c.gridy = 4;
    this.credits = makeInfoTextAreaField("Additional Credits", " ");
    infoPanel.add(this.credits, c);
    c.gridy = 5;
    this.status = makeInfoTextAreaField("Status", " ");
    infoPanel.add(this.status, c);
    c.gridy = 6;
    DModSelectWindow.visitOnSteam = new JLabel("Visit on Steam");
    DModSelectWindow.visitOnSteam.setFont(DModSelectWindow.visitOnSteam.getFont().deriveFont(1));
    DModSelectWindow.visitOnSteam.setBorder(new EmptyBorder(0, 10, 0, 0));
    DModSelectWindow.visitOnSteam.setForeground(Color.BLUE);
    DModSelectWindow.visitOnSteam.setEnabled(false);
    DModSelectWindow.visitOnSteam.setVisible(false);
    infoPanel.add(DModSelectWindow.visitOnSteam, c);
    c.gridy = 7;
    DModSelectWindow.joinDiscord = new JLabel("Join on Discord");
    DModSelectWindow.joinDiscord.setFont(DModSelectWindow.joinDiscord.getFont().deriveFont(1));
    DModSelectWindow.joinDiscord.setBorder(new EmptyBorder(0, 10, 0, 0));
    DModSelectWindow.joinDiscord.setForeground(Color.decode("#9656ce"));
    DModSelectWindow.joinDiscord.setEnabled(false);
    DModSelectWindow.joinDiscord.setVisible(false);
    infoPanel.add(DModSelectWindow.joinDiscord, c);
    c.gridy = 8;
    DModSelectWindow.supportOnPatreon = new JLabel("Support on Patreon");
    DModSelectWindow.supportOnPatreon.setFont(DModSelectWindow.supportOnPatreon.getFont().deriveFont(1));
    DModSelectWindow.supportOnPatreon.setBorder(new EmptyBorder(0, 10, 0, 0));
    DModSelectWindow.supportOnPatreon.setForeground(Color.decode("#f96854"));
    DModSelectWindow.supportOnPatreon.setEnabled(false);
    DModSelectWindow.supportOnPatreon.setVisible(false);
    infoPanel.add(DModSelectWindow.supportOnPatreon, c);
    c.gridx = 0;
    c.gridy = 0;
    c.gridheight = 10;
    c.weightx = 1.0D;
    c.weighty = 1.0D;
    this.description = makeInfoTextAreaField("Description", " ");
    infoPanel.add(this.description, c);
    panel.add(infoPanel, "Center");
    return panel;
  }
  
  private JLabel makeInfoLabelField(String title, String value) {
    JLabel label = new JLabel(value);
    TitledBorder border = BorderFactory.createTitledBorder(title);
    border.setTitleFont(border.getTitleFont().deriveFont(1));
    label.setFont(label.getFont().deriveFont(0));
    label.setBorder(BorderFactory.createCompoundBorder(border, 
          
          BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    return label;
  }
  
  private JTextArea makeInfoTextAreaField(String title, String value) {
    JTextArea label = new JTextArea(value);
    TitledBorder border = BorderFactory.createTitledBorder(title);
    border.setTitleFont(border.getTitleFont().deriveFont(1));
    label.setBorder(BorderFactory.createCompoundBorder(border, 
          
          BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    label.setEditable(false);
    label.setAlignmentX(0.0F);
    label.setLineWrap(true);
    label.setWrapStyleWord(true);
    label.setOpaque(false);
    label.setFont(border.getTitleFont().deriveFont(0).deriveFont(11.0F));
    return label;
  }
  
  private JPanel makeModBannerPanel() {
    this.modBannerNoticePanel = new JPanel();
    this.modBannerNoticePanel.setLayout(new GridLayout(0, 1));
    this.modBannerNoticePanel.setBorder(BorderFactory.createEmptyBorder(1, 0, 2, 0));
    this.modUpdateBanner = new JLabel();
    this.modUpdateBanner.setIcon(ICON_WARNING);
    this.modUpdateBanner.setText("<html>An update is available for this mod.</html>");
    this.modUpdateBanner.setHorizontalAlignment(0);
    this.modUpdateBanner.setOpaque(true);
    this.modUpdateBanner.setBackground(new Color(255, 193, 7));
    this.modUpdateBanner.setBorder(new EmptyBorder(5, 5, 5, 5));
    return this.modBannerNoticePanel;
  }
  
  private JPanel makeStatusPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setBorder(new MatteBorder(1, 0, 0, 0, Color.darkGray));
    JLabel sts_version = new JLabel("Slay the Spire version: " + Loader.STS_VERSION);
    if (Loader.STS_BETA)
      sts_version.setText(sts_version.getText() + " BETA"); 
    sts_version.setHorizontalAlignment(4);
    panel.add(sts_version, "East");
    JPanel bottomPanel = new JPanel(new GridLayout(1, 0));
    bottomPanel.add(DUIHelp.MakeMultiPlayButton(this));
    bottomPanel.add(DUIHelp.MakeQuickPlayButton(this));
    JCheckBox debugCheck = new JCheckBox("Debug");
    if (Loader.DEBUG)
      debugCheck.setSelected(true); 
    debugCheck.addActionListener(event -> {
          Loader.DEBUG = debugCheck.isSelected();
          Loader.MTS_CONFIG.setBool("debug", Loader.DEBUG);
          try {
            Loader.MTS_CONFIG.save();
          } catch (IOException e) {
            e.printStackTrace();
          } 
        });
    bottomPanel.add(debugCheck);
    panel.add(bottomPanel, "West");
    return panel;
  }
  
  private JPanel makeTopPanel() {
    return ModSelectWindowExtensions.makeTopToolbar();
  }
  
  @Deprecated
  private JPanel makeTopPanelBanner() {
    this.bannerNoticePanel = new JPanel();
    this.bannerNoticePanel.setLayout(new GridLayout(0, 1));
    if (Loader.STS_BETA) {
      this.betaWarningBanner = new JLabel();
      this.betaWarningBanner.setIcon(ICON_ERROR);
      this.betaWarningBanner.setText("<html>You are on the Slay the Spire beta branch.<br/>If mods are not working correctly,<br/>switch to the main branch for best results.</html>");
      this.betaWarningBanner.setHorizontalAlignment(0);
      this.betaWarningBanner.setOpaque(true);
      this.betaWarningBanner.setBackground(new Color(255, 80, 80));
      this.betaWarningBanner.setBorder(new EmptyBorder(5, 5, 5, 5));
      this.bannerNoticePanel.add(this.betaWarningBanner);
    } 
    this.mtsUpdateBanner = new JLabel();
    this.mtsUpdateBanner.setIcon(ICON_WARNING);
    this.mtsUpdateBanner.setText("<html>An update for ModTheSpire is available.<br/>Click here to open the download page.</html>");
    this.mtsUpdateBanner.setHorizontalAlignment(0);
    this.mtsUpdateBanner.setOpaque(true);
    this.mtsUpdateBanner.setBackground(new Color(255, 193, 7));
    this.mtsUpdateBanner.setBorder(new EmptyBorder(5, 5, 5, 5));
    this.mtsUpdateBanner.setCursor(Cursor.getPredefinedCursor(12));
    return this.bannerNoticePanel;
  }
  
  private void setMTSUpdateAvailable(final URL url) {
    this.bannerNoticePanel.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            if (Desktop.isDesktopSupported())
              try {
                Desktop.getDesktop().browse(url.toURI());
              } catch (IOException|URISyntaxException ex) {
                ex.printStackTrace();
              }  
          }
        });
    this.bannerNoticePanel.add(this.mtsUpdateBanner);
    pack();
    repaint();
  }
  
  void saveWindowDimensions(Dimension d) {
    Loader.MTS_CONFIG.setInt("width", d.width);
    Loader.MTS_CONFIG.setInt("height", d.height);
    try {
      Loader.MTS_CONFIG.save();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  void saveWindowMaximize() {
    Loader.MTS_CONFIG.setBool("maximize", this.isMaximized);
    try {
      Loader.MTS_CONFIG.save();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  void saveWindowLocation() {
    Point loc = getLocationOnScreen();
    Loader.MTS_CONFIG.setInt("x", loc.x);
    Loader.MTS_CONFIG.setInt("y", loc.y);
    try {
      Loader.MTS_CONFIG.save();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  boolean isInScreenBounds(Point location, Rectangle size) {
    size.setLocation(location);
    return isInScreenBounds(size);
  }
  
  boolean isInScreenBounds(Rectangle location) {
    for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
      Rectangle bounds = gd.getDefaultConfiguration().getBounds();
      bounds.x -= 10;
      bounds.width += 20;
      bounds.y -= 10;
      bounds.height += 20;
      if (bounds.contains(location))
        return true; 
    } 
    return false;
  }
  
  void setModInfo(final ModInfo info) {
    this.currentModInfo = info;
    this.name.setTitle(info.Name);
    this.authors.setText(String.join(", ", (CharSequence[])info.Authors));
    if (info.ModVersion != null) {
      this.modVersion.setText(info.ModVersion.toString());
    } else {
      this.modVersion.setText(" ");
    } 
    if (info.MTS_Version != null) {
      this.mtsVersion.setText(info.MTS_Version + "+");
    } else {
      this.mtsVersion.setText(" ");
    } 
    if (info.STS_Version != null && !info.STS_Version.isEmpty()) {
      this.stsVersion.setText(info.STS_Version);
    } else {
      this.stsVersion.setText(" ");
    } 
    this.description.setText(info.Description);
    this.credits.setText(info.Credits);
    this.status.setText(info.statusMsg);
    String steamModId = DracosUtil.GetModPublicId(info);
    DModSelectWindow.visitOnSteam.setEnabled((steamModId != null && !steamModId.isEmpty()));
    DModSelectWindow.visitOnSteam.setVisible((steamModId != null && !steamModId.isEmpty()));
    for (MouseListener l : DModSelectWindow.visitOnSteam.getMouseListeners())
      DModSelectWindow.visitOnSteam.removeMouseListener(l); 
    if (DModSelectWindow.visitOnSteam.isEnabled())
      DModSelectWindow.visitOnSteam.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
              String modID = DracosUtil.GetModPublicId(info);
              if (modID != null) {
                Desktop desktop = Desktop.getDesktop();
                try {
                  URI uri = new URI("https://steamcommunity.com/sharedfiles/filedetails/?id=" + modID);
                  desktop.browse(uri);
                } catch (IOException ex) {
                  ex.printStackTrace();
                } catch (URISyntaxException ex) {
                  ex.printStackTrace();
                } 
              } 
            }
            
            public void mousePressed(MouseEvent e) {}
            
            public void mouseReleased(MouseEvent e) {}
            
            public void mouseEntered(MouseEvent e) {}
            
            public void mouseExited(MouseEvent e) {}
          }); 
    final String discordUrl = (info.modInfoExtended == null) ? "" : info.modInfoExtended.discord_url;
    DModSelectWindow.joinDiscord.setEnabled((discordUrl != null && !discordUrl.isEmpty()));
    DModSelectWindow.joinDiscord.setVisible((discordUrl != null && !discordUrl.isEmpty()));
    for (MouseListener l : DModSelectWindow.joinDiscord.getMouseListeners())
      DModSelectWindow.joinDiscord.removeMouseListener(l); 
    if (DModSelectWindow.joinDiscord.isEnabled())
      DModSelectWindow.joinDiscord.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
              Desktop desktop = Desktop.getDesktop();
              try {
                URI uri = new URI(discordUrl);
                desktop.browse(uri);
              } catch (IOException ex) {
                ex.printStackTrace();
              } catch (URISyntaxException ex) {
                ex.printStackTrace();
              } 
            }
            
            public void mousePressed(MouseEvent e) {}
            
            public void mouseReleased(MouseEvent e) {}
            
            public void mouseEntered(MouseEvent e) {}
            
            public void mouseExited(MouseEvent e) {}
          }); 
    final String patreonUrl = (info.modInfoExtended == null) ? "" : info.modInfoExtended.patreon_url;
    DModSelectWindow.supportOnPatreon.setEnabled((patreonUrl != null && !patreonUrl.isEmpty()));
    DModSelectWindow.supportOnPatreon.setVisible((patreonUrl != null && !patreonUrl.isEmpty()));
    for (MouseListener l : DModSelectWindow.supportOnPatreon.getMouseListeners())
      DModSelectWindow.supportOnPatreon.removeMouseListener(l); 
    if (DModSelectWindow.supportOnPatreon.isEnabled())
      DModSelectWindow.supportOnPatreon.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
              Desktop desktop = Desktop.getDesktop();
              try {
                URI uri = new URI(patreonUrl);
                desktop.browse(uri);
              } catch (IOException ex) {
                ex.printStackTrace();
              } catch (URISyntaxException ex) {
                ex.printStackTrace();
              } 
            }
            
            public void mousePressed(MouseEvent e) {}
            
            public void mouseReleased(MouseEvent e) {}
            
            public void mouseEntered(MouseEvent e) {}
            
            public void mouseExited(MouseEvent e) {}
          }); 
    setModUpdateBanner(info);
    repaint();
  }
  
  synchronized void setModUpdateBanner(ModInfo info) {
    if (this.currentModInfo != null && this.currentModInfo.equals(info)) {
      boolean needsUpdate = false;
      if (MODUPDATES != null)
        for (ModUpdate modUpdate : MODUPDATES) {
          if (modUpdate.info.equals(info)) {
            needsUpdate = true;
            break;
          } 
        }  
      if (needsUpdate) {
        this.modBannerNoticePanel.add(this.modUpdateBanner);
      } else {
        this.modBannerNoticePanel.remove(this.modUpdateBanner);
      } 
    } 
  }
  
  public void startCheckingForMTSUpdate() {
    (new Thread(() -> {
          try {
            GithubUpdateChecker githubUpdateChecker = new GithubUpdateChecker("kiooeht", "ModTheSpire");
            if (githubUpdateChecker.isNewerVersionAvailable(Loader.MTS_VERSION)) {
              URL latestReleaseURL = githubUpdateChecker.getLatestReleaseURL();
              setMTSUpdateAvailable(latestReleaseURL);
              return;
            } 
          } catch (IllegalArgumentException e) {
            System.out.println("ERROR: ModTheSpire: " + e.getMessage());
          } catch (IOException iOException) {}
        })).start();
  }
  
  @Deprecated
  public void startCheckingForModUpdates(JButton updatesBtn) {
    updatesBtn.setIcon(ICON_LOAD);
    (new Thread(() -> {
          for (int i = 0; i < this.info.length; i++) {
            if ((this.info[i]).UpdateJSON != null && !(this.info[i]).UpdateJSON.isEmpty())
              this.modList.setUpdateIcon(this.info[i], UpdateIconType.CHECKING); 
          } 
          boolean anyNeedUpdates = false;
          MODUPDATES = new ArrayList<>();
          for (int j = 0; j < this.info.length; j++) {
            if ((this.info[j]).UpdateJSON != null && !(this.info[j]).UpdateJSON.isEmpty())
              try {
                GithubUpdateChecker githubUpdateChecker = new GithubUpdateChecker((this.info[j]).UpdateJSON);
                if (githubUpdateChecker.isNewerVersionAvailable((this.info[j]).ModVersion)) {
                  anyNeedUpdates = true;
                  MODUPDATES.add(new ModUpdate(this.info[j], githubUpdateChecker.getLatestReleaseURL(), githubUpdateChecker.getLatestDownloadURL()));
                  setModUpdateBanner(this.info[j]);
                  revalidate();
                  repaint();
                  this.modList.setUpdateIcon(this.info[j], UpdateIconType.UPDATE_AVAILABLE);
                } else {
                  this.modList.setUpdateIcon(this.info[j], UpdateIconType.UPTODATE);
                } 
              } catch (IllegalArgumentException e) {
                System.out.println("ERROR: " + (this.info[j]).Name + ": " + e.getMessage());
              } catch (IOException e) {
                System.out.println(e);
              }  
          } 
          if (anyNeedUpdates) {
            updatesBtn.setIcon(ICON_WARNING);
            updatesBtn.setToolTipText("Mod updates are available.");
            for (ActionListener listener : updatesBtn.getActionListeners())
              updatesBtn.removeActionListener(listener);
            updatesBtn.addActionListener(e -> {
              UpdateWindow win;
              win = new UpdateWindow(this);
              win.setVisible(true);
            });
          } else {
            updatesBtn.setIcon(ICON_UPDATE);
          } 
        })).start();
  }
  
  public void warnAboutMissingVersions() {
    for (ModInfo modInfo : this.info) {
      if (modInfo.ModVersion == null)
        JOptionPane.showMessageDialog(null, modInfo.Name + " has a missing or bad version number.\nGo yell at the author to fix it.", "Warning", 2); 
    } 
  }
}
