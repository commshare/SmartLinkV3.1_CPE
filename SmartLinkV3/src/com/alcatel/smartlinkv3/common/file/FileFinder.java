package com.alcatel.smartlinkv3.common.file;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.alcatel.smartlinkv3.ui.activity.StorageSearchActivity;

import android.os.Handler;

/**
 * ʵ��һ��֧��ͨ���Ļ��ڹ�������㷨���ļ�������
 */
public class FileFinder {
	private boolean m_bCancel = false;
	public void setCancel(boolean bCancel) {
		m_bCancel = bCancel;
	}

	/**
	 * �����ļ���
	 * @param baseDirName		����ҵ�Ŀ¼
	 * @param targetFileName	Ŀ���ļ���֧��ͨ�����ʽ
	 * @param nSeachType		0:all 1:files; 2:directory
	 * @return		�����ѯ�������ļ��б�
	 */
	public ArrayList<File> findFiles(String baseDirName, String targetFileName, int nSeachType,Handler handler) {
		/**
		 * �㷨������
		 * ��ĳ���������ҵ��ļ��г������������ļ��е��������ļ��м��ļ���
		 * ��Ϊ�ļ��������ƥ�䣬ƥ��ɹ����������Ϊ���ļ��У������С�
		 * ���в��գ��ظ���������������Ϊ�գ���������ؽ��
		 */
		int nCount = 0;
		ArrayList<File> fileList = new ArrayList<File>();
		//�ж�Ŀ¼�Ƿ����
		File baseDir = new File(baseDirName);
		if (!baseDir.exists() || !baseDir.isDirectory()){
			System.out.println("�ļ�����ʧ�ܣ�" + baseDirName + "����һ��Ŀ¼��");
			return fileList;
		}
		String tempName = null;
		//����һ�����У�Queue�ڵ������ж���
		Queue<File> queue = new LinkedList<File>();  //ʵ����� 
		queue.add(baseDir);//��� 
		File tempFile = null;
		while (!queue.isEmpty()) {
			if(m_bCancel)
				return fileList;
			//�Ӷ�����ȡĿ¼
			tempFile = (File) queue.poll();
			if (tempFile.exists() && tempFile.isDirectory()) {
				File[] files = tempFile.listFiles();
				for (int i = 0; i < files.length; i++) {
					if(m_bCancel)
						return fileList;
					if(files[i].isHidden())
						continue;
					//�����Ŀ¼��Ž����
					if (files[i].isDirectory()) { 
						queue.add(files[i]);
					}
					
					if(nSeachType == 1 && files[i].isFile() == false)
						continue;
					
					if(nSeachType == 2 && files[i].isDirectory() == false)
						continue;
						
					//����ļ�����Ŀ���ļ������ƥ�� 
					tempName =  files[i].getName(); 
					if (wildcardMatch(targetFileName, tempName)) {
						//ƥ��ɹ������ļ�����ӵ����
						fileList.add(files[i]); 
						nCount++;
						if(nCount >= 5) {
							handler.obtainMessage(StorageSearchActivity.MSG_LOGCL_STOREAGE_SEARCH_RESULT,fileList.clone()).sendToTarget();
							nCount = 0;
							try {
								Thread.currentThread().sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			} 
		}
		
		return fileList;
	}
	/**
	 * ͨ���ƥ��
	 * @param pattern	ͨ���ģʽ
	 * @param str	��ƥ����ַ�
	 * @return	ƥ��ɹ��򷵻�true�����򷵻�false
	 */
	private boolean wildcardMatch(String pattern, String str) {
		/*int patternLength = pattern.length();
		int strLength = str.length();
		int strIndex = 0;
		char ch;
		for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
			ch = pattern.charAt(patternIndex);
			if (ch == '*') {
				//ͨ����Ǻ�*��ʾ����ƥ���������ַ�
				while (strIndex < strLength) {
					if (wildcardMatch(pattern.substring(patternIndex + 1),
							str.substring(strIndex))) {
						return true;
					}
					strIndex++;
				}
			} else if (ch == '?') {
				//ͨ����ʺ�?��ʾƥ������һ���ַ�
				strIndex++;
				if (strIndex > strLength) {
					//��ʾstr���Ѿ�û���ַ�ƥ��?�ˡ�
					return false;
				}
			} else {
				if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
					return false;
				}
				strIndex++;
			}
		}
		return (strIndex == strLength);*/
		String strSearch = pattern.toLowerCase();
		String strName = str.toLowerCase();
		if(strName.indexOf(strSearch) >= 0)
			return true;
		return false;	
	}

	/*public static void main(String[] paramert) {
		//	�ڴ�Ŀ¼�����ļ�
		String baseDIR = "C:/temp"; 
		//	����չ��Ϊtxt���ļ�
		String fileName = "*.txt"; 
		//	��෵��5���ļ�
		int countNumber = 5; 
		List resultList = FileFinder.findFiles(baseDIR, fileName, countNumber); 
		if (resultList.size() == 0) {
			System.out.println("No File Fount.");
		} else {
			for (int i = 0; i < resultList.size(); i++) {
				System.out.println(resultList.get(i));//��ʾ���ҽ�� 
			}
		}
	}*/
}

