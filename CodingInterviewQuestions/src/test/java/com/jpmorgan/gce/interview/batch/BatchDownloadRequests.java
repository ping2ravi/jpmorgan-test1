package com.jpmorgan.gce.interview.batch;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchDownloadRequests {

    /*
        Instructions:

        - You will be implementing some classes and methods, some of the classes have been started for you, but
        some are still missing significant parts and you will need to complete these
        - You may also extend some classes where you are not prompt if you feel this improves the design, however expect
        to explain your justification for doing so (i.e. why it makes the design better)
        - The aim of the exercise is to create batches of Download Requests. Each DownloadRequest has an Account ID and
        a GroupID. Below is a test that needs to be executed using your implementation.
        - Your implementation needs to batch up the DownloadRequests into multiple batches that have a maximum size,
        say 5, however DownloadRequests that have the same GroupID must go into the same batch.
        - For your first attempt don't worry if you slightly breach the maximum batch size, if you have time you can
        improve your algorithm to get batches that are more close to the maximum batch size.

     */

    ExecutorService executor = Executors.newSingleThreadExecutor();

    @Test
    public void testBatchingByGroupID() {
          DownloadRequest[] requests = new DownloadRequest[]{
                  new DownloadRequest(new AccountID("SCH001"), new GroupID("GRP001", 1)),
                  new DownloadRequest(new AccountID("SCH002"), null),
                  new DownloadRequest(new AccountID("SCH003"), null),
                  new DownloadRequest(new AccountID("SCH004"), null),
                  new DownloadRequest(new AccountID("SCH005"), null),
                  new DownloadRequest(new AccountID("SCH006"), new GroupID("GRP001", 2)),
                  new DownloadRequest(new AccountID("SCH007"), null),
                  new DownloadRequest(new AccountID("SCH008"), null),
                  new DownloadRequest(new AccountID("SCH009"), null),
                  new DownloadRequest(new AccountID("SCH010"), new GroupID("GRP001", 1)),
                  new DownloadRequest(new AccountID("SCH011"), null),
                  new DownloadRequest(new AccountID("SCH012"), null),
                  new DownloadRequest(new AccountID("SCH013"), null),
                  new DownloadRequest(new AccountID("SCH014"), null),
                  new DownloadRequest(new AccountID("SCH015"), new GroupID("GRP001", 2)),
                  new DownloadRequest(new AccountID("SCH016"), null),
                  new DownloadRequest(new AccountID("SCH017"), new GroupID("GRP001", 1)),
                  new DownloadRequest(new AccountID("SCH018"), new GroupID("GRP001", 1)),
                  new DownloadRequest(new AccountID("SCH019"), null),
                  new DownloadRequest(new AccountID("SCH020"), new GroupID("GRP003", 1)),
                  new DownloadRequest(new AccountID("SCH021"), new GroupID("GRP001", 1)),
                  new DownloadRequest(new AccountID("SCH022"), new GroupID("GRP001", 1)),
                  new DownloadRequest(new AccountID("SCH023"), null),
                  new DownloadRequest(new AccountID("SCH024"), null),
                  new DownloadRequest(new AccountID("SCH025"), null),
                  new DownloadRequest(new AccountID("SCH026"), null),
                  new DownloadRequest(new AccountID("SCH027"), null),
                  new DownloadRequest(new AccountID("SCH028"), null),
                  new DownloadRequest(new AccountID("SCH029"), new GroupID("GRP003", 1)),
                  new DownloadRequest(new AccountID("SCH030"), new GroupID("GRP003", 1)),
                  new DownloadRequest(new AccountID("SCH031"), null),
                  new DownloadRequest(new AccountID("SCH032"), null),
                  new DownloadRequest(new AccountID("SCH033"), null),
          };

        BatchingStrategy strategy = new BatchSameGroupStrategy();
        Downloader downloader = new PrintingDownloader();

        int optimumSize = 5;
        Collection<Batch<DownloadRequest>> batches = strategy.createBatches(Arrays.asList(requests), optimumSize);
        System.out.println(batches);
        final CountDownLatch allDownloadsComplete = new CountDownLatch(batches.size());

        Runnable decrementCounter = new Runnable() {
            @Override
            public void run() {
                allDownloadsComplete.countDown();
            }
        };

        for (Batch<DownloadRequest> batch : batches) {
            executor.submit(new DownloaderWorker(downloader, batch, decrementCounter));
        }

        try {
            allDownloadsComplete.await();
        } catch (InterruptedException e) {
            /* allow to exit as this is a test */
        }
    }
}
