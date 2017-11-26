package com.alcatel.smartlinkv3.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by qianli.ma on 2017/11/22 0022.
 */

public class VersionUtils {

    private static String[] oldVersion = {"y853", "y854", "y858", "y859", "y900", "y901", "ee60"};
    private static String[] newVersion = {"mw40", "mw41tm", "mw41gl", "mw40v1", "mw41vj", "ee40"};

    /**
     * 是否为旧版本
     *
     * @param currentVersion
     * @return
     */
    public static boolean isOldVersion(String currentVersion) {
        boolean isOld = false;
        List<String> oldVersions = Arrays.asList(VersionUtils.oldVersion);
        List<String> newVersions = Arrays.asList(VersionUtils.newVersion);
        currentVersion = currentVersion.toLowerCase();
        // 判断旧版本
        for (String olds : oldVersions) {
            if (currentVersion.contains(olds)) {
                isOld = true;
                break;
            }
        }
        // 判断新版本
        for (String news : newVersions) {
            if (currentVersion.contains(news)) {
                isOld = false;
            } else {
                isOld = true;
                break;
            }
        }
        return isOld;
    }
}
