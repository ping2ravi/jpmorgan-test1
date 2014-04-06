package com.jpmorgan.gce.interview.batch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestBatchSameGroupStrategy {

	BatchSameGroupStrategy batchSameGroupStrategy;

	@Before
	public void init() {
		batchSameGroupStrategy = new BatchSameGroupStrategy();
	}

	@Test
	public void test01_createDefaultBatchByGroupIds() {
		DownloadRequest[] requests = new DownloadRequest[] { new DownloadRequest(new AccountID("SCH001"), new GroupID("GRP001", 1)),
				new DownloadRequest(new AccountID("SCH002"), null), new DownloadRequest(new AccountID("SCH003"), null),
				new DownloadRequest(new AccountID("SCH004"), null), new DownloadRequest(new AccountID("SCH005"), null),
				new DownloadRequest(new AccountID("SCH006"), new GroupID("GRP001", 2)) };
		Map<GroupID, Batch<DownloadRequest>> returnNotNullGroupIdBatches = new HashMap<GroupID, Batch<DownloadRequest>>();
		Batch<DownloadRequest> returnNullGroupIdbatch = new Batch<DownloadRequest>();
		batchSameGroupStrategy.createDefaultBatchByGroupIds(Arrays.asList(requests), returnNotNullGroupIdBatches, returnNullGroupIdbatch);
		assertEquals(2, returnNotNullGroupIdBatches.size());
		assertEquals(4, returnNullGroupIdbatch.size());

	}

	@Test
	public void test02_sortBatchesBySize() {

		List<Batch<DownloadRequest>> batchLists = new ArrayList<Batch<DownloadRequest>>();
		Batch<DownloadRequest> firstBatch = new Batch<DownloadRequest>();
		firstBatch.add(new DownloadRequest(new AccountID("SCH001"), new GroupID("GRP001", 1)));
		firstBatch.add(new DownloadRequest(new AccountID("SCH002"), new GroupID("GRP001", 1)));
		firstBatch.add(new DownloadRequest(new AccountID("SCH003"), new GroupID("GRP001", 1)));
		firstBatch.add(new DownloadRequest(new AccountID("SCH004"), new GroupID("GRP001", 1)));

		Batch<DownloadRequest> secondBatch = new Batch<DownloadRequest>();
		secondBatch.add(new DownloadRequest(new AccountID("SCH004"), new GroupID("GRP002", 2)));
		secondBatch.add(new DownloadRequest(new AccountID("SCH005"), new GroupID("GRP002", 2)));

		Batch<DownloadRequest> thirdBatch = new Batch<DownloadRequest>();
		thirdBatch.add(new DownloadRequest(new AccountID("SCH006"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH007"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH008"), new GroupID("GRP003", 3)));

		batchLists.add(firstBatch);
		batchLists.add(secondBatch);
		batchLists.add(thirdBatch);

		batchSameGroupStrategy.sortBatchesBySize(batchLists);

		// First make sure we get back same number of items
		assertEquals(3, batchLists.size());

		// First element in the list will be with minimum size and then size
		// will keep increasing
		assertEquals(secondBatch, batchLists.get(0));
		assertEquals(thirdBatch, batchLists.get(1));
		assertEquals(firstBatch, batchLists.get(2));

		// Also make sure size of each list hasnt been changed by sort method,
		// to avoid any side effect
		assertEquals(2, batchLists.get(0).size());
		assertEquals(3, batchLists.get(1).size());
		assertEquals(4, batchLists.get(2).size());
	}

	/**
	 * Test where we pass null sorted list and null nullGroupIdbatch
	 */
	@Test
	public void test03_createOptimizedBatchByGroupIds() {
		int optimumSize = 5;
		List<Batch<DownloadRequest>> sortedBatchList = null;
		Batch<DownloadRequest> nullGroupIdbatch = null;
		Collection<Batch<DownloadRequest>> returnCollection = batchSameGroupStrategy.createOptimizedBatchByGroupIds(sortedBatchList, nullGroupIdbatch,
				optimumSize);

		assertNotNull(returnCollection);
		assertEquals(0, returnCollection.size());

	}

	/**
	 * Test where we pass empty sorted List and null nullGroupIdbatch
	 */
	@Test
	public void test04_createOptimizedBatchByGroupIds() {
		List<Batch<DownloadRequest>> sortedBatchList = new ArrayList<Batch<DownloadRequest>>();
		int optimumSize = 5;
		Batch<DownloadRequest> nullGroupIdbatch = null;
		Collection<Batch<DownloadRequest>> returnCollection = batchSameGroupStrategy.createOptimizedBatchByGroupIds(sortedBatchList, nullGroupIdbatch,
				optimumSize);

		assertNotNull(returnCollection);
		assertEquals(0, returnCollection.size());

	}

	/**
	 * Test where we pass null sorted List and empty nullGroupIdbatch
	 */
	@Test
	public void test05_createOptimizedBatchByGroupIds() {
		List<Batch<DownloadRequest>> sortedBatchList = null;
		int optimumSize = 5;
		Batch<DownloadRequest> nullGroupIdbatch = new Batch<DownloadRequest>();
		Collection<Batch<DownloadRequest>> returnCollection = batchSameGroupStrategy.createOptimizedBatchByGroupIds(sortedBatchList, nullGroupIdbatch, optimumSize);

		assertNotNull(returnCollection);
		assertEquals(0, returnCollection.size());

	}

	/**
	 * Test where we pass empty sorted List and empty nullGroupIdbatch
	 */
	@Test
	public void test06_createOptimizedBatchByGroupIds() {
		int optimumSize = 5;
		List<Batch<DownloadRequest>> sortedBatchList = new ArrayList<Batch<DownloadRequest>>();
		Batch<DownloadRequest> nullGroupIdbatch = new Batch<DownloadRequest>();
		Collection<Batch<DownloadRequest>> returnCollection = batchSameGroupStrategy.createOptimizedBatchByGroupIds(sortedBatchList, nullGroupIdbatch, optimumSize);

		assertNotNull(returnCollection);
		assertEquals(0, returnCollection.size());

	}
	
	/**
	 * Test when toBatch is null
	 * Must not throw null pointer Exception
	 */
	@Test
	public void test07_moveItemsFromOneBatchToOther(){
		Batch<DownloadRequest> fromBatch = new Batch<DownloadRequest>();
		Batch<DownloadRequest> toBatch = null;
		int optimumSize = 5;
		batchSameGroupStrategy.moveItemsFromOneBatchToOther(fromBatch, toBatch, optimumSize);
	}

	/**
	 * Test when fromBatch is null
	 * Must not throw null pointer Exception
	 */
	@Test
	public void test08_moveItemsFromOneBatchToOther(){
		Batch<DownloadRequest> fromBatch = null;
		Batch<DownloadRequest> toBatch = new Batch<DownloadRequest>();
		int optimumSize = 5;
		batchSameGroupStrategy.moveItemsFromOneBatchToOther(fromBatch, toBatch, optimumSize);
	}
	
	/**
	 * Test when optimumSize is equal to 0
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test09_moveItemsFromOneBatchToOther(){
		Batch<DownloadRequest> fromBatch = new Batch<DownloadRequest>();
		Batch<DownloadRequest> toBatch = new Batch<DownloadRequest>();
		int optimumSize = 0;
		batchSameGroupStrategy.moveItemsFromOneBatchToOther(fromBatch, toBatch, optimumSize);
	}
	
	/**
	 * Test when optimumSize is less then 0
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test10_moveItemsFromOneBatchToOther(){
		Batch<DownloadRequest> fromBatch = new Batch<DownloadRequest>();
		Batch<DownloadRequest> toBatch = new Batch<DownloadRequest>();
		int optimumSize = -4;
		batchSameGroupStrategy.moveItemsFromOneBatchToOther(fromBatch, toBatch, optimumSize);
	}
	
	/**
	 * Test when fromBatch size and to batch Size is equal to optimum size
	 */
	@Test
	public void test11_moveItemsFromOneBatchToOther(){
		Batch<DownloadRequest> toBatch = new Batch<DownloadRequest>();
		toBatch.add(createDownloadRequest("SCH001","GRP001", 1));
		toBatch.add(createDownloadRequest("SCH002","GRP001", 1));
		
		Batch<DownloadRequest> fromBatch = new Batch<DownloadRequest>();
		fromBatch.add(createDownloadRequest("SCH003","GRP002", 2));
		fromBatch.add(createDownloadRequest("SCH004","GRP002", 2));
		fromBatch.add(createDownloadRequest("SCH005","GRP002", 2));

		
		int optimumSize = 5;
		batchSameGroupStrategy.moveItemsFromOneBatchToOther(fromBatch, toBatch, optimumSize);
		
		assertEquals(0, fromBatch.size());
		assertEquals(5, toBatch.size());
	}
	
	/**
	 * Test when fromBatch size and to batch Size is less then optimum size
	 */
	@Test
	public void test12_moveItemsFromOneBatchToOther(){
		Batch<DownloadRequest> toBatch = new Batch<DownloadRequest>();
		toBatch.add(createDownloadRequest("SCH001","GRP001", 1));
		toBatch.add(createDownloadRequest("SCH002","GRP001", 1));
		
		Batch<DownloadRequest> fromBatch = new Batch<DownloadRequest>();
		fromBatch.add(createDownloadRequest("SCH003","GRP002", 2));
		fromBatch.add(createDownloadRequest("SCH004","GRP002", 2));

		
		int optimumSize = 5;
		batchSameGroupStrategy.moveItemsFromOneBatchToOther(fromBatch, toBatch, optimumSize);
		
		assertEquals(0, fromBatch.size());
		assertEquals(4, toBatch.size());
	}
	
	/**
	 * Test when fromBatch size and to batch Size is more then optimum size
	 */
	@Test
	public void test13_moveItemsFromOneBatchToOther(){
		Batch<DownloadRequest> toBatch = new Batch<DownloadRequest>();
		toBatch.add(createDownloadRequest("SCH001","GRP001", 1));
		toBatch.add(createDownloadRequest("SCH002","GRP001", 1));
		
		Batch<DownloadRequest> fromBatch = new Batch<DownloadRequest>();
		fromBatch.add(createDownloadRequest("SCH003","GRP002", 2));
		fromBatch.add(createDownloadRequest("SCH004","GRP002", 2));
		fromBatch.add(createDownloadRequest("SCH005","GRP002", 2));
		fromBatch.add(createDownloadRequest("SCH006","GRP002", 2));

		
		int optimumSize = 5;
		batchSameGroupStrategy.moveItemsFromOneBatchToOther(fromBatch, toBatch, optimumSize);
		
		assertEquals(1, fromBatch.size());
		assertEquals(5, toBatch.size());
	}
	
	/**
	 * Test when fromBatch is empty
	 */
	@Test
	public void test14_moveItemsFromOneBatchToOther(){
		Batch<DownloadRequest> toBatch = new Batch<DownloadRequest>();
		toBatch.add(createDownloadRequest("SCH001","GRP001", 1));
		toBatch.add(createDownloadRequest("SCH002","GRP001", 1));
		
		Batch<DownloadRequest> fromBatch = new Batch<DownloadRequest>();

		
		int optimumSize = 5;
		batchSameGroupStrategy.moveItemsFromOneBatchToOther(fromBatch, toBatch, optimumSize);
		
		assertEquals(0, fromBatch.size());
		assertEquals(2, toBatch.size());
	}
	
	/**
	 * Test where some of batches can be grouped together and nullGroupIdbatch is empty
	 */
	@Test
	public void test15_createOptimizedBatchByGroupIds() {
		int optimumSize = 5;
		List<Batch<DownloadRequest>> sortedBatchList = new ArrayList<Batch<DownloadRequest>>();
		Batch<DownloadRequest> firstBatch = new Batch<DownloadRequest>();
		firstBatch.add(new DownloadRequest(new AccountID("SCH001"), new GroupID("GRP001", 1)));
		firstBatch.add(new DownloadRequest(new AccountID("SCH002"), new GroupID("GRP001", 1)));

		Batch<DownloadRequest> secondBatch = new Batch<DownloadRequest>();
		secondBatch.add(new DownloadRequest(new AccountID("SCH003"), new GroupID("GRP002", 2)));
		secondBatch.add(new DownloadRequest(new AccountID("SCH004"), new GroupID("GRP002", 2)));
		secondBatch.add(new DownloadRequest(new AccountID("SCH005"), new GroupID("GRP002", 2)));

		Batch<DownloadRequest> thirdBatch = new Batch<DownloadRequest>();
		thirdBatch.add(new DownloadRequest(new AccountID("SCH006"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH007"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH008"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH009"), new GroupID("GRP003", 3)));

		sortedBatchList.add(firstBatch);
		sortedBatchList.add(secondBatch);
		sortedBatchList.add(thirdBatch);
		
		Batch<DownloadRequest> nullGroupIdbatch = new Batch<DownloadRequest>();
		Collection<Batch<DownloadRequest>> returnCollection = batchSameGroupStrategy.createOptimizedBatchByGroupIds(sortedBatchList, nullGroupIdbatch, optimumSize);

		assertNotNull(returnCollection);
		assertEquals(2, returnCollection.size());
	}
	
	/**
	 * Test where no two batches can be grouped together and nullGroupIdbatch is empty
	 */
	@Test
	public void test16_createOptimizedBatchByGroupIds() {
		int optimumSize = 5;
		List<Batch<DownloadRequest>> sortedBatchList = new ArrayList<Batch<DownloadRequest>>();
		Batch<DownloadRequest> firstBatch = new Batch<DownloadRequest>();
		firstBatch.add(new DownloadRequest(new AccountID("SCH001"), new GroupID("GRP001", 1)));
		firstBatch.add(new DownloadRequest(new AccountID("SCH002"), new GroupID("GRP001", 1)));
		firstBatch.add(new DownloadRequest(new AccountID("SCH0021"), new GroupID("GRP001", 1)));

		Batch<DownloadRequest> secondBatch = new Batch<DownloadRequest>();
		secondBatch.add(new DownloadRequest(new AccountID("SCH003"), new GroupID("GRP002", 2)));
		secondBatch.add(new DownloadRequest(new AccountID("SCH004"), new GroupID("GRP002", 2)));
		secondBatch.add(new DownloadRequest(new AccountID("SCH005"), new GroupID("GRP002", 2)));

		Batch<DownloadRequest> thirdBatch = new Batch<DownloadRequest>();
		thirdBatch.add(new DownloadRequest(new AccountID("SCH006"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH007"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH008"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH009"), new GroupID("GRP003", 3)));

		sortedBatchList.add(firstBatch);
		sortedBatchList.add(secondBatch);
		sortedBatchList.add(thirdBatch);
		
		Batch<DownloadRequest> nullGroupIdbatch = new Batch<DownloadRequest>();
		Collection<Batch<DownloadRequest>> returnCollection = batchSameGroupStrategy.createOptimizedBatchByGroupIds(sortedBatchList, nullGroupIdbatch, optimumSize);

		assertNotNull(returnCollection);
		assertEquals(3, returnCollection.size());

	}
	
	/**
	 * Test where some of batches can be grouped together and nullGroupIdbatch is Non empty
	 */
	@Test
	public void test17_createOptimizedBatchByGroupIds() {
		int optimumSize = 5;
		List<Batch<DownloadRequest>> sortedBatchList = new ArrayList<Batch<DownloadRequest>>();
		Batch<DownloadRequest> firstBatch = new Batch<DownloadRequest>();
		firstBatch.add(new DownloadRequest(new AccountID("SCH001"), new GroupID("GRP001", 1)));
		firstBatch.add(new DownloadRequest(new AccountID("SCH002"), new GroupID("GRP001", 1)));

		Batch<DownloadRequest> secondBatch = new Batch<DownloadRequest>();
		secondBatch.add(new DownloadRequest(new AccountID("SCH003"), new GroupID("GRP002", 2)));
		secondBatch.add(new DownloadRequest(new AccountID("SCH004"), new GroupID("GRP002", 2)));
		secondBatch.add(new DownloadRequest(new AccountID("SCH005"), new GroupID("GRP002", 2)));

		Batch<DownloadRequest> thirdBatch = new Batch<DownloadRequest>();
		thirdBatch.add(new DownloadRequest(new AccountID("SCH006"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH007"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH008"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH009"), new GroupID("GRP003", 3)));

		sortedBatchList.add(firstBatch);
		sortedBatchList.add(secondBatch);
		sortedBatchList.add(thirdBatch);
		
		Batch<DownloadRequest> nullGroupIdbatch = new Batch<DownloadRequest>();
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0101"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0102"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0103"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0104"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0105"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0106"), null));
		Collection<Batch<DownloadRequest>> returnCollection = batchSameGroupStrategy.createOptimizedBatchByGroupIds(sortedBatchList, nullGroupIdbatch, optimumSize);

		assertNotNull(returnCollection);
		assertEquals(3, returnCollection.size());
	}
	
	/**
	 * Test where no TWO batches can be grouped together and nullGroupIdbatch is Non empty but all nullGroupIdbatch can be merged
	 * into notNullGroupId batches
	 */
	@Test
	public void test18_createOptimizedBatchByGroupIds() {
		int optimumSize = 5;
		List<Batch<DownloadRequest>> sortedBatchList = new ArrayList<Batch<DownloadRequest>>();
		Batch<DownloadRequest> firstBatch = new Batch<DownloadRequest>();
		firstBatch.add(new DownloadRequest(new AccountID("SCH001"), new GroupID("GRP001", 1)));
		firstBatch.add(new DownloadRequest(new AccountID("SCH002"), new GroupID("GRP001", 1)));
		firstBatch.add(new DownloadRequest(new AccountID("SCH0021"), new GroupID("GRP001", 1)));

		Batch<DownloadRequest> secondBatch = new Batch<DownloadRequest>();
		secondBatch.add(new DownloadRequest(new AccountID("SCH003"), new GroupID("GRP002", 2)));
		secondBatch.add(new DownloadRequest(new AccountID("SCH004"), new GroupID("GRP002", 2)));
		secondBatch.add(new DownloadRequest(new AccountID("SCH005"), new GroupID("GRP002", 2)));

		Batch<DownloadRequest> thirdBatch = new Batch<DownloadRequest>();
		thirdBatch.add(new DownloadRequest(new AccountID("SCH006"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH007"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH008"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH009"), new GroupID("GRP003", 3)));

		sortedBatchList.add(firstBatch);
		sortedBatchList.add(secondBatch);
		sortedBatchList.add(thirdBatch);
		
		Batch<DownloadRequest> nullGroupIdbatch = new Batch<DownloadRequest>();
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0101"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0102"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0103"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0104"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0105"), null));
		Collection<Batch<DownloadRequest>> returnCollection = batchSameGroupStrategy.createOptimizedBatchByGroupIds(sortedBatchList, nullGroupIdbatch, optimumSize);

		assertNotNull(returnCollection);
		assertEquals(3, returnCollection.size());
	}
	
	/**
	 * Test where no TWO batches can be grouped together and nullGroupIdbatch is Non empty but not all nullGroupIdbatch can be merged
	 * into notNullGroupId batches
	 */
	@Test
	public void test19_createOptimizedBatchByGroupIds() {
		int optimumSize = 5;
		List<Batch<DownloadRequest>> sortedBatchList = new ArrayList<Batch<DownloadRequest>>();
		Batch<DownloadRequest> firstBatch = new Batch<DownloadRequest>();
		firstBatch.add(new DownloadRequest(new AccountID("SCH001"), new GroupID("GRP001", 1)));
		firstBatch.add(new DownloadRequest(new AccountID("SCH002"), new GroupID("GRP001", 1)));
		firstBatch.add(new DownloadRequest(new AccountID("SCH0021"), new GroupID("GRP001", 1)));

		Batch<DownloadRequest> secondBatch = new Batch<DownloadRequest>();
		secondBatch.add(new DownloadRequest(new AccountID("SCH003"), new GroupID("GRP002", 2)));
		secondBatch.add(new DownloadRequest(new AccountID("SCH004"), new GroupID("GRP002", 2)));
		secondBatch.add(new DownloadRequest(new AccountID("SCH005"), new GroupID("GRP002", 2)));

		Batch<DownloadRequest> thirdBatch = new Batch<DownloadRequest>();
		thirdBatch.add(new DownloadRequest(new AccountID("SCH006"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH007"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH008"), new GroupID("GRP003", 3)));
		thirdBatch.add(new DownloadRequest(new AccountID("SCH009"), new GroupID("GRP003", 3)));

		sortedBatchList.add(firstBatch);
		sortedBatchList.add(secondBatch);
		sortedBatchList.add(thirdBatch);
		
		Batch<DownloadRequest> nullGroupIdbatch = new Batch<DownloadRequest>();
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0101"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0102"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0103"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0104"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0105"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0106"), null));
		nullGroupIdbatch.add(new DownloadRequest(new AccountID("SCH0107"), null));
		Collection<Batch<DownloadRequest>> returnCollection = batchSameGroupStrategy.createOptimizedBatchByGroupIds(sortedBatchList, nullGroupIdbatch, optimumSize);

		assertNotNull(returnCollection);
		assertEquals(4, returnCollection.size());
	}
	
	/**
	 * Test provided test data
	 */
	@Test
	public void test20_createBatches() {
		int optimumSize = 5;
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
		
		Collection<Batch<DownloadRequest>> returnCollection = batchSameGroupStrategy.createBatches(Arrays.asList(requests), optimumSize);

		assertNotNull(returnCollection);
		assertEquals(3, returnCollection.size());
	}
	
	/**
	 * Test provided test data but pass optimumSize as 0
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test21_createBatches() {
		int optimumSize = 0;
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
		
		batchSameGroupStrategy.createBatches(Arrays.asList(requests), optimumSize);
	}
	
	/**
	 * Test provided test data but pass optimumSize less then 0
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test22_createBatches() {
		int optimumSize = -2;
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
		batchSameGroupStrategy.createBatches(Arrays.asList(requests), optimumSize);

	}
	/**
	 * Test when passed Download Request List is empty
	 */
	@Test
	public void test23_createBatches() {
		int optimumSize = 5;
		Collection<DownloadRequest> requests = new ArrayList<DownloadRequest>();
		Collection<Batch<DownloadRequest>> returnCollection = batchSameGroupStrategy.createBatches(requests, optimumSize);

		assertNotNull(returnCollection);
		assertEquals(0, returnCollection.size());
	}
	/**
	 * Test when passed Download Request List is null
	 */
	@Test
	public void test24_createBatches() {
		int optimumSize = 5;
		Collection<DownloadRequest> requests = null;
		Collection<Batch<DownloadRequest>> returnCollection = batchSameGroupStrategy.createBatches(requests, optimumSize);

		assertNotNull(returnCollection);
		assertEquals(0, returnCollection.size());
	}
	
	private DownloadRequest createDownloadRequest(String accountId, String groupCode, int groupLevel){
		return new DownloadRequest(new AccountID(accountId), new GroupID(groupCode, groupLevel));
	}

}
