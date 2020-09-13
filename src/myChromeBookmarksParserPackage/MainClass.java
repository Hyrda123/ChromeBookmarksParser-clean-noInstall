package myChromeBookmarksParserPackage;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.scene.shape.Path;


public class MainClass 
{
	public static void main (String[] args)
	{
		try 
		{
			JSONArray bookmarks = getBookmarks();
			recursiveExploration(bookmarks, "UrlsInBar", "");		
		} 
		catch (IOException e) 	 { System.out.println("Reading Error \n" + e.getStackTrace()); }
		catch (ParseException e) { System.out.println("Parse Error \n"   + e.getStackTrace()); }
	}
	
	private static JSONArray getBookmarks() throws IOException, ParseException
	{
		JSONParser jpar = new JSONParser();
		FileReader reader = new FileReader("src/myChromeBookmarksParserPackage/bookmarks.JSON");
		JSONObject json = (JSONObject) jpar.parse(reader);
		JSONObject roots =(JSONObject) json.get("roots");
		JSONObject bookmarkBar = (JSONObject) roots.get("bookmark_bar");
		return (JSONArray) bookmarkBar.get("children");
	}
	
	private static void recursiveExploration(JSONArray folderElements, String folderName, String parentFolder)
	{
		ArrayList<String> urlsInFolder = new ArrayList<String>();
		for(Object obj : folderElements) 
		{
			JSONObject element = (JSONObject) obj;
			String objType = (String) element.get("type");
			
			if(objType.equals("url"))	
				urlsInFolder.add(getUrl(element));
			else if(objType.equals("folder"))
			{
				JSONArray newFolderElements = (JSONArray) element.get("children");
				recursiveExploration(newFolderElements, getNewFolderName(element), parentFolder + "___" + folderName);
				continue;
			}
			else 
			{
				System.out.println("Unknown bookmark type: " + objType + "\nId: " + (String) element.get("id") + "\n");
				continue;
			}
		}

		String fileName = parentFolder + "___" + folderName + ".txt";
		try { writeUrlsOnFile(urlsInFolder, fileName); } 
		catch (IOException e) { System.out.println("Writing " + fileName + "  Error \n" + e.getStackTrace()); }
	}

	private static String getUrl(JSONObject element)
	{
		String id   = (String) element.get("id");
		String name = (String) element.get("name");
		String url  = (String) element.get("url");
		
		return "Id:   " + id + "\nName: " + name + "\nUrl:  " + url + "\n\n";
	}
	
	public static String getNewFolderName(JSONObject element)
	{
		String id   = (String) element.get("id");
		String name = (String) element.get("name");
		return id + " - " + name;
	}
	
	private static void writeUrlsOnFile(ArrayList<String> urls, String filename) throws IOException
	{
		FileWriter writer = new FileWriter(filename);
		
		for(String url: urls)
			if(url != null)
				writer.write(url+"\n");
	
		writer.close();
	}
}





