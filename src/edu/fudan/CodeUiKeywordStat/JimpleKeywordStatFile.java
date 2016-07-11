package edu.fudan.CodeUiKeywordStat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class JimpleKeywordStatFile 
{
	//
	// Output information
	private List<JimpleHit> jimpleHit = new ArrayList<JimpleHit>();
	private List<ActivityKeywordUsage> activityKeywordUsage = new ArrayList<ActivityKeywordUsage>();
	
	/**

		Find the line number of start mark of a section
		
		If no start mark found, -1 is returned.

	 */
	private int findStartOfSection(List<String> lines, String startMark)
	{
		for (int i=0; i<lines.size(); i++)
		{
			String line = lines.get(i);
			
			if (line.startsWith(startMark))
			{
				return i;
			}
		}	
		
		//
		// No start mark found
		return -1;
	}
	
	/**
	 
		Load the content of Jimple Hit section to class,
		
		and return if the load process is successful.

	 */
	private boolean loadJimpleHit(List<String> lines, int startOfJimpleHit)
	{
		for (int i = startOfJimpleHit+1; i<lines.size(); i++)
		{
			String line = lines.get(i);
			
			//
			// Check if we encountered end mark of Jimple Hit section
			if (line.startsWith("Jimple with Keywords in APK <"))
			{
				// If so, the Jimple Hit section loading is finished
				return true;
			}
			
			String[] lineParts = line.split(",", 3);
			if (lineParts.length < 3)
			{
				System.err.println("[WARN} Jimple Hit section line is invalid: " + line);
				continue;
			}
			
			//
			// Record Jimple Hit
			JimpleHit jimpleHitInst = new JimpleHit();
			jimpleHitInst.jimpleId = Integer.parseInt(lineParts[0]);
			jimpleHitInst.keyword = lineParts[1];
			
			jimpleHit.add(jimpleHitInst);
		}		
		
		//
		// Not end mark of Jimple Hit section found
		System.err.println("[WARN] No end mark of Jimple Hit section found");
		return false;
	}
	
	/**
		 
		Load the content of Activity Keyword Usage section to class,
		
		and return if the load process is successful.
	
	 */
	private boolean loadActivityKeywordUsage(List<String> lines, int startOfJimpleHit)
	{
		for (int i = startOfJimpleHit+1; i<lines.size(); i++)
		{
			String line = lines.get(i);
			
			//
			// Check if we encountered end mark of Activity Keyword Usage section
			if (line.startsWith("Root Caller Activity Classes <"))
			{
				// If so, the Activity Keyword Usage section loading is finished
				return true;
			}
			
			String[] lineParts = line.split(",");
			if (lineParts.length != 4)
			{
				System.err.println("[WARN} Activity Keyword Usage section line is invalid: " + line);
				continue;
			}
			
			//
			// Record Activity Keyword Usage
			ActivityKeywordUsage usage = new ActivityKeywordUsage();
			usage.initiateJimpleId = Integer.parseInt(lineParts[0]);
			usage.keyword = lineParts[2];
			
			//
			// Parsing activity ID
			String activityIdInStr = lineParts[3];
			
			// Remove the 'S' prefix of activity ID
			// There may be multiple 'S' prefix
			int idBegin=0;
			for (idBegin=0; idBegin<activityIdInStr.length(); idBegin++)
			{
				// Loop until 'S' prefix is skipped
				if (activityIdInStr.charAt(idBegin) != 'S')
				{
					break;
				}
			}
			activityIdInStr = activityIdInStr.substring(idBegin);
			
			
			try
			{
				usage.activityId = Integer.parseInt(lineParts[3]);
			}
			catch (NumberFormatException e)
			{
				// Activity ID in file is invalid
				// Skip current activity keyword usage
				continue;
			}
			
			activityKeywordUsage.add(usage);
		}		
		
		//
		// Not end mark of Jimple Hit section found
		System.err.println("[WARN] No end mark of Jimple Hit section found");
		return false;
	}	
	
	private void loadStatFile(String statFileName)
	{
		//
		// Parameter validation to leave to classes in java.io
		
		List<String> lines = FileUtil.readAllLinesFromFile(statFileName);
		
		//
		// Load Jimple statements hit
		
		int startOfJimpleHit = findStartOfSection(lines, "Jimple with Keywords in APK >");
		if (startOfJimpleHit < 0)
		{
			// No start mark of Jimple Hit section found
			System.err.println("[WARN] No Jimple Hit start mark found");
			
			// End parsing
			return;
		}
		
		if (!loadJimpleHit(lines, startOfJimpleHit))
		{
			// Jimple Hit section doesn't successfully loaded
			return;
		}
		
		//
		// Load activity keyword usage
		
		int startOfActivityUsage = findStartOfSection(lines, "Root Caller Activity Classes >");
		if (startOfActivityUsage < 0)
		{
			// No start mark of Activity Usage section found
			System.err.println("[WARN] No start mark of Activity Keyword Usage section found");
			
			// End parsing
			return;
		}
		
		if (!loadActivityKeywordUsage(lines, startOfActivityUsage))
		{
			// Jimple Hit section doesn't successfully loaded
			return;
		}
		
		//
		// Jimple keyword stat file loading finished
	}
	
	JimpleKeywordStatFile(String statFileName)
	{
		//
		// Check parameters
		File statFile = new File(statFileName);
		if (!statFile.isFile())
		{
			throw new RuntimeException("Given jimple keyword stat file isn't valid: " + statFileName);
		}
		
		loadStatFile(statFileName);
	}
	
	//
	// Output information access methods
	
	List<JimpleHit> getJimpleHit()
	{
		return jimpleHit;
	}
	
	List<ActivityKeywordUsage> getActivityKeywordUsage()
	{
		return activityKeywordUsage;
	}
}

//
// Data classes for recording info on
// Jimple statements and activity

class JimpleHit
{
	int jimpleId;
	String keyword;
}

class ActivityKeywordUsage
{
	int initiateJimpleId;
	String keyword;
	int activityId;
}