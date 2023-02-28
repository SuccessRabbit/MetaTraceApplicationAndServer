package com.sam.metatrace.Interfaces;

public interface OnUploadFileResultListener {
    /**
     * 上传成功回调函数
     * @param downloadFilePath 服务器上的nginx服务器对应的文件下载地址
     */
    void onUploadSuccess(String downloadFilePath);
    void onUploadFailed();
}
