package com.evacipated.cardcrawl.modthespire;

import com.evacipated.cardcrawl.modthespire.draco.mods.DModInfo;
import com.evacipated.cardcrawl.modthespire.draco.mods.ModInfoExtended;
import com.evacipated.cardcrawl.modthespire.draco.steam.objects.DSteamUGCDetails;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.SemverException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class ModInfo implements Serializable {
  private static final long serialVersionUID = 7452562412479584982L;
  
  public transient URL jarURL;
  
  public transient String statusMsg = " ";
  
  public transient boolean isWorkshop = false;
  
  public transient DSteamUGCDetails steamWorkshopDetails;
  
  public transient ModInfoExtended modInfoExtended = new ModInfoExtended();
  
  @SerializedName("modid")
  public String ID;
  
  @SerializedName("name")
  public String Name;
  
  @SerializedName("version")
  public Semver ModVersion;
  
  @SerializedName("author_list")
  public String[] Authors;
  
  @SerializedName("credits")
  public String Credits;
  
  @SerializedName("description")
  public String Description;
  
  @SerializedName("mts_version")
  public Semver MTS_Version;
  
  @SerializedName("sts_version")
  public String STS_Version;
  
  @SerializedName("dependencies")
  public String[] Dependencies;
  
  @SerializedName("optional_dependencies")
  public String[] OptionalDependencies;
  
  @SerializedName("update_json")
  public String UpdateJSON;
  
  private ModInfo() {
    this.Name = "";
    this.Authors = new String[0];
    this.Description = "";
    this.MTS_Version = safeVersion("0.0.0");
    this.STS_Version = null;
    this.Dependencies = new String[0];
    this.OptionalDependencies = new String[0];
    this.UpdateJSON = null;
  }
  
  public String getIDName() {
    if (this.ID == null || this.ID.isEmpty())
      return this.Name; 
    return this.ID;
  }
  
  private static void closeLoader(URLClassLoader loader) {
    try {
      if (loader != null)
        loader.close(); 
    } catch (Exception e) {
      System.out.println("Exception during loader.close(), URLClassLoader may be leaked. " + e.toString());
    } 
  }
  
  public static ModInfo ReadModInfo(File mod_jar) {
    Gson gson = (new GsonBuilder()).excludeFieldsWithModifiers(new int[] { 8, 128 }).registerTypeAdapter(Semver.class, new VersionDeserializer()).setDateFormat("MM-dd-yyyy").create();
    URLClassLoader loader = null;
    try {
      loader = new URLClassLoader(new URL[] { mod_jar.toURI().toURL() }, null);
      InputStream in = loader.getResourceAsStream("ModTheSpire.json");
      if (in == null) {
        ModInfo modInfo = ReadModInfoOld(mod_jar);
        modInfo.jarURL = mod_jar.toURI().toURL();
        return modInfo;
      } 
      ModInfo info = (ModInfo)gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), ModInfo.class);
      info.jarURL = mod_jar.toURI().toURL();
      in.close();
      info.modInfoExtended = DModInfo.loadExtendedModInfo(loader, gson);
      return info;
    } catch (Exception e) {
      System.out.println(mod_jar);
      e.printStackTrace();
    } finally {
      if (loader != null)
        closeLoader(loader); 
    } 
    return null;
  }
  
  private static ModInfo ReadModInfoOld(File mod_jar) {
    ModInfo info = new ModInfo();
    info.Name = mod_jar.getName();
    info.Name = info.Name.substring(0, info.Name.length() - 4);
    URLClassLoader loader = null;
    try {
      loader = new URLClassLoader(new URL[] { mod_jar.toURI().toURL() });
      Properties prop = new Properties();
      InputStream inProp = loader.getResourceAsStream("ModTheSpire.config");
      if (inProp != null) {
        prop.load(new InputStreamReader(inProp, StandardCharsets.UTF_8));
        info.Name = prop.getProperty("name");
        String author = prop.getProperty("author");
        if (author != null && !author.isEmpty())
          info.Authors = author.split(","); 
        info.MTS_Version = safeVersion(prop.getProperty("mts_version", "0.0.0"));
        info.Description = prop.getProperty("description");
        info.STS_Version = prop.getProperty("sts_version");
        inProp.close();
      } 
    } catch (Exception e) {
      System.out.println("ERROR: Failed to read Mod info from " + mod_jar.getName());
    } finally {
      closeLoader(loader);
    } 
    return info;
  }
  
  static class VersionDeserializer implements JsonDeserializer<Semver> {
    public Semver deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      try {
        return ModInfo.safeVersion(jsonElement.getAsJsonPrimitive().getAsString());
      } catch (SemverException e) {
        return null;
      } 
    }
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof ModInfo))
      return false; 
    ModInfo info = (ModInfo)obj;
    if (this.ID == null && info.ID == null)
      return Objects.equals(this.Name, info.Name); 
    return Objects.equals(this.ID, info.ID);
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.ID, this.Name });
  }
  
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeObject(this.ID);
    out.writeObject(this.Name);
    out.writeObject(this.ModVersion.toString());
    out.writeObject(this.Authors);
    out.writeObject(this.Credits);
    out.writeObject(this.Description);
    out.writeObject(this.MTS_Version.toString());
    out.writeObject(this.STS_Version);
    out.writeObject(this.Dependencies);
    out.writeObject(this.OptionalDependencies);
    out.writeObject(this.UpdateJSON);
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    this.ID = (String)in.readObject();
    this.Name = (String)in.readObject();
    this.ModVersion = safeVersion((String)in.readObject());
    this.Authors = (String[])in.readObject();
    this.Credits = (String)in.readObject();
    this.Description = (String)in.readObject();
    this.MTS_Version = safeVersion((String)in.readObject());
    this.STS_Version = (String)in.readObject();
    this.Dependencies = (String[])in.readObject();
    this.OptionalDependencies = (String[])in.readObject();
    this.UpdateJSON = (String)in.readObject();
  }
  
  public static Semver safeVersion(String verString) {
    return new Semver(verString, Semver.SemverType.NPM);
  }
}
