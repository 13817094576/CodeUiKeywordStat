package edu.fudan.CodeUiKeywordStat;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

public class Main 
{

	//
	// Program status
	public static final int EXIT_NORMAL = 0;
	public static final int EXIT_ERROR = 1;
	
	private static void ShowUsage()
	{
		System.out.println("Usage: java -jar CodeUiKeywordStat.jar JIMPLE_KEYWORD_STAT_DIR UI_KEYWORD_STAT_DIR");
		System.out.println("This program is written and tested on Java 1.7");
	}
	
	private static void CheckArguments(String[] args)
	{
		if (args.length < 2)
		{
			ShowUsage();
			Runtime.getRuntime().exit(EXIT_NORMAL);;
		}
		
		File jimpleKeywordStatDir = new File(args[0]);
		if (!jimpleKeywordStatDir.isDirectory())
		{
			System.err.println("Given jimple keyword stat dir isn't valid: " + args[0]);
			throw new RuntimeException("Given jimple keyword stat dir isn't valid: " + args[0]);
		}
		
		File uiKeywordStatDir = new File(args[1]);
		if (!uiKeywordStatDir.isDirectory())
		{
			System.err.println("Given UI keyword stat dir isn't valid: " + args[1]);
			throw new RuntimeException("Given UI keyword stat dir isn't valid: " + args[1]);
		}		
	}
	
	
	
	public static void main(String[] args) 
	{
		CheckArguments(args);
		
		File jimpleKeywordStatDir = new File(args[0]);
		File uiKeywordStatDir = new File(args[1]);
		
		String[] logFileNames = jimpleKeywordStatDir.list();
		for (String logFileName : logFileNames)
		{
			//
			// Validate the log file name
			if (!logFileName.endsWith(".apk.log"))
			{
				System.err.println("[WARN] Log file name is invalid: " + logFileName);
				
				// Skip current log file
				continue;
			}
			
			//
			// Do keyword statistics with stat files
			
			File jimpleStatFile = new File(jimpleKeywordStatDir, logFileName);
			File uiStatFile = new File(uiKeywordStatDir, logFileName);
			
			KeywordStat keywordStat = new KeywordStat(jimpleStatFile.getPath(), uiStatFile.getPath());
			
			//
			// Print keyword statistic of current App
			
			// Remove the '.apk.log' at the end of log file name
			String appName = logFileName.substring(0, logFileName.length() - 8);
			
			Collection<KeywordUsage> keywordUsage = keywordStat.getKeywordUsage();
			for (KeywordUsage curUsage : keywordUsage)
			{
				String outLine = String.format("%s,%s,%d,%d", 
						appName, curUsage.keyword, curUsage.usedNotShownCount, curUsage.usedCount);
				
				System.out.println(outLine);
			}
		}
		
		//
		// Exit normally
	}

}
