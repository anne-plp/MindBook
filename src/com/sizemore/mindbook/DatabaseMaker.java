package com.sizemore.mindbook;

import java.util.Date;
import java.util.HashMap;

public class DatabaseMaker
{
	public String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	public String[] days = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
	
public HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>>	makeDatabase()
{
	HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>> database = new HashMap<String, HashMap<String, HashMap<String, HashMap<String,String>>>>();
	String date = new Date().toString();
	
	boolean gotDayOfWeek = false;
	boolean gotMonth = false;
	boolean gotDayOfMonth = false;
	boolean gotTime = false;
	boolean gotZone = false;
	
	String dayOfWeek = "";
	String month = "";
	String dayOfMonth = "";
	String time = "";
	String zone = "";
	String year = "";
	
	System.out.println(date);
	int length = date.length();
	int prevIndex = 0;
	int currentIndex = 0;
	for (int i=0; i<length; i++)
		{
		if (date.charAt(i) == ' ')
			{
			prevIndex = currentIndex;
			currentIndex = i;
			if (!gotDayOfWeek)
				{
				dayOfWeek = date.substring(prevIndex,currentIndex);
				gotDayOfWeek = true;
				}
			else if (!gotMonth)
				{
				month = date.substring(prevIndex,currentIndex);
				gotMonth = true;
				}
			else if (!gotDayOfMonth)
				{
				dayOfMonth = date.substring(prevIndex,currentIndex);
				gotDayOfMonth = true;
				}
			else if (!gotTime)
				{
				time = date.substring(prevIndex,currentIndex);
				gotTime = true;
				}
			else
				{
				zone = date.substring(prevIndex,currentIndex);
				gotZone = true;
				}
			}
			if (i == (length-1))
				{
				year = date.substring(currentIndex+1);
				}
		}
	HashMap<String,HashMap<String,HashMap<String,String>>> yearMap = new HashMap<String,HashMap<String,HashMap<String,String>>> ();
	database.put(year, yearMap);
	
	for (int i = 0; i < 12; i++)
		{
		HashMap<String,HashMap<String,String>> monthMap = new HashMap<String,HashMap<String,String>>();
		database.get(year).put(months[i], monthMap);
		}
	for (int i = 0; i < 12; i++)
		{
		for (int j = 0; j < 31; j++)
			{
			HashMap<String,String> dayMap = new HashMap<String,String>();
			database.get(year).get(months[i]).put(days[j], dayMap);
			}
		}
	
	return database;
}

public HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>> addYear(HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>> database, String year)

{
	String date = new Date().toString();
	boolean gotDayOfWeek = false;
	boolean gotMonth = false;
	boolean gotDayOfMonth = false;
	boolean gotTime = false;
	boolean gotZone = false;
	
	String dayOfWeek = "";
	String month = "";
	String dayOfMonth = "";
	String time = "";
	String zone = "";
	
	System.out.println(date);
	int length = date.length();
	int prevIndex = 0;
	int currentIndex = 0;
	for (int i=0; i<length; i++)
		{
		if (date.charAt(i) == ' ')
			{
			prevIndex = currentIndex;
			currentIndex = i;
			if (!gotDayOfWeek)
				{
				dayOfWeek = date.substring(prevIndex,currentIndex);
				gotDayOfWeek = true;
				}
			else if (!gotMonth)
				{
				month = date.substring(prevIndex,currentIndex);
				gotMonth = true;
				}
			else if (!gotDayOfMonth)
				{
				dayOfMonth = date.substring(prevIndex,currentIndex);
				gotDayOfMonth = true;
				}
			else if (!gotTime)
				{
				time = date.substring(prevIndex,currentIndex);
				gotTime = true;
				}
			else
				{
				zone = date.substring(prevIndex,currentIndex);
				gotZone = true;
				}
			}
		}
	HashMap<String,HashMap<String,HashMap<String,String>>> yearMap = new HashMap<String,HashMap<String,HashMap<String,String>>> ();
	database.put(year, yearMap);
	
	for (int i = 0; i < 12; i++)
		{
		HashMap<String,HashMap<String,String>> monthMap = new HashMap<String,HashMap<String,String>>();
		database.get(year).put(months[i], monthMap);
		}
	for (int i = 0; i < 12; i++)
		{
		for (int j = 0; j < 31; j++)
			{
			HashMap<String,String> dayMap = new HashMap<String,String>();
			database.get(year).get(months[i]).put(days[j], dayMap);
			}
		}
	
	return database;
}
	
}
