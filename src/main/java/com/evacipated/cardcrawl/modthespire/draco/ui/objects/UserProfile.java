package com.evacipated.cardcrawl.modthespire.draco.ui.objects;

import com.evacipated.cardcrawl.modthespire.draco.modlist.ModListProperties;
import java.awt.Color;

public class UserProfile {
  public String displayName;
  
  public String id;
  
  public Color backgroundColor;
  
  public ModListProperties properties = new ModListProperties();
  
  public UserProfile(String displayName) {
    this(displayName, displayName);
  }
  
  public UserProfile(String displayName, String id) {
    this.displayName = displayName;
    this.id = id;
  }
  
  public String getDisplayName() {
    return this.displayName;
  }
  
  public String getId() {
    return this.id;
  }
  
  public Color getBackgroundColor() {
    return this.backgroundColor;
  }
  
  public String toString() {
    return this.displayName;
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof UserProfile))
      return false; 
    UserProfile p = (UserProfile)obj;
    return p.getId().equals(this.id);
  }
  
  public static UserProfile[] FromStringArray(String[] strings) {
    UserProfile[] toReturn = new UserProfile[strings.length];
    for (int i = 0; i < strings.length; i++)
      toReturn[i] = new UserProfile(strings[i]); 
    return toReturn;
  }
  
  public void setId(String s) {
    this.id = s;
  }
}
