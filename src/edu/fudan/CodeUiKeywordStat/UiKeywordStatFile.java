package edu.fudan.CodeUiKeywordStat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class UiKeywordStatFile 
{
	private List<UiKeyword> uiKeyword = new ArrayList<UiKeyword>();
	
	private void loadStatFile(String statFileName)
	{
		//
		// Parameter validation to leave to classes in java.io
		
		List<String> lines = FileUtil.readAllLinesFromFile(statFileName);
		
		//
		// Load the keyword usage of each activity
		for (String line : lines)
		{
			String[] lineParts = line.split(",", 3);
			if (lineParts.length != 3)
			{
				System.err.println("[WARN] Text line in UI keyword stat file is invalid: " + line);
				
				// Ignore invalid text line
				continue;
			}
			
			// Get activity ID
			int activityId = 0;
			try
			{
				activityId = Integer.parseInt(lineParts[0]);
			}
			catch (NumberFormatException e)
			{
				System.err.println("[WARN] Activity ID in UI keyword stat file is invalid: " + line);
				
				// Ignore text line containing invalid activity ID
				continue;
			}
			
			String[] keywords = lineParts[2].split(",");
			
			// Record keyword usage in UI
			for (String keyword : keywords)
			{
				// Canonicalize keyword text
				keyword = keyword.trim();
				if (keyword.isEmpty())
				{
					// Skip empty keyword
					continue;
				}
				
				UiKeyword uiKeywordInst = new UiKeyword();
				uiKeywordInst.activityId = activityId;
				uiKeywordInst.keyword = keyword;
				
				uiKeyword.add(uiKeywordInst);
			}
		}
	}
	
	UiKeywordStatFile(String statFileName)
	{
		//
		// Check parameters
		File statFile = new File(statFileName);
		if (!statFile.isFile())
		{
			throw new RuntimeException("Given UI keyword stat file isn't valid: " + statFileName);
		}
		
		loadStatFile(statFileName);
	}
	
	//
	// Output information access methods
	
	List<UiKeyword> getUiKeyword()
	{
		return uiKeyword;
	}
}

//
// Data classes for recording
// keywords in UI of app

class UiKeyword
{
	String keyword;
	int activityId;
}