/*
 * ADOBE SYSTEMS INCORPORATED
 * Copyright 2007 Adobe Systems Incorporated
 * All Rights Reserved
 *  
 * NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the 
 * terms of the Adobe license agreement accompanying it.  If you have received this file from a 
 * source other than Adobe, then your use, modification, or distribution of it requires the prior 
 * written permission of Adobe.
 */

package com.adobe.livecycle.samples.directory.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The object of this class would be used as a cookie between different batches of Principals to be sent for 
 * Synchronization.
 */
public class SampleStateTracker {
	private int currentBatchNo = 0;
	private int totalBatches = 10;
	private int batchSize = 10;
	private List<Integer> principalList = new ArrayList<Integer>(); 	

	public SampleStateTracker(int currentBatchNo, int totalBatches, int batchSize) {
		super();
		this.currentBatchNo = currentBatchNo;
		this.totalBatches = totalBatches;
		this.batchSize = batchSize;
		
		int numPrincipals = totalBatches * batchSize;
		int numRemoved = numPrincipals / 10;
		int numSampleSize = numPrincipals + numRemoved;
		
		Random random = new Random();
		
		for (int i = 0;i < numSampleSize;i++) {
			principalList.add(i);
		}
		
		for (int i = 0;i < numRemoved;i++) {
			int index = random.nextInt(principalList.size());
			principalList.remove(index);
		}
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public int getCurrentBatchNo() {
		return currentBatchNo;	
	}

	public void setCurrentBatchNo(int currentBatchNo) {
		this.currentBatchNo = currentBatchNo;
	}

	public int getTotalBatches() {
		return totalBatches;
	}

	public void setTotalBatches(int totalBatches) {
		this.totalBatches = totalBatches;
	}

	public List<Integer> getCurrentBatchList() {
		int startIndex = currentBatchNo * batchSize;
		int endIndex = Math.min(startIndex + batchSize, principalList.size());
		return principalList.subList(startIndex, endIndex);
	}

}
