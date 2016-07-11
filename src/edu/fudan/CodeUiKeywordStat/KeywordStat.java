package edu.fudan.CodeUiKeywordStat;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class KeywordStat 
{
	private Map<String, KeywordUsage> keywordUsage = new HashMap<String, KeywordUsage>();
	
	private void diffKeywordStat(String jimpleStatFile, String uiStatFile)
	{
		//
		// Leave parameter validation to StatFile class
		
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
				usageCounter.usedCount = 1;
				
				// Initialize usedNotShownCount the same value as usedCount first
				usageCounter.usedNotShownCount = 1;
				
				keywordUsage.put(curJimpleHit.keyword, usageCounter);
			}
			else
			{
				//
				// Increment the keyword usage counter already exists
				
				KeywordUsage usageCounter = keywordUsage.get(curJimpleHit.keyword);
				
				usageCounter.usedCount++;
				usageCounter.usedNotShownCount++;
			}
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
					
					if (keywordUsage.containsKey(keyword))
					{
						KeywordUsage usageCounter = keywordUsage.get(keyword);
						usageCounter.usedNotShownCount--;
					}
				}
			}
		}
	}
	
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
				usageCounter.usedCount = 1;
				
				// Initialize usedNotShownCount the same value as usedCount first
				usageCounter.usedNotShownCount = 1;
				
				keywordUsage.put(curJimpleHit.keyword, usageCounter);
			}
			else
			{
				//
				// Increment the keyword usage counter already exists
				
				KeywordUsage usageCounter = keywordUsage.get(curJimpleHit.keyword);
				
				usageCounter.usedCount++;
				usageCounter.usedNotShownCount++;
			}
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

class KeywordUsage
{
	String keyword;
	
	int usedCount;
	int usedNotShownCount;
}