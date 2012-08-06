package com.rms.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RMSShell {
	public static void main(String[] args) throws java.io.IOException 
	{

		String commandLine;
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

		//Break with Ctrl+C
		while (true) 
		{
			//read the command
			System.out.print("shell>");
			commandLine = console.readLine();
	
		    //if just a return, loop
		    if (commandLine.equals(""))
		    continue;
		    //history
		    if(commandLine.equals('*'))
		    {
		      //new class HistoryStringArray();
		     // {
		       //   history[4] = history[3]
		        //  history[3] = history[2]
		        //  history[2] = history[1]
		        //  history[1] = history[0]
		        //  history[0] = commandLine
		    }
		    
		    //help command
		    if (commandLine.equals("help"))
		    {
		        System.out.println();
		        System.out.println();
		        System.out.println("Welcome to the shell");
		        System.out.println("Written by: Brett Salmiery");
		        System.out.println("CIS 390   -  Dr. Guzide");
		        System.out.println("--------------------");
		        System.out.println();
		        System.out.println("Commands to use:");
		        System.out.println("1) cat prog.java");
		        System.out.println("2) exit");
		        System.out.println("3) clear");
		        System.out.println();
		        System.out.println();
		        System.out.println("---------------------");
		        System.out.println();
		    }
	
		    if (commandLine.equals("clear"))
		    {
				for( int cls = 0; cls < 10; cls++ )
				{
					System.out.print("");
				}
		    }
	
		    if (commandLine.endsWith(".java"))
		    {
				if(commandLine.startsWith("cat"))
				{
					System.out.println("test");
					ProcessBuilder pb = new ProcessBuilder();
					//pb = new ProcessBuilder(commandLine);
				}
				else
			    {
					System.out.println("Incorrect Command");
			    }
		    }
	
		    if (commandLine.equals("exit"))
		    {
		        System.out.println("...Terminating the Virtual Machine");
		        System.out.println("...Done");
		        System.out.println("Please Close manually with Options > Close");
		        System.exit(0);
		    }
		}
	}
}

