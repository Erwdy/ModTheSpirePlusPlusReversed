package com.evacipated.cardcrawl.modthespire;

import com.evacipated.cardcrawl.modthespire.draco.loadingscreen.LoadingScreenBuilder;
import com.evacipated.cardcrawl.modthespire.draco.mods.DLoader;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamUGCDetails;
import com.evacipated.cardcrawl.modthespire.draco.ui.CrashExtensions;
import com.evacipated.cardcrawl.modthespire.draco.ui.DUIHelp;
import com.evacipated.cardcrawl.modthespire.draco.ui.ThemeManager;
import com.evacipated.cardcrawl.modthespire.draco.util.MtSPPConfigManager;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.steam.SteamSearch;
import com.evacipated.cardcrawl.modthespire.ui.ModSelectWindow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vdurmont.semver4j.Semver;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import javassist.ClassPath;
import javassist.ClassPool;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.objectweb.asm.ClassReader;

public class Loader {
  public static boolean DEBUG = false;
  
  public static boolean OUT_JAR = false;
  
  public static boolean PACKAGE = false;
  
  public static boolean CLOSE_WHEN_FINISHED = false;
  
  public static Semver MTS_VERSION;
  
  public static String MOD_DIR = "mods/";
  
  public static String BETA_SUBDIR = "beta/";
  
  public static String STS_JAR = "desktop-1.0.jar";
  
  private static String MAC_STS_JAR = "SlayTheSpire.app/Contents/Resources/" + STS_JAR;
  
  private static String STS_JAR2 = "SlayTheSpire.jar";
  
  public static String COREPATCHES_JAR = "/corepatches.jar";
  
  private static final String COREPATCHES_LWJGL3_JAR = "/corepatches-lwjgl3.jar";
  
  static String KOTLIN_JAR = "/kotlin.jar";
  
  static String LWJGL3_JAR = "/lwjgl3.jar";
  
  public static String STS_PATCHED_JAR = "desktop-1.0-patched.jar";
  
  public static String JRE_51_DIR = "jre1.8.0_51";
  
  public static ModInfo[] MODINFOS;
  
  public static ModInfo[] ALLMODINFOS;
  
  private static ClassPool POOL;
  
  private static List<SteamSearch.WorkshopInfo> WORKSHOP_INFOS;
  
  public static List<DSteamUGCDetails> NEW_WORKSHOP_INFOS;
  
  public static SpireConfig MTS_CONFIG;
  
  public static String STS_VERSION = null;
  
  public static boolean STS_BETA = false;
  
  public static boolean allowBeta = false;
  
  public static String profileArg = null;
  
  public static List<String> manualModIds = null;
  
  public static String[] ARGS;
  
  public static boolean SKIP_INTRO = false;
  
  public static boolean LWJGL3_ENABLED = false;
  
  public static ModSelectWindow ex;
  
  private static final List<URL> extraJars = new ArrayList<>();
  
  public static boolean isModLoaded(String modID) {
    for (int i = 0; i < MODINFOS.length; i++) {
      if (modID.equals((MODINFOS[i]).ID))
        return true; 
    } 
    return false;
  }
  
  public static boolean isModSideloaded(String modID) {
    modID = "__sideload_" + modID;
    for (int i = 0; i < MODINFOS.length; i++) {
      if (modID.equals((MODINFOS[i]).ID))
        return true; 
    } 
    return false;
  }
  
  public static boolean isModLoadedOrSideloaded(String modID) {
    return (isModLoaded(modID) || isModSideloaded(modID));
  }
  
  public static ClassPool getClassPool() {
    return POOL;
  }
  
  public static List<SteamSearch.WorkshopInfo> getWorkshopInfos() {
    return WORKSHOP_INFOS;
  }
  
  public static void main(String[] args) {
    System.setProperty("sun.java2d.d3d", "false");
    List<String> argList = Arrays.asList(args);
    if (!argList.contains("--jre51") && (new File(JRE_51_DIR)).exists()) {
      System.out.println("JRE 51 exists, restarting using it...");
      try {
        String path = Loader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = URLDecoder.decode(path, "utf-8");
        path = (new File(path)).getPath();
        String[] newArgs = new String[args.length + 4];
        newArgs[0] = SteamSearch.findJRE51();
        newArgs[1] = "-jar";
        newArgs[2] = path;
        newArgs[3] = "--jre51";
        System.arraycopy(args, 0, newArgs, 4, args.length);
        ProcessBuilder pb = new ProcessBuilder(newArgs);
        pb.redirectOutput(new File("sendToDevs", "mts_process_launch.log"));
        pb.redirectErrorStream(true);
        pb.start();
        System.exit(0);
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(3);
      } 
    } else if (argList.contains("--jre51")) {
      System.out.println("Launched using JRE 51");
    } 
    ARGS = args;
    try {
      Properties defaults = new Properties();
      defaults.setProperty("debug", Boolean.toString(false));
      defaults.setProperty("out-jar", Boolean.toString(false));
      defaults.setProperty("package", Boolean.toString(false));
      defaults.setProperty("close-when-finished", Boolean.toString(false));
      defaults.setProperty("allow-beta", Boolean.toString(true));
      defaults.setProperty("skip-launcher", Boolean.toString(false));
      defaults.setProperty("skip-intro", Boolean.toString(false));
      defaults.setProperty("mods", "");
      defaults.putAll(ModSelectWindow.getDefaults());
      MTS_CONFIG = new SpireConfig(null, "ModTheSpire", defaults);
      MtSPPConfigManager.init();
    } catch (IOException e) {
      e.printStackTrace();
    } 
    DEBUG = MTS_CONFIG.getBool("debug");
    OUT_JAR = MTS_CONFIG.getBool("out-jar");
    PACKAGE = MTS_CONFIG.getBool("package");
    CLOSE_WHEN_FINISHED = MTS_CONFIG.getBool("close-when-finished");
    allowBeta = MTS_CONFIG.getBool("allow-beta");
    boolean skipLauncher = MTS_CONFIG.getBool("skip-launcher");
    SKIP_INTRO = MTS_CONFIG.getBool("skip-intro");
    profileArg = MTS_CONFIG.getString("profile");
    String modIds = MTS_CONFIG.getString("mods");
    if (!LWJGL3_ENABLED)
      LWJGL3_ENABLED = MTS_CONFIG.getBool("imgui"); 
    if (argList.contains("--debug"))
      DEBUG = true; 
    if (argList.contains("--out-jar"))
      OUT_JAR = true; 
    if (argList.contains("--package"))
      PACKAGE = true; 
    if (argList.contains("--close-when-finished"))
      CLOSE_WHEN_FINISHED = true; 
    if (argList.contains("--allow-beta"))
      allowBeta = true; 
    if (argList.contains("--skip-launcher"))
      skipLauncher = true; 
    if (argList.contains("--skip-intro"))
      SKIP_INTRO = true; 
    if (argList.contains("--imgui"))
      LWJGL3_ENABLED = true; 
    ThemeManager.loadDefaultTheme();
    int profileArgIndex = argList.indexOf("--profile");
    if (profileArgIndex >= 0 && argList.size() > profileArgIndex + 1)
      profileArg = argList.get(profileArgIndex + 1); 
    int modIdsIndex = argList.indexOf("--mods");
    if (modIdsIndex >= 0 && argList.size() > modIdsIndex + 1)
      modIds = argList.get(modIdsIndex + 1); 
    if (!modIds.isEmpty()) {
      manualModIds = Arrays.asList(modIds.split(","));
      profileArg = null;
      skipLauncher = true;
    } 
    loadMTSVersion();
    try {
      String thisJarName = (new File(Loader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getName();
      if (thisJarName.equals(STS_JAR))
        STS_JAR = STS_JAR2; 
    } catch (URISyntaxException uRISyntaxException) {}
    File tmp = new File(STS_JAR);
    if (!tmp.exists()) {
      String steamJar = SteamSearch.findDesktopJar();
      if (steamJar != null && (new File(steamJar)).exists()) {
        STS_JAR = steamJar;
      } else {
        tmp = new File(MAC_STS_JAR);
        checkFileInfo(tmp);
        if (!tmp.exists()) {
          checkFileInfo(new File("SlayTheSpire.app"));
          checkFileInfo(new File("SlayTheSpire.app/Contents"));
          checkFileInfo(new File("SlayTheSpire.app/Contents/Resources"));
          JOptionPane.showMessageDialog(null, "Unable to find '" + STS_JAR + "'");
          return;
        } 
        System.out.println("Using Mac version at: " + MAC_STS_JAR);
        STS_JAR = MAC_STS_JAR;
      } 
    } 
    DLoader.RefreshModList();
    findGameVersion();
    boolean finalSkipLauncher = skipLauncher;
    EventQueue.invokeLater(() -> {
          ex = new ModSelectWindow(ALLMODINFOS, finalSkipLauncher);
          DUIHelp.RepaintUI();
          DLoader.InitializeModCompatibility();
          ex.setVisible(true);
          ex.warnAboutMissingVersions();
          String java_version = System.getProperty("java.version");
          if (!java_version.startsWith("1.8")) {
            String msg = "ModTheSpire requires Java version 8 to run properly.\nYou are currently using Java " + java_version;
            JOptionPane.showMessageDialog(null, msg, "Warning", 2);
          } 
          ex.startCheckingForMTSUpdate();
        });
  }
  
  public static void closeWindow() {
    ex.dispatchEvent(new WindowEvent((Window)ex, 201));
  }
  
  public static void restoreWindowOnCrash() {
    ex.setState(0);
    ex.setVisible(true);
    ex.toFront();
    ex.requestFocus();
  }
  
  public static void runMods(File[] modJars) {
    if (DEBUG) {
      System.out.println("Running with debug mode turned ON...");
      System.out.println();
    } 
    try {
      ModInfo[] modInfos = buildInfoArray(modJars, manualModIds);
      checkDependencies(modInfos);
      modInfos = orderDependencies(modInfos);
      MODINFOS = modInfos;
      printMTSInfo(System.out);
      unpackJar(KOTLIN_JAR);
      if (LWJGL3_ENABLED) {
        COREPATCHES_JAR = "/corepatches-lwjgl3.jar";
        unpackJar(LWJGL3_JAR);
      } 
      MTSClassLoader loader = new MTSClassLoader(Loader.class.getResourceAsStream(COREPATCHES_JAR), buildUrlArray(MODINFOS), Loader.class.getClassLoader());
      if (modJars.length > 0) {
        MTSClassLoader tmpPatchingLoader = new MTSClassLoader(Loader.class.getResourceAsStream(COREPATCHES_JAR), buildUrlArray(MODINFOS), Loader.class.getClassLoader());
        System.out.println("Begin patching...");
        try {
          MTSClassPool pool = new MTSClassPool(tmpPatchingLoader);
          LoadingScreenBuilder.setLoadTextAndValue(0);
          MODINFOS = Patcher.sideloadMods(tmpPatchingLoader, loader, pool, ALLMODINFOS, MODINFOS);
          System.out.printf("Patching enums...", new Object[0]);
          LoadingScreenBuilder.setLoadTextAndValue(20);
          Patcher.patchEnums(tmpPatchingLoader, pool, new URL[] { Loader.class.getResource(COREPATCHES_JAR) });
          Patcher.patchEnums(tmpPatchingLoader, pool, MODINFOS);
          System.out.println("Done.");
          System.out.println("Finding core patches...");
          LoadingScreenBuilder.setLoadTextAndValue(40);
          Patcher.injectPatches(tmpPatchingLoader, pool, Patcher.findPatches(new URL[] { Loader.class.getResource(COREPATCHES_JAR) }));
          System.out.println("Finding patches...");
          Patcher.injectPatches(tmpPatchingLoader, pool, Patcher.findPatches(MODINFOS));
          LoadingScreenBuilder.setLoadTextAndValue(60);
          Patcher.patchOverrides(tmpPatchingLoader, pool, MODINFOS);
          LoadingScreenBuilder.setLoadTextAndValue(60);
          Patcher.finalizePatches(tmpPatchingLoader);
          ClassPath cp = Patcher.compilePatches(loader, pool);
          tmpPatchingLoader.close();
          pool.resetClassLoader(loader);
          pool.insertClassPath(cp);
          POOL = pool;
          POOL.childFirstLookup = true;
          System.out.printf("Busting enums...", new Object[0]);
          LoadingScreenBuilder.setLoadTextAndValue(80);
          Patcher.bustEnums(loader, new URL[] { Loader.class.getResource(COREPATCHES_JAR) });
          Patcher.bustEnums(loader, MODINFOS);
          System.out.println("Done.");
          System.out.println();
          LoadingScreenBuilder.setLoadTextAndValue(100);
          if (PACKAGE) {
            System.out.println("Creating prepackaged JAR...");
            PackageJar.packageJar(pool, "desktop-1.0-modded.jar");
            System.out.println("Done.");
            return;
          } 
          if (OUT_JAR) {
            System.out.printf("Dumping JAR...", new Object[0]);
            OutJar.dumpJar(pool, STS_PATCHED_JAR);
            System.out.println("Done.");
            return;
          } 
          System.out.printf("Setting isModded = true...", new Object[0]);
          System.out.flush();
          Class<?> Settings = loader.loadClass("com.megacrit.cardcrawl.core.Settings");
          Field isModded = Settings.getDeclaredField("isModded");
          isModded.set(null, Boolean.valueOf(true));
          System.out.println("Done.");
          System.out.println();
          Field isDev = Settings.getDeclaredField("isDev");
          isDev.set(null, Boolean.valueOf(false));
          System.out.printf("Adding ModTheSpire to version...", new Object[0]);
          System.out.flush();
          Class<?> CardCrawlGame = loader.loadClass("com.megacrit.cardcrawl.core.CardCrawlGame");
          Field VERSION_NUM = CardCrawlGame.getDeclaredField("VERSION_NUM");
          String oldVersion = (String)VERSION_NUM.get(null);
          VERSION_NUM.set(null, oldVersion + " [ModTheSpire " + MTS_VERSION + "]");
          System.out.println("Done.");
          System.out.println();
          System.out.println("Initializing mods...");
          Patcher.initializeMods(loader, MODINFOS);
          System.out.println("Done.");
          System.out.println();
        } catch (Exception e) {
          LoadingScreenBuilder.showLogWindow();
          e.printStackTrace();
          CrashExtensions.showCrashReportToolbar(e);
          return;
        } 
      } 
      SwingUtilities.invokeLater(LoadingScreenBuilder::showLogWindow);
      System.out.println("Starting game...");
      Class<?> cls = loader.loadClass("com.megacrit.cardcrawl.desktop.DesktopLauncher");
      Method method = cls.getDeclaredMethod("main", new Class[] { String[].class });
      method.invoke(null, new Object[] { ARGS });
      if (!DEBUG)
        (new Timer()).schedule(new TimerTask() {
              public void run() {
                Loader.ex.setState(1);
              }
            },  1000L); 
    } catch (MissingDependencyException e) {
      System.err.println("ERROR: " + e.getMessage());
      JOptionPane.showMessageDialog(null, e.getMessage(), "Missing Dependency", 0);
    } catch (DuplicateModIDException e) {
      System.err.println("ERROR: " + e.getMessage());
      JOptionPane.showMessageDialog(null, e.getMessage(), "Duplicate Mod ID", 0);
    } catch (MissingModIDException e) {
      System.err.println("ERROR: " + e.getMessage());
      JOptionPane.showMessageDialog(null, e.getMessage(), "Missing Mod ID", 0);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  static void loadMTSVersion() {
    loadMTSVersion(null);
  }
  
  static void loadMTSVersion(String suffix) {
    try {
      Properties properties = new Properties();
      properties.load(Loader.class.getResourceAsStream("/META-INF/version.prop"));
      String version = properties.getProperty("version");
      if (suffix != null)
        version = version + "+" + suffix; 
      MTS_VERSION = ModInfo.safeVersion(version);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    } 
  }
  
  public static void setGameVersion(String versionString) {
    if (versionString.startsWith("(") && versionString.endsWith(")"))
      versionString = versionString.substring(1, versionString.length() - 1); 
    STS_VERSION = versionString;
  }
  
  private static void findGameVersion() {
    try {
      URLClassLoader tmpLoader = new URLClassLoader(new URL[] { (new File(STS_JAR)).toURI().toURL() });
      InputStream in = tmpLoader.getResourceAsStream("com/megacrit/cardcrawl/core/CardCrawlGame.class");
      ClassReader classReader = new ClassReader(in);
      classReader.accept(new GameVersionFinder(), 0);
      InputStream in2 = tmpLoader.getResourceAsStream("com/megacrit/cardcrawl/core/Settings.class");
      ClassReader classReader2 = new ClassReader(in2);
      classReader2.accept(new GameBetaFinder(), 0);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  private static void unpackJar(String name) {
    try {
      Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), new String[] { "ModTheSpire" });
      File f = tmpDir.toFile();
      if (!f.exists())
        f.mkdirs(); 
      String filename = Paths.get(name, new String[0]).getFileName().toString();
      Path tmpFile = tmpDir.resolve(filename);
      InputStream input = Loader.class.getResourceAsStream(name);
      OutputStream output = new FileOutputStream(tmpFile.toFile());
      byte[] buf = new byte[8192];
      int length;
      while ((length = input.read(buf)) > 0)
        output.write(buf, 0, length); 
      output.close();
      input.close();
      extraJars.add(tmpFile.toUri().toURL());
    } catch (Exception e) {
      System.out.println("Failed to unpack " + name);
      e.printStackTrace();
    } 
  }
  
  private static URL[] buildUrlArray(ModInfo[] modInfos) throws MalformedURLException {
    List<URL> urls = new ArrayList<>(modInfos.length + 1);
    urls.addAll(extraJars);
    for (ModInfo modInfo : modInfos)
      urls.add(modInfo.jarURL); 
    urls.add((new File(STS_JAR)).toURI().toURL());
    return urls.<URL>toArray(new URL[0]);
  }
  
  private static ModInfo[] buildInfoArray(File[] modJars, List<String> modIds) throws MissingModIDException {
    ModInfo[] infos;
    if (modIds != null) {
      Map<String, ModInfo> infoMap = new HashMap<>();
      for (File modJar : modJars) {
        ModInfo info = ModInfo.ReadModInfo(modJar);
        if (info != null && info.ID != null && !info.ID.isEmpty())
          infoMap.put(info.ID, info); 
      } 
      infos = new ModInfo[modIds.size()];
      for (int i = 0; i < modIds.size(); i++) {
        ModInfo info = infoMap.get(modIds.get(i));
        if (info == null)
          throw new MissingModIDException((String)modIds.get(i)); 
        infos[i] = info;
      } 
    } else {
      infos = new ModInfo[modJars.length];
      for (int i = 0; i < modJars.length; i++)
        infos[i] = ModInfo.ReadModInfo(modJars[i]); 
    } 
    return infos;
  }
  
  public static File[] getAllModFiles(String directory) {
    File file = new File(directory);
    if (!file.exists() || !file.isDirectory())
      return new File[0]; 
    File[] files = file.listFiles(new FilenameFilter() {
          public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".jar");
          }
        });
    if (files == null || files.length == 0)
      return new File[0]; 
    return files;
  }
  
  private static ModInfo[] getAllMods(List<SteamSearch.WorkshopInfo> workshopInfos) {
    List<ModInfo> modInfos = new ArrayList<>();
    if (STS_BETA)
      for (File f : getAllModFiles(MOD_DIR + BETA_SUBDIR)) {
        ModInfo info = ModInfo.ReadModInfo(f);
        if (info != null && 
          modInfos.stream().noneMatch(i -> (i.ID == null || i.ID.equals(info.ID))))
          modInfos.add(info); 
      }  
    for (File f : getAllModFiles(MOD_DIR)) {
      ModInfo info = ModInfo.ReadModInfo(f);
      if (info != null && 
        modInfos.stream().noneMatch(i -> (i.ID == null || i.ID.equals(info.ID))))
        modInfos.add(info); 
    } 
    BiConsumer<File, Boolean> lambda = (f, beta) -> {
        ModInfo info = ModInfo.ReadModInfo(f);
        if (info != null) {
          info.UpdateJSON = null;
          info.isWorkshop = true;
          boolean doAdd = true;
          Iterator<ModInfo> it = modInfos.iterator();
          while (it.hasNext()) {
            ModInfo modInfo = it.next();
            if (modInfo.ID != null && modInfo.ID.equals(info.ID)) {
              if (modInfo.ModVersion == null || info.ModVersion == null) {
                doAdd = false;
                break;
              } 
              if (info.ModVersion.isGreaterThan(modInfo.ModVersion)) {
                it.remove();
                continue;
              } 
              doAdd = false;
              break;
            } 
          } 
          if (doAdd)
            modInfos.add(info); 
        } 
      };
    for (SteamSearch.WorkshopInfo workshopInfo : workshopInfos) {
      for (File f : getAllModFiles(workshopInfo.getInstallPath()))
        lambda.accept(f, Boolean.valueOf(false)); 
      if (STS_BETA)
        for (File f : getAllModFiles(Paths.get(workshopInfo.getInstallPath(), new String[] { BETA_SUBDIR }).toString()))
          lambda.accept(f, Boolean.valueOf(true));  
    } 
    modInfos.sort(Comparator.comparing(m -> m.Name));
    return modInfos.<ModInfo>toArray(new ModInfo[0]);
  }
  
  public static void printMTSInfo(PrintStream out) {
    out.println("Version Info:");
    out.printf(" - Java version (%s)\n", new Object[] { System.getProperty("java.version") });
    out.printf(" - Slay the Spire (%s)", new Object[] { STS_VERSION });
    if (STS_BETA)
      out.printf(" BETA", new Object[0]); 
    out.printf("\n", new Object[0]);
    out.printf(" - ModTheSpire (%s)\n", new Object[] { MTS_VERSION });
    out.printf("Mod list:\n", new Object[0]);
    for (ModInfo info : MODINFOS) {
      out.printf(" - %s", new Object[] { info.getIDName() });
      if (info.ModVersion != null)
        out.printf(" (%s)", new Object[] { info.ModVersion }); 
      out.println();
    } 
    out.println();
  }
  
  private static void checkDependencies(ModInfo[] modinfos) throws MissingDependencyException, DuplicateModIDException {
    Map<String, ModInfo> dependencyMap = new HashMap<>();
    for (ModInfo info : modinfos) {
      if (info.ID != null)
        if (!dependencyMap.containsKey(info.ID)) {
          dependencyMap.put(info.ID, info);
        } else {
          throw new DuplicateModIDException((ModInfo)dependencyMap.get(info.ID), info);
        }  
    } 
    for (ModInfo info : modinfos) {
      for (String dependency : info.Dependencies) {
        boolean has = false;
        for (ModInfo dependinfo : modinfos) {
          if (dependinfo.ID != null && dependinfo.ID.equals(dependency)) {
            has = true;
            break;
          } 
        } 
        if (!has)
          throw new MissingDependencyException(info, dependency); 
      } 
    } 
  }
  
  private static int findDependencyIndex(ModInfo[] modInfos, String dependencyID) {
    for (int i = 0; i < modInfos.length; i++) {
      if (modInfos[i] != null && (modInfos[i]).ID != null && 
        (modInfos[i]).ID.equals(dependencyID))
        return i; 
    } 
    return -1;
  }
  
  private static ModInfo[] orderDependencies(ModInfo[] modInfos) throws CyclicDependencyException {
    GraphTS<ModInfo> g = new GraphTS<>();
    for (ModInfo info : modInfos)
      g.addVertex(info); 
    for (int i = 0; i < modInfos.length; i++) {
      for (String dependency : (modInfos[i]).Dependencies)
        g.addEdge(findDependencyIndex(modInfos, dependency), i); 
      for (String optionalDependency : (modInfos[i]).OptionalDependencies) {
        int idx = findDependencyIndex(modInfos, optionalDependency);
        if (idx != -1)
          g.addEdge(idx, i); 
      } 
    } 
    g.tsortStable();
    return g.sortedArray.<ModInfo>toArray(new ModInfo[g.sortedArray.size()]);
  }
  
  private static void checkFileInfo(File file) {
    System.out.printf(file.getName() + ": ", new Object[0]);
    System.out.println(file.exists() ? "Exists" : "Does not exist");
    if (file.exists()) {
      System.out.printf("Type: ", new Object[0]);
      if (file.isFile()) {
        System.out.println("File");
      } else if (file.isDirectory()) {
        System.out.println("Directory");
        System.out.println("Contents:");
        for (File subfile : (File[])Objects.<File[]>requireNonNull(file.listFiles()))
          System.out.println("  " + subfile.getName()); 
      } else {
        System.out.println("Unknown");
      } 
    } 
  }
  
  @Deprecated
  private static void convertOldWorkshopInfoFiles(List<SteamSearch.WorkshopInfo> workshopInfos) {
    if (workshopInfos.isEmpty())
      return; 
    try {
      String pathUpdated = SpireConfig.makeFilePath(null, "WorkshopUpdated", "json");
      String pathLocations = SpireConfig.makeFilePath(null, "WorkshopLocations", "json");
      Files.deleteIfExists(Paths.get(pathUpdated, new String[0]));
      Files.deleteIfExists(Paths.get(pathLocations, new String[0]));
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      String data = gson.toJson(workshopInfos);
      Files.write(Paths.get(SpireConfig.makeFilePath(null, "WorkshopInfo", "json"), new String[0]), data.getBytes(), new java.nio.file.OpenOption[0]);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}
