package com.jpmorgan.gce.interview.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchSameGroupStrategy implements BatchingStrategy {

	@Override
	public Collection<Batch<DownloadRequest>> createBatches(Collection<DownloadRequest> requests, int optimumSize) {
		if(requests == null){
			return Collections.EMPTY_LIST;
		}
		Map<GroupID, Batch<DownloadRequest>> tempNotNullGroupIdBatches = new HashMap<GroupID, Batch<DownloadRequest>>();
		Batch<DownloadRequest> nullGroupIdbatch = new Batch<DownloadRequest>();

		//Create default batches based on groupIds
		createDefaultBatchByGroupIds(requests, tempNotNullGroupIdBatches, nullGroupIdbatch);

		List<Batch<DownloadRequest>> notNullGroupBatches = new ArrayList<Batch<DownloadRequest>>(tempNotNullGroupIdBatches.values());

		//At this place if we just want to return non optimized batching, then we can just add nullGroupIdbatch to notNullGroupBatches
		//and return notNullGroupBatches. It will be a basic implementation

		//Now We can optimize it
		Collection<Batch<DownloadRequest>> returnBatches = createOptimizedBatchByGroupIds(notNullGroupBatches, nullGroupIdbatch, optimumSize);

		return returnBatches;
	}

	/**
	 * This method will create batches based on Group ids and put them into
	 * second parameter returnNotNullGroupIdBatches, if groupId is Not Null and
	 * if groupid is null then it will add download request to third parameter
	 * 
	 * @param requests
	 * @param returnNotNullGroupIdBatches
	 * @param returnNullGroupIdbatch
	 */
	void createDefaultBatchByGroupIds(Collection<DownloadRequest> requests, Map<GroupID, Batch<DownloadRequest>> returnNotNullGroupIdBatches,
			Batch<DownloadRequest> returnNullGroupIdbatch) {
		Batch<DownloadRequest> oneBatch;
		for (DownloadRequest oneDownloadRequest : requests) {
			if (oneDownloadRequest.getGroupID() == null) {
				oneBatch = returnNullGroupIdbatch;
			} else {
				oneBatch = returnNotNullGroupIdBatches.get(oneDownloadRequest.getGroupID());
				if (oneBatch == null) {
					oneBatch = new Batch<DownloadRequest>();
					returnNotNullGroupIdBatches.put(oneDownloadRequest.getGroupID(), oneBatch);
				}
			}
			oneBatch.add(oneDownloadRequest);
		}
	}

	/**
	 * Sort given batch by batch size
	 * 
	 * @param batchLists
	 */
	void sortBatchesBySize(List<Batch<DownloadRequest>> batchLists) {
		Collections.sort(batchLists, new Comparator<Batch<DownloadRequest>>() {
			@Override
			public int compare(Batch<DownloadRequest> o1, Batch<DownloadRequest> o2) {
				return o1.size() - o2.size();
			}
		});
	}
	
	/**
	 * 
	 * @param sortedBatchList,
	 * @param nullGroupIdbatch a batch of DownloadRequest where groupId was null
	 * @param optimumSize
	 * @return
	 */
	Collection<Batch<DownloadRequest>> createOptimizedBatchByGroupIds(List<Batch<DownloadRequest>> notNullGroupBatches, Batch<DownloadRequest> nullGroupIdbatch,
			int optimumSize) {
		Collection<Batch<DownloadRequest>> returnList = new ArrayList<Batch<DownloadRequest>>();
		
		if ((notNullGroupBatches == null || notNullGroupBatches.isEmpty()) && (nullGroupIdbatch == null || nullGroupIdbatch.size() == 0)) {
			return returnList;
		}

		if ((notNullGroupBatches == null || notNullGroupBatches.isEmpty()) && (nullGroupIdbatch != null && nullGroupIdbatch.size() > 0)) {
			//all Download Request are with null GroupId, then just return one big batch of these download Request
			//But if in future required those to be broken down too, then code can be added
			returnList.add(nullGroupIdbatch);
			return returnList;
		}
		
		//Sort batches
		sortBatchesBySize(notNullGroupBatches);
		

		if (nullGroupIdbatch == null ) {
			//Just create an empty batch to avoid null pointer exception
			nullGroupIdbatch = new Batch<DownloadRequest>();
		}
		int start = 0;
		int end = notNullGroupBatches.size() - 1;
		Batch<DownloadRequest> endBatch;
		Batch<DownloadRequest> startBatch;
		while (start <= end) {
			if(start == end){
				endBatch = notNullGroupBatches.get(end);
				moveItemsFromOneBatchToOther(nullGroupIdbatch, endBatch, optimumSize);
				returnList.add(endBatch);
				//move end pointer towards start
				end--;
				continue;
			}
			//System.out.println(sortedBatchList.get(start).size() + sortedBatchList.get(end).size()+" > "+optimumSize);
			if (notNullGroupBatches.get(start).size() + notNullGroupBatches.get(end).size() > optimumSize) {
				endBatch = notNullGroupBatches.get(end);
				//move items from nullGroupbatch to end batch,which we are about to add into return list
				moveItemsFromOneBatchToOther(nullGroupIdbatch, endBatch, optimumSize);
				returnList.add(endBatch);
				//move end pointer towards start
				end--;
				continue;
			}
			if (notNullGroupBatches.get(start).size() + notNullGroupBatches.get(end).size() <= optimumSize) {
				endBatch = notNullGroupBatches.get(end);
				startBatch = notNullGroupBatches.get(start);
				//move items from nullGroupbatch to end batch,which we are about to add into return list
				moveItemsFromOneBatchToOther(startBatch, endBatch, optimumSize);
				//Do not move end pointer, it still may have space available, next loop iteration will take care of it
				//move start pointer towards end
				start++;
				continue;
			}
			
		}
		if(nullGroupIdbatch.size() > 0){
			returnList.add(nullGroupIdbatch);
		}
		return returnList;
	}

	/**
	 * Move items from fromBatch to toBatch, until toBatch size becomes equal to optimum size or fromBacth becomes empty
	 * @param fromBatch
	 * @param toBatch
	 * @param optimumSize
	 */
	void moveItemsFromOneBatchToOther(Batch<DownloadRequest> fromBatch, Batch<DownloadRequest> toBatch, int optimumSize) {
		if(fromBatch == null || toBatch == null){
			return;
		}
		if(optimumSize <= 0){
			throw new IllegalArgumentException("optimumSize must be more then 0, provided optimumSize "+ optimumSize+" is not valid");
		}
		while (toBatch.size() < optimumSize && fromBatch.size() > 0) {
			// Always remove from end as its list and will have better
			// performance
			toBatch.add(fromBatch.remove(fromBatch.size() - 1));
		}
	}

}
