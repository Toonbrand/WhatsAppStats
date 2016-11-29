package nl.toonbrand.whatsappstats;

import java.util.Scanner;

public class Checks {

	public static boolean isActualMessage(String line){
		if(line.substring(20).equalsIgnoreCase("Messages you send to this chat and calls are now secured with end-to-end encryption. Tap for more info.") || 
				line.substring(20).equalsIgnoreCase("Messages you send to this group are now secured with end-to-end encryption. Tap for more info.") ||
				line.contains(" created group ") ||
				line.contains(" added ") ||
				(line.contains(" changed the subject from ") && line.contains(" to ")) ||
				line.contains(" changed this group's icon")){
			return false;
		}
		return true;
	}

	public static boolean isNewLine(String line){
		if(line.length()>10 && line.charAt(2) == '/' && line.substring(5, 8).equals("/20") && line.charAt(10) == ','){
			return false;
		}
		else{
			return true;
		}
	}

	public static boolean isWholeWord(String line, int expresLength, int i){
		char a = 'a';
		for(int j=0;j<26;j++){
			char check = (char)(a+j);
			if(i!=0 && line.toLowerCase().charAt(i-1)==check || line.concat(" ").toLowerCase().charAt(i+expresLength)==check){
				return false;
			}
		}
		return true;
	}

	public static boolean yesNoInput(Scanner s){
		while(true){
			String input = s.nextLine().toLowerCase();
			switch (input){
			case "y": 
			case "yes" : 
			case "yea": 
			case "yeah": 
			case "true": 
			case "affirmative": 
			case "positive": 
			case "ten four": 
				return true;
				
			case "n" : 
			case "no" : 
			case "nah" : 
			case "nope" : 
			case "false" : 
			case "negative" : 
				return false;
				
			default:
				System.out.println("I didn't get that. Did you mean yes or no? ");
			}
		}
	}
}
