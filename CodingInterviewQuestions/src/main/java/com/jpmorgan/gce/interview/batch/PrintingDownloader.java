package com.jpmorgan.gce.interview.batch;

public class PrintingDownloader implements Downloader {

    public void download(DownloadRequest request) {
    	if(request.getGroupID() == null){
    		System.out.println("Downloading request for account: "
                    + request.getAccountID().getAccountCode() + ", group: null" );
    	}else{
    		System.out.println("Downloading request for account: "
                    + request.getAccountID().getAccountCode() + ", group: " + request.getGroupID().toString());	
    	}
        
    }

}
