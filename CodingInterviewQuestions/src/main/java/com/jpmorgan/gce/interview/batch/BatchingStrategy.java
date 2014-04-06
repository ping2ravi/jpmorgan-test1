package com.jpmorgan.gce.interview.batch;

import java.util.Collection;

public interface BatchingStrategy {

    Collection<Batch<DownloadRequest>> createBatches(Collection<DownloadRequest> requests, int optimumSize);
}
