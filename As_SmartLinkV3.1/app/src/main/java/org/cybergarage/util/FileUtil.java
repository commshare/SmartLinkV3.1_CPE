/******************************************************************
*
*	CyberUtil for Java
*
*	Copyright (C) Satoshi Konno 2002-2003
*
*	File: FileUtil.java
*
*	Revision:
*
*	01/03/03
*		- first revision.
*
******************************************************************/

package org.cybergarage.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public final class FileUtil
{
	public final static byte[] load(String fileName)
	{
		try {	
			FileInputStream fin=new FileInputStream(fileName);
			return load(fin);
		}
		catch (Exception e) {
			Debug.warning(e);
			return new byte[0];
		}
	}

	public final static byte[] load(File file)
	{
		try {	
			FileInputStream fin=new FileInputStream(file);
			return load(fin);
		}
		catch (Exception e) {
			Debug.warning(e);
			return new byte[0];
		}
	}

	public final static byte[] load(FileInputStream fin)
	{
		byte readBuf[] = new byte[512*1024];
	
		try {	
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
			int readCnt = fin.read(readBuf);
			while (0 < readCnt) {
				bout.write(readBuf, 0, readCnt);
				readCnt = fin.read(readBuf);
			}
			
			fin.close();
			
			return bout.toByteArray();
		}
		catch (Exception e) {
			Debug.warning(e);
			return new byte[0];
		}
	}
	
	public final static boolean isXMLFileName(String name)
	{
		if (StringUtil.hasData(name) == false)
			return false;
		String lowerName = name.toLowerCase();
		return lowerName.endsWith("xml");
	}

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    // 删除SD卡上的单个文件方法
    public static boolean deleteFile(String fileName) {

        File file = new File(fileName);
        if (file == null || !file.exists() || file.isDirectory()){
            return false;
        }
        file.delete();

        return true;
    }
}

