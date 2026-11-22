/*
 * Copyright (C) 2016 AriaLyy(https://github.com/AriaLyy/Aria)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arialyy.aria.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.arialyy.aria.core.AriaConfig;
import com.arialyy.aria.core.FtpUrlEntity;
import dalvik.system.DexFile;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lyy on 2016/1/22. 閫氱敤宸ュ叿
 */
public class CommonUtil {
  private static final String TAG = "CommonUtil";
  public static final String SERVER_CHARSET = "ISO-8859-1";
  private static long lastClickTime;
  /**
   * android銆乤ndroidx銆乻upport鐨刦ragment銆乨ialogFragment绫诲悕
   */
  private static List<String> mFragmentClassName = new ArrayList<>();
  private static List<String> mDialogFragmentClassName = new ArrayList<>();

  static {
    mFragmentClassName.add("androidx.fragment.app.Fragment");
    mFragmentClassName.add("androidx.fragment.app.DialogFragment");
    mFragmentClassName.add("android.app.Fragment");
    mFragmentClassName.add("android.app.DialogFragment");
    mFragmentClassName.add("androidx.fragment.app.Fragment");
    mFragmentClassName.add("android.support.v4.app.DialogFragment");

    mDialogFragmentClassName.add("androidx.fragment.app.DialogFragment");
    mDialogFragmentClassName.add("android.app.DialogFragment");
    mDialogFragmentClassName.add("android.support.v4.app.DialogFragment");
  }

  /**
   * 鑾峰彇fragment鐨刟ctivityz
   *
   * @return 鑾峰彇澶辫触锛岃繑鍥瀗ull
   */
  public static Activity getFragmentActivity(Object obj) {
    try {
      Method method = obj.getClass().getMethod("getActivity");
      return (Activity) method.invoke(obj);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 鍒ゆ柇娉ㄨВ瀵硅薄鏄惁鏄痜ragment
   *
   * @return true 瀵硅薄鏄痜ragment
   */
  public static boolean isFragment(Class subClazz) {
    Class parentClass = subClazz.getSuperclass();
    if (parentClass == null) {
      return false;
    } else {
      String parentName = parentClass.getName();
      if (mFragmentClassName.contains(parentName)) {
        return true;
      } else {
        return isFragment(parentClass);
      }
    }
  }

  /**
   * 鍒ゆ柇瀵硅薄鏄惁鏄疍ialogFragment
   *
   * @return true 瀵硅薄鏄疍ialogFragment
   */
  public static boolean isDialogFragment(Class subClazz) {
    Class parentClass = subClazz.getSuperclass();
    if (parentClass == null) {
      return false;
    } else {
      String parentName = parentClass.getName();
      if (mDialogFragmentClassName.contains(parentName)) {
        return true;
      } else {
        return isDialogFragment(parentClass);
      }
    }
  }

  public static boolean isLocalOrAnonymousClass(Class clazz) {
    // JVM Spec 4.8.6: A class must have an EnclosingMethod
    // attribute if and only if it is a local class or an
    // anonymous class.
    return clazz.isLocalClass() || clazz.isAnonymousClass();
  }

  public static String getTargetName(Object obj) {
    String targetName;
    if (isLocalOrAnonymousClass(obj.getClass())) {
      Log.w(TAG, String.format("%s 鏄尶鍚嶅唴閮ㄧ被鎴栧眬閮ㄧ被锛屽皢浣跨敤鍏朵富绫荤殑瀵硅薄", obj.getClass().getName()));
      String clsName = obj.getClass().getName();
      int $Index = clsName.lastIndexOf("$");
      targetName = clsName.substring(0, $Index);
    } else {
      targetName = obj.getClass().getName();
    }
    return targetName;
  }

  /**
   * 鑾峰彇绾跨▼鍚嶇О锛屽懡鍚嶈鍒欙細md5(浠诲姟鍦板潃 + 绾跨▼id)
   *
   * @param url 浠诲姟鍦板潃
   * @param threadId 绾跨▼id
   */
  public static String getThreadName(String url, int threadId) {
    return getStrMd5(url.concat(String.valueOf(threadId)));
  }

  /**
   * 妫€鏌ql鐨別xpression鏄惁鍚堟硶
   *
   * @return false 涓嶅悎娉?   */
  public static boolean checkSqlExpression(String... expression) {
    if (expression.length == 0) {
      ALog.e(TAG, "sql璇彞琛ㄨ揪寮忎笉鑳戒负null");
      return false;
    }
    //if (expression.length == 1) {
    //  ALog.e(TAG, String.format("琛ㄨ揪寮忛渶瑕佸啓鍏ュ弬鏁帮紝鍙傛暟淇℃伅锛?s", Arrays.toString(expression)));
    //  return false;
    //}
    String where = expression[0];
    //if (!where.contains("?")) {
    //  ALog.e(TAG, String.format("璇峰湪where璇彞鐨?='鍚庣紪鍐?锛屽弬鏁颁俊鎭細%s", Arrays.toString(expression)));
    //  return false;
    //}
    Pattern pattern = Pattern.compile("\\?");
    Matcher matcher = pattern.matcher(where);
    int count = 0;
    while (matcher.find()) {
      count++;
    }
    if (count < expression.length - 1) {
      ALog.e(TAG, String.format("鏉′欢璇彞鐨?涓暟涓嶈兘灏忎簬鍙傛暟涓暟锛屽弬鏁颁俊鎭細%s", Arrays.toString(expression)));
      return false;
    }
    if (count > expression.length - 1) {
      ALog.e(TAG, String.format("鏉′欢璇彞鐨?涓暟涓嶈兘澶т簬鍙傛暟涓暟锛?鍙傛暟淇℃伅锛?s", Arrays.toString(expression)));
      return false;
    }
    return true;
  }

  /**
   * 鏄惁鏄揩閫熺偣鍑伙紝500ms鍐呭揩閫熺偣鍑绘棤鏁?   *
   * @return true 蹇€熺偣鍑?   */
  public static boolean isFastDoubleClick() {
    long time = System.currentTimeMillis();
    long timeD = time - lastClickTime;
    if (0 < timeD && timeD < 500) {
      ALog.i(TAG, "操作太频繁，请稍后再试");
      return true;
    }
    lastClickTime = time;
    return false;
  }

  /**
   * 灏嗗瓧绗︿覆杞崲涓篎tp鏈嶅姟鍣ㄩ粯璁ょ殑ISO-8859-1缂栫爜
   *
   * @param charSet 鍘熷瓧绗︿覆缂栫爜s
   * @param str 闇€瑕佽浆鎹㈢殑瀛楃涓?   * @return 杞崲鍚庣殑瀛楃涓?   */
  public static String convertFtpChar(String charSet, String str)
      throws UnsupportedEncodingException {
    return new String(str.getBytes(charSet), SERVER_CHARSET);
  }

  /**
   * 灏嗗瓧绗︿覆杞崲涓篎tp鏈嶅姟鍣ㄩ粯璁ょ殑ISO-8859-1缂栫爜
   *
   * @param charSet 瀛楃涓茬紪鐮?   * @param str 闇€瑕佽浆鎹㈢殑瀛楃涓?   * @return 杞崲鍚庣殑瀛楃涓?   */
  public static String convertSFtpChar(String charSet, String str)
      throws UnsupportedEncodingException {
    return new String(str.getBytes(), charSet);
  }

  /**
   * 鑾峰彇鏌愬寘涓嬫墍鏈夌被
   *
   * @param className 杩囨护鐨勭被鍚?   * @return 绫荤殑瀹屾暣鍚嶇О
   */
  public static List<String> getPkgClassNames(Context context, String className) {
    List<String> classNameList = new ArrayList<>();
    String pPath = context.getPackageCodePath();
    File dir = new File(pPath).getParentFile();
    String[] paths = dir.list();
    if (paths == null) {
      classNameList.addAll(getPkgClassName(pPath, className));
    } else {
      String dPath = dir.getPath();
      for (String path : dir.list()) {
        String fPath = dPath + "/" + path;
        if (!fPath.endsWith(".apk")) {
          continue;
        }
        classNameList.addAll(getPkgClassName(fPath, className));
      }
    }
    return classNameList;
  }

  /**
   * 鑾峰彇鎸囧畾鍖呭悕涓嬬殑鎵€鏈夌被
   *
   * @param path dex璺緞
   * @param filterClass 闇€瑕佽繃婊ょ殑绫?   */
  public static List<String> getPkgClassName(String path, String filterClass) {
    List<String> list = new ArrayList<>();
    try {
      File file = new File(path);
      if (!file.exists()) {
        ALog.w(TAG, String.format("路径【%s】下的Dex文件不存在", path));
        return list;
      }

      DexFile df = new DexFile(path);//閫氳繃DexFile鏌ユ壘褰撳墠鐨凙PK涓彲鎵ц鏂囦欢
      Enumeration<String> enumeration = df.entries();
      while (enumeration.hasMoreElements()) {
        String _className = enumeration.nextElement();
        if (!_className.contains(filterClass)) {
          continue;
        }
        if (_className.contains(filterClass)) {
          list.add(_className);
        }
      }
      df.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return list;
  }

  /**
   * 鎷︽埅window.location.replace鏁版嵁
   *
   * @return 閲嶅畾鍚憉rl
   */
  public static String getWindowReplaceUrl(String text) {
    if (TextUtils.isEmpty(text)) {
      ALog.e(TAG, "鎷︽埅鏁版嵁涓簄ull");
      return null;
    }
    String reg = Regular.REG_WINLOD_REPLACE;
    Pattern p = Pattern.compile(reg);
    Matcher m = p.matcher(text);
    if (m.find()) {
      String s = m.group();
      s = s.substring(9, s.length() - 2);
      return s;
    }
    return null;
  }

  /**
   * 鑾峰彇sdcard app鐨勭紦瀛樼洰褰?   *
   * @return "/mnt/sdcard/Android/data/{package_name}/files/"
   */
  public static String getAppPath(Context context) {
    boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(
        android.os.Environment.getExternalStorageState());
    if (!sdExist) {
      return null;
    } else {
      File file = context.getExternalFilesDir(null);
      String dir;
      if (file != null) {
        dir = file.getPath() + "/";
      } else {
        dir = Environment.getExternalStorageDirectory().getPath()
            + "/Android/data/"
            + context.getPackageName()
            + "/files/";
      }
      return dir;
    }
  }

  /**
   * 鑾峰彇map娉涘瀷绫诲瀷
   *
   * @param map list绫诲瀷瀛楁
   * @return 娉涘瀷绫诲瀷
   */
  public static Class[] getMapParamType(Field map) {
    Class type = map.getType();
    if (!type.isAssignableFrom(Map.class)) {
      ALog.d(TAG, "瀛楁绫诲瀷涓嶆槸Map");
      return null;
    }

    Type fc = map.getGenericType();

    if (fc == null) {
      ALog.d(TAG, "该字段没有泛型参数");
      return null;
    }

    if (fc instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) fc;
      Type[] types = pt.getActualTypeArguments();
      Class[] clazz = new Class[2];
      clazz[0] = (Class) types[0];
      clazz[1] = (Class) types[1];
      return clazz;
    }
    return null;
  }

  /**
   * 鑾峰彇list娉涘瀷绫诲瀷
   *
   * @param list list绫诲瀷瀛楁
   * @return 娉涘瀷绫诲瀷
   */

  public static Class getListParamType(Field list) {
    Class type = list.getType();
    if (!type.isAssignableFrom(List.class)) {
      ALog.d(TAG, "瀛楁绫诲瀷涓嶆槸List");
      return null;
    }

    Type fc = list.getGenericType(); // 鍏抽敭鐨勫湴鏂癸紝濡傛灉鏄疞ist绫诲瀷锛屽緱鍒板叾Generic鐨勭被鍨?
    if (fc == null) {
      ALog.d(TAG, "该字段没有泛型参数");
      return null;
    }

    if (fc instanceof ParameterizedType) { //濡傛灉鏄硾鍨嬪弬鏁扮殑绫诲瀷
      ParameterizedType pt = (ParameterizedType) fc;
      return (Class) pt.getActualTypeArguments()[0];
    }
    return null;
  }

  /**
   * 鍒嗗壊鑾峰彇url锛屽崗璁紝ip/鍩熷悕锛岀鍙ｏ紝鍐呭
   *
   * @param url 杈撳叆鐨剈rl{@code String url = "ftp://z:z@dygod18.com:21211/[鐢靛奖澶╁爞www.dy2018.com]鐚╃悆宕涜捣3锛氱粓鏋佷箣鎴楤D鍥借嫳鍙岃涓嫳鍙屽瓧.mkv";}
   */
  public static FtpUrlEntity getFtpUrlInfo(String url) {
    Uri uri = Uri.parse(url);

    String userInfo = uri.getUserInfo(), remotePath = uri.getPath();
    ALog.d(TAG,
        String.format("scheme = %s, user = %s, host = %s, port = %s, path = %s", uri.getScheme(),
            userInfo, uri.getHost(), uri.getPort(), remotePath));

    FtpUrlEntity entity = new FtpUrlEntity();
    entity.url = url;
    entity.hostName = uri.getHost();
    entity.port = uri.getPort() == -1 ? "21" : String.valueOf(uri.getPort());
    if (!TextUtils.isEmpty(userInfo)) {
      String[] temp = userInfo.split(":");
      if (temp.length == 2) {
        entity.user = temp[0];
        entity.password = temp[1];
      } else {
        entity.user = userInfo;
      }
    }
    entity.scheme = uri.getScheme();
    entity.remotePath = TextUtils.isEmpty(remotePath) ? "/" : remotePath;
    return entity;
  }

  /**
   * 杞崲Url
   *
   * @param url 鍘熷湴鍧€
   * @return 杞崲鍚庣殑鍦板潃
   */
  public static String convertUrl(String url) {
    Uri uri = Uri.parse(url);
    url = uri.toString();
    if (hasDoubleCharacter(url)) {
      //棰勫厛澶勭悊绌烘牸锛孶RLEncoder鍙細鎶婄┖鏍艰浆鎹负+
      url = url.replaceAll(" ", "%20");
      //鍖归厤鍙屽瓧鑺傚瓧绗?鍖呮嫭姹夊瓧鍦ㄥ唴)
      String regex = Regular.REG_DOUBLE_CHAR_AND_SPACE;
      Pattern p = Pattern.compile(regex);
      Matcher m = p.matcher(url);
      Set<String> strs = new HashSet<>();
      while (m.find()) {
        strs.add(m.group());
      }
      try {
        for (String str : strs) {
          url = url.replaceAll(str, URLEncoder.encode(str, "UTF-8"));
        }
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
    return url;
  }

  /**
   * 鍒ゆ柇鏄惁鏈夊弻瀛楄妭瀛楃(鍖呮嫭姹夊瓧鍦ㄥ唴) 鍜岀┖鏍笺€佸埗琛ㄧ銆佸洖杞?   *
   * @param chineseStr 闇€瑕佽繘琛屽垽鏂殑瀛楃涓?   * @return {@code true}鏈夊弻瀛楄妭瀛楃锛寋@code false} 鏃犲弻瀛楄妭瀛楃
   */
  public static boolean hasDoubleCharacter(String chineseStr) {
    char[] charArray = chineseStr.toCharArray();
    for (char aCharArray : charArray) {
      if (((aCharArray >= 0x0391) && (aCharArray <= 0xFFE5)) || (aCharArray == 0x0d) || (aCharArray
          == 0x0a) || (aCharArray == 0x20)) {
        return true;
      }
    }
    return false;
  }

  /**
   * base64 瑙ｅ瘑瀛楃涓?   *
   * @param str 琚姞瀵嗙殑瀛楃涓?   * @return 瑙ｅ瘑鍚庣殑瀛楃涓?   */
  public static String decryptBASE64(String str) {
    return new String(Base64.decode(str.getBytes(), Base64.DEFAULT));
  }

  /**
   * base64 鍔犲瘑瀛楃涓?   *
   * @param str 闇€瑕佸姞瀵嗙殑瀛楃涓?   * @return 鍔犲瘑鍚庣殑瀛楃涓?   */
  public static String encryptBASE64(String str) {
    return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
  }

  /**
   * 瀛楃涓茬紪鐮佽浆鎹?   */
  public static String strCharSetConvert(String oldStr, String charSet) {
    try {
      return new String(oldStr.getBytes(), charSet);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 鏍规嵁涓嬭浇浠诲姟缁勭殑url鍒涘缓key
   *
   * @return urls 涓?null 鎴栬€?size涓?锛岃繑鍥?"
   */
  public static String getMd5Code(List<String> urls) {
    if (urls == null || urls.size() < 1) return "";
    String md5 = "";
    StringBuilder sb = new StringBuilder();
    for (String url : urls) {
      sb.append(url);
    }
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(sb.toString().getBytes());
      md5 = new BigInteger(1, md.digest()).toString(16);
    } catch (NoSuchAlgorithmException e) {
      ALog.e(TAG, e.getMessage());
    }
    return md5;
  }

  /**
   * 鑾峰彇瀛楃涓茬殑md5
   *
   * @return 瀛楃涓蹭负绌烘垨鑾峰彇md5澶辫触锛屽垯杩斿洖""
   */
  public static String getStrMd5(String str) {
    if (TextUtils.isEmpty(str)) return "";
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(str.getBytes());
      return new BigInteger(1, md.digest()).toString(16);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * 鑾峰彇CPU鏍稿績鏁?   */
  public static int getCoresNum() {
    //Private Class to display only CPU devices in the directory listing
    class CpuFilter implements FileFilter {
      @Override public boolean accept(File pathname) {
        //Check if filename is "cpu", followed by a single digit number
        return Pattern.matches("cpu[0-9]", pathname.getName());
      }
    }

    try {
      //Get directory containing CPU info
      File dir = new File("/sys/devices/system/cpu/");
      //Filter to only list the devices we care about
      File[] files = dir.listFiles(new CpuFilter());
      ALog.d(TAG, "CPU Count: " + files.length);
      //Return the number of cores (virtual CPU devices)
      return files.length;
    } catch (Exception e) {
      //Print exception
      ALog.d(TAG, "CPU Count: Failed.");
      e.printStackTrace();
      //Default to return 1 core
      return 1;
    }
  }

  /**
   * 鏍￠獙鏂囦欢MD5鐮?   */
  public static boolean checkMD5(String md5, File updateFile) {
    if (TextUtils.isEmpty(md5) || updateFile == null) {
      ALog.e(TAG, "MD5 string empty or updateFile null");
      return false;
    }

    String calculatedDigest = getFileMD5(updateFile);
    if (calculatedDigest == null) {
      ALog.e(TAG, "calculatedDigest null");
      return false;
    }
    return calculatedDigest.equalsIgnoreCase(md5);
  }

  /**
   * 鏍￠獙鏂囦欢MD5鐮?   */
  public static boolean checkMD5(String md5, InputStream is) {
    if (TextUtils.isEmpty(md5) || is == null) {
      ALog.e(TAG, "MD5 string empty or updateFile null");
      return false;
    }

    String calculatedDigest = getFileMD5(is);
    if (calculatedDigest == null) {
      ALog.e(TAG, "calculatedDigest null");
      return false;
    }
    return calculatedDigest.equalsIgnoreCase(md5);
  }

  /**
   * 鑾峰彇鏂囦欢MD5鐮?   */
  public static String getFileMD5(File updateFile) {
    InputStream is;
    try {
      is = new FileInputStream(updateFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }

    return getFileMD5(is);
  }

  /**
   * 鑾峰彇鏂囦欢MD5鐮?   */
  public static String getFileMD5(InputStream is) {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }

    byte[] buffer = new byte[8192];
    int read;
    try {
      while ((read = is.read(buffer)) > 0) {
        digest.update(buffer, 0, read);
      }
      byte[] md5sum = digest.digest();
      BigInteger bigInt = new BigInteger(1, md5sum);
      String output = bigInt.toString(16);
      // Fill to 32 chars
      output = String.format("%32s", output).replace(' ', '0');
      return output;
    } catch (IOException e) {
      throw new RuntimeException("Unable to process file for MD5", e);
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 鍒涘缓闅愭€х殑Intent
   */
  public static Intent createIntent(String packageName, String action) {
    Uri.Builder builder = new Uri.Builder();
    builder.scheme(packageName);
    Uri uri = builder.build();
    Intent intent = new Intent(action);
    intent.setData(uri);
    return intent;
  }

  /**
   * 瀛樺偍瀛楃涓插埌閰嶇疆鏂囦欢
   *
   * @param preName 閰嶇疆鏂囦欢鍚?   * @param key 瀛樺偍鐨勯敭鍊?   * @param value 闇€瑕佸瓨鍌ㄧ殑瀛楃涓?   * @return 鎴愬姛鏍囧織
   */
  public static Boolean putString(String preName, Context context, String key, String value) {
    SharedPreferences pre = context.getSharedPreferences(preName, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = pre.edit();
    editor.putString(key, value);
    return editor.commit();
  }

  /**
   * 浠庨厤缃枃浠惰鍙栧瓧绗︿覆
   *
   * @param preName 閰嶇疆鏂囦欢鍚?   * @param key 瀛楃涓查敭鍊?   * @return 閿€煎搴旂殑瀛楃涓? 榛樿杩斿洖""
   */
  public static String getString(String preName, Context context, String key) {
    SharedPreferences pre = context.getSharedPreferences(preName, Context.MODE_PRIVATE);
    return pre.getString(key, "");
  }

  /**
   * 鑾峰彇鎵€鏈夊瓧娈碉紝鍖呮嫭鐖剁被鐨勫瓧娈?   */
  public static List<Field> getAllFields(Class clazz) {
    List<Field> fields = new ArrayList<>();
    Class personClazz = clazz.getSuperclass();
    if (personClazz != null) {
      Class rootClazz = personClazz.getSuperclass();
      if (rootClazz != null) {
        Collections.addAll(fields, rootClazz.getDeclaredFields());
      }
      Collections.addAll(fields, personClazz.getDeclaredFields());
    }
    Collections.addAll(fields, clazz.getDeclaredFields());
    List<Field> ignore = new ArrayList<>();
    for (Field field : fields) {
      if (field.getName().equals(AriaConfig.IGNORE_CLASS_KLASS) || field.getName()
          .equals(AriaConfig.IGNORE_CLASS_MONITOR)) {
        ignore.add(field);
      }
    }
    if (!ignore.isEmpty()) {
      fields.removeAll(ignore);
    }
    return fields;
  }

  /**
   * 鑾峰彇褰撳墠绫婚噷闈㈢殑鎵€鍦ㄥ瓧娈?   */
  public static Field[] getFields(Class clazz) {
    Field[] fields;
    fields = clazz.getDeclaredFields();
    if (fields.length == 0) {
      Class superClazz = clazz.getSuperclass();
      if (superClazz != null) {
        fields = getFields(superClazz);
      }
    }
    return fields;
  }

  /**
   * 鑾峰彇绫婚噷闈㈢殑鎸囧畾瀵硅薄锛屽鏋滆绫绘病鏈夊垯浠庣埗绫绘煡璇?   */
  public static Field getField(Class clazz, String name) {
    Field field = null;
    try {
      field = clazz.getDeclaredField(name);
    } catch (NoSuchFieldException e) {
      try {
        field = clazz.getField(name);
      } catch (NoSuchFieldException e1) {
        if (clazz.getSuperclass() == null) {
          return null;
        } else {
          field = getField(clazz.getSuperclass(), name);
        }
      }
    }
    if (field != null) {
      field.setAccessible(true);
    }
    return field;
  }

  /**
   * 鑾峰彇绫婚噷闈㈢殑鎸囧畾瀵硅薄锛屽鏋滆绫绘病鏈夊垯浠庣埗绫绘煡璇?   */
  public static Method getMethod(Class clazz, String methodName, Class<?>... params){
    Method method = null;
    try {
      method = clazz.getDeclaredMethod(methodName, params);
    } catch (NoSuchMethodException e) {
      try {
        method = clazz.getMethod(methodName, params);
      } catch (NoSuchMethodException ex) {
        if (clazz.getSuperclass() == null) {
          return null;
        } else {
          method = getMethod(clazz.getSuperclass(), methodName, params);
        }
      }
    }

    if (method != null){
      method.setAccessible(true);
    }

    return method;
  }

  /**
   * 瀛楃涓茶浆hashcode
   */
  public static int keyToHashCode(String str) {
    int total = 0;
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (ch == '-') ch = (char) 28; // does not contain the same last 5 bits as any letter
      if (ch == '\'') ch = (char) 29; // nor this
      total = (total * 33) + (ch & 0x1F);
    }
    return total;
  }

  /**
   * 灏唊ey杞崲涓?6杩涘埗鐮?   *
   * @param key 缂撳瓨鐨刱ey
   * @return 杞崲鍚庣殑key鐨勫€? 绯荤粺渚挎槸閫氳繃璇ey鏉ヨ鍐欑紦瀛?   */
  public static String keyToHashKey(String key) {
    String cacheKey;
    try {
      final MessageDigest mDigest = MessageDigest.getInstance("MD5");
      mDigest.update(key.getBytes());
      cacheKey = bytesToHexString(mDigest.digest());
    } catch (NoSuchAlgorithmException e) {
      cacheKey = String.valueOf(key.hashCode());
    }
    return cacheKey;
  }

  /**
   * 灏嗘櫘閫氬瓧绗︿覆杞崲涓?6浣嶈繘鍒跺瓧绗︿覆
   */
  public static String bytesToHexString(byte[] src) {
    StringBuilder stringBuilder = new StringBuilder("0x");
    if (src == null || src.length <= 0) {
      return null;
    }
    char[] buffer = new char[2];
    for (byte aSrc : src) {
      buffer[0] = Character.forDigit((aSrc >>> 4) & 0x0F, 16);
      buffer[1] = Character.forDigit(aSrc & 0x0F, 16);
      stringBuilder.append(buffer);
    }
    return stringBuilder.toString();
  }

  /**
   * 鑾峰彇瀵硅薄鍚?   *
   * @param obj 瀵硅薄
   * @return 瀵硅薄鍚?   */
  public static String getClassName(Object obj) {
    String[] arrays = obj.getClass().getName().split("\\.");
    return arrays[arrays.length - 1];
  }

  /**
   * 鑾峰彇瀵硅薄鍚?   *
   * @param clazz clazz
   * @return 瀵硅薄鍚?   */
  public static String getClassName(Class clazz) {
    String[] arrays = clazz.getName().split("\\.");
    return arrays[arrays.length - 1];
  }

  /**
   * 鏍煎紡鍖栨枃浠跺ぇ灏?   *
   * @param size file.length() 鑾峰彇鏂囦欢澶у皬
   */
  public static String formatFileSize(double size) {
    if (size < 0) {
      return "0kb";
    }
    double kiloByte = size / 1024;
    if (kiloByte < 1) {
      return size + "b";
    }

    double megaByte = kiloByte / 1024;
    if (megaByte < 1) {
      BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
      return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "kb";
    }

    double gigaByte = megaByte / 1024;
    if (gigaByte < 1) {
      BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
      return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "mb";
    }

    double teraBytes = gigaByte / 1024;
    if (teraBytes < 1) {
      BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
      return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "gb";
    }
    BigDecimal result4 = new BigDecimal(teraBytes);
    return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "tb";
  }

  /**
   * 杞崲鏃堕棿
   * 鏃堕棿锛? 灏忔椂锛屾樉绀哄垎绉掞紝鏄剧ず鏍峰紡 00:20
   * 鏃堕棿 鈮? 灏忔椂锛屾樉绀烘椂鍒嗙锛屾樉绀烘牱寮?01:11:12
   * 鏃堕棿 鈮? 澶╋紝鏄剧ず澶╂椂鍒嗭紝鏄剧ず鏍峰紡 1d 01:11
   * 鏃堕棿 鈮? 澶╋紝鏄剧ず鏍峰紡 鈭?   *
   * @param seconds 鍗曚綅涓簊鐨勬椂闂?   */
  public static String formatTime(int seconds) {
    String standardTime;
    if (seconds <= 0) {
      standardTime = "00:00";
    } else if (seconds < 60) {
      standardTime = String.format(Locale.getDefault(), "00:%02d", seconds % 60);
    } else if (seconds < 3600) {
      standardTime = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
    } else if (seconds < 86400) {
      standardTime =
          String.format(Locale.getDefault(), "%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60,
              seconds % 60);
    } else if (seconds < 604800) {
      standardTime =
          String.format(Locale.getDefault(), "%dd %02d:%02d", seconds / 86400,
              seconds % 86400 / 3600,
              seconds % 3600);
    } else {
      standardTime = "∞";
    }

    return standardTime;
  }

  /**
   * 閫氳繃鏂囦欢鍚嶈幏鍙栦笅杞介厤缃枃浠惰矾寰?   *
   * @param fileName 鏂囦欢鍚?   */
  public static String getFileConfigPath(boolean isDownload, String fileName) {
    return AriaConfig.getInstance().getAPP().getFilesDir().getPath() + (isDownload
        ? AriaConfig.DOWNLOAD_TEMP_DIR
        : AriaConfig.UPLOAD_TEMP_DIR) + fileName + ".properties";
  }
}
