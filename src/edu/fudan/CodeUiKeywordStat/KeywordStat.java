package edu.fudan.CodeUiKeywordStat;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**

	This is the worker class for doing actual keyword stat task

 */
class KeywordStat 
{
	//
	// Keyword usage stat for output
	private Map<String, KeywordUsage> keywordUsage = new HashMap<String, KeywordUsage>();
	
	/**
	 
		Generate statistics result by computing
		
		sizeof(set(JimpleHitKeywords) - set(KeywordsOnActivity)).

	 */
	private void diffKeywordStat(String jimpleStatFile, String uiStatFile)
	{
		//
		// Leave parameter validation to StatFile class
		
		//
		// Load both analysis log files
		JimpleKeywordStatFile jimpleStat = new JimpleKeywordStatFile(jimpleStatFile);
		UiKeywordStatFile uiStat = new UiKeywordStatFile(uiStatFile);
		
		//
		// Scan Jimple Hit List to initialize used count
		List<JimpleHit> jimpleHit = jimpleStat.getJimpleHit();
		for (JimpleHit curJimpleHit : jimpleHit)
		{
			if (!keywordUsage.containsKey(curJimpleHit.keyword))
			{
				// Create new counter for current keyword
				KeywordUsage usageCounter = new KeywordUsage();
				usageCounter.keyword = curJimpleHit.keyword;
				usageCounter.usedPackage.add(curJimpleHit.packageName);
				
				keywordUsage.put(curJimpleHit.keyword, usageCounter);
			}
			else
			{
				//
				// Increment the keyword usage counter which already exists
				
				KeywordUsage usageCounter = keywordUsage.get(curJimpleHit.keyword);
				
				usageCounter.usedPackage.add(curJimpleHit.packageName);
			}
		}
		
		//
		// Initialize usedNotShown counter to the size of usedPackage set
		Collection<KeywordUsage> usageSet = keywordUsage.values();
		for (KeywordUsage curUsage : usageSet)
		{
			curUsage.usedNotShownCount = curUsage.usedPackage.size();
		}
		
		//
		// Update the usedNotShown counter by
		// computing the number of keywords shown on Activities
		
		List<ActivityKeywordUsage> activityClassKeywords = jimpleStat.getActivityKeywordUsage();
		List<UiKeyword> uiKeywords = uiStat.getUiKeyword();
		for (ActivityKeywordUsage activityClassKeyword : activityClassKeywords)
		{
			for (UiKeyword uiKeyword : uiKeywords)
			{
				if (activityClassKeyword.activityId == uiKeyword.activityId
					&& activityClassKeyword.keyword.equalsIgnoreCase(uiKeyword.keyword))
				{
					//
					// This keyword is shown on Activities
					String keyword = activityClassKeyword.keyword;
					
					//
					// Decrement the usedNotShowCount
					if (keywordUsage.containsKey(keyword))
					{
						KeywordUsage usageCounter = keywordUsage.get(keyword);
						usageCounter.usedNotShownCount--;
					}
				}
			}
		}
	}
	
	/**
	 
		This method implements JimpleKeywordStat log only stat.
		
		It's invoked when the UI parsing log is missing.

	 */
	private void jimpleKeywordStat(String jimpleStatFile)
	{
		//
		// Leave parameter validation to StatFile class
		
		JimpleKeywordStatFile jimpleStat = new JimpleKeywordStatFile(jimpleStatFile);
		
		//
		// Scan Jimple Hit List to initialize used count
		List<JimpleHit> jimpleHit = jimpleStat.getJimpleHit();
		for (JimpleHit curJimpleHit : jimpleHit)
		{
			if (!keywordUsage.containsKey(curJimpleHit.keyword))
			{
				// Create new counter for current keyword
				KeywordUsage usageCounter = new KeywordUsage();
				usageCounter.keyword = curJimpleHit.keyword;
				usageCounter.usedPackage.add(curJimpleHit.packageName);
				
				keywordUsage.put(curJimpleHit.keyword, usageCounter);
			}
			else
			{
				//
				// Increment the keyword usage counter already exists
				
				KeywordUsage usageCounter = keywordUsage.get(curJimpleHit.keyword);
				
				usageCounter.usedPackage.add(curJimpleHit.packageName);
			}
		}		
		
		//
		// Set usedNotShownCount as the size of usedPackage set
		Collection<KeywordUsage> usageSet = keywordUsage.values();
		for (KeywordUsage curUsage : usageSet)
		{
			curUsage.usedNotShownCount = curUsage.usedPackage.size();
		}
	}
	
	KeywordStat(String jimpleStatFile, String uiStatFile)
	{
		//
		// Check parameters
		File jimpleStatInst = new File(jimpleStatFile);
		if (!jimpleStatInst.isFile())
		{
			throw new RuntimeException("Given Jimple stat file path isn't valid: " + jimpleStatFile);
		}
		
		File uiStatFileInst = new File(uiStatFile);
		if (!uiStatFileInst.isFile())
		{
			//
			// If UI stat file doesn't exist, 
			// do statistic on Jimple Hit only
			
			jimpleKeywordStat(jimpleStatFile);
		}
		else
		{
			//
			// Do diff statistic on code keyword usage and UI keyword usage
			// We should exclude proper UI keyword usage from code keyword usage set
			
			diffKeywordStat(jimpleStatFile, uiStatFile);
		}
	}
	
	//
	// Output information access methods
	
	Collection<KeywordUsage> getKeywordUsage()
	{
		return keywordUsage.values();
	}
}

/**

	Data class for recording keyword usage results.

 */
class KeywordUsage
{
	String keyword;
	
	Set<String> usedPackage = new HashSet<String>();
	int usedNotShownCount;
}