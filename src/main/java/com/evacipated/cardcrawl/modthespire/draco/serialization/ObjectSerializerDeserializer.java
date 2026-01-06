package com.evacipated.cardcrawl.modthespire.draco.serialization;

import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ObjectSerializerDeserializer {
  public static String SerializeAndCompress(Serializable obj) {
    return compress(serialize(obj));
  }
  
  public static Object DecompressAndDeserialize(String s) {
    return deserialize(decompress(s));
  }
  
  private static String serialize(Serializable obj) {
    return (new Gson()).toJson(obj) + "DracosClassSeparator" + obj.getClass().getName();
  }
  
  private static Object deserialize(String s) {
    String[] data = s.split("DracosClassSeparator");
    try {
      return (new Gson()).fromJson(data[0], Class.forName(data[1]));
    } catch (Exception e) {
      return null;
    } 
  }
  
  private static String compress(String s) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      GZIPOutputStream gos = new GZIPOutputStream(baos);
      gos.write(s.getBytes());
      gos.close();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (Exception exception) {
      return s;
    } 
  }
  
  private static String decompress(String s) {
    try {
      byte[] data = Base64.getDecoder().decode(s);
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      GZIPInputStream gis = new GZIPInputStream(bais);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int len;
      while ((len = gis.read(buffer)) > 0)
        baos.write(buffer, 0, len); 
      gis.close();
      return new String(baos.toByteArray());
    } catch (Exception exception) {
      return s;
    } 
  }
}
