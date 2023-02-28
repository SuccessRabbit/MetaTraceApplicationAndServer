package com.sam.metatrace.Interfaces;

public interface OnDownloadFileResultListener {
    void onDownloadSuccess(String localFilePath);
    void onDownloadFailed();
    void onDownloading(int progress);
}
