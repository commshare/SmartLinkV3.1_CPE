package com.alcatel.smartlinkv3.rx.impl.download;

/**
 * Created by yaodong.zhang on 2017/6/22.
 */

public interface DownloadProgressListener {
    void update(long downedLength, long totalLength, boolean done);
}
