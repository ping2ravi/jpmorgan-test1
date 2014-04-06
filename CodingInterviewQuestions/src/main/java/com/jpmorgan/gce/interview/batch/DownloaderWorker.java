package com.jpmorgan.gce.interview.batch;

public class DownloaderWorker implements Runnable {

    Downloader downloader;
    Batch<DownloadRequest> requests;
    Runnable callBack;

    public DownloaderWorker(Downloader downloader, Batch<DownloadRequest> requests, Runnable callBack) {
        this.requests = requests;
        this.downloader = downloader;
        this.callBack = callBack;
    }

    @Override
    public void run() {
        System.out.println("----------------- Downloading Batch ----------------------");
        for (DownloadRequest request : requests) {
            downloader.download(request);
        }
        callBack.run();
    }
}
