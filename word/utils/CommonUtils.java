package com.xinzhihui.multiscreen.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.ArrayList;

import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;


/**
 * Created by Administrator on 2016/12/22.
 */

public class CommonUtils {
	
    /**
     * 反射获取内、外存储设备路径
     *
     * @param mContext
     * @param is_removale
     * @return
     */
    public static String[] getStoragePaths(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Method getState = storageVolumeClazz.getMethod("getState");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);

            ArrayList<String> pathList = new ArrayList<String>();

            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);

                String state = (String) getState.invoke(storageVolumeElement);
                if ("mounted".equals(state)) {
                    pathList.add(path);
                }
//                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
//                if (is_removale == removable) {
//                    return path;
//                }
            }
            if (pathList != null && pathList.size() > 0) {
                String paths[] = new String[pathList.size()];
                pathList.toArray(paths);
                return paths;
            }
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    

    public static String getSignature(String appId, String timestamp, String appSecret) {
        String str = new String();
        String requestStr = appId + timestamp + appSecret;
        str = getSha1(requestStr);
        return str;
    }


    //SHA1加密
    public static String getSha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }


    public static String getSystemInfoLine() {
        BufferedReader reader = null;
        String line;
        String info = null;
        try {
            reader = new BufferedReader(new FileReader("/sys/class/sunxi_info/sys_info"));
            reader.readLine();
            reader.readLine();
            line = reader.readLine();
            if (line != null) {
                line = line.trim();
                String[] tokens = line.split(": ");
                info = tokens[1];
                Log.d("qiansheng", "info=" + info);
            }
        } catch (FileNotFoundException e) {
            Log.e("qiansheng", "not found /sys/class/sunxi_info/sys_info");
        } catch (IOException e) {
            Log.e("qiansheng", "/sys/class/sunxi_info/sys_info");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e("qiansheng", "could not close /sys/class/sunxi_info/sys_info");
            }
        }
        return info;

    }


}
