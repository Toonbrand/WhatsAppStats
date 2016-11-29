package nl.toonbrand.whatsappstats;

import static nl.toonbrand.whatsappstats.Checks.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

/**
 * Created by toon on 24-11-2016.
 */
public class ChatSearcher {
	static Person allMessages = new Person("allMessages");
	static ArrayList<Person> persons = new ArrayList<>();
	static DecimalFormat df = new DecimalFormat("#.##");

	public static void main(String[] args) {
		final long loadTime = System.currentTimeMillis();

		String fileLoc = "Sources/WhatsApp_Chat_keesdag.txt";
		createFilledPersonen(fileLoc);

		final long endLoadTime = System.currentTimeMillis();

		mostMessages(persons);
		df.setRoundingMode(RoundingMode.HALF_EVEN);
		Scanner s = new Scanner(System.in);
		System.out.println("");
		System.out.print("Enter search terms: ");
		ArrayList<String> popWords = new ArrayList<String>(Arrays.asList(s.nextLine().split(" ")));
		System.out.print("Case sensitive? ");
		boolean caseSensitive = yesNoInput(s);
		System.out.print("Whole word? ");
		boolean wholeWord = yesNoInput(s);
		System.out.println("");
		s.close();

		final long searchTime = System.currentTimeMillis();

		if(popWords.size()>1){
			findExpressionsForUser(mostpopularWord(popWords, caseSensitive, wholeWord).getWord(), caseSensitive, wholeWord); 
		}
		else{
			findExpressionsForUser(popWords.get(0), caseSensitive, wholeWord);
		}

		final long endSearchTime = System.currentTimeMillis();
		System.out.println("\n[File loading took: " + (endLoadTime - loadTime) + "ms]");
		System.out.println("[Searching took:    " + (endSearchTime - searchTime) + "ms]");
	}

	public static void createFilledPersonen(String fileLoc){
		ArrayList<String> users = new ArrayList<String>();
		Person lastPerson = null;

		System.out.println("Getting txt file from [" + fileLoc + "]");
		try{
			FileReader reader = new FileReader(fileLoc);
			Scanner s = new Scanner(reader);
			while(s.hasNextLine()){
				String line = s.nextLine();
				line = cleanLine(line);
				if(isNewLine(line)){
					Message lastMessage = lastPerson.getMessages().get(lastPerson.getMessages().size()-1);
					lastMessage.setContent(lastMessage.getContent()+(" "+line));
				}

				else{
					if (isActualMessage(line)){
						String date = line.substring(1,10);
						String time = line.substring(12,17);
						String name = getNameFromMessage(line);
						String content = line.substring(22+name.length(),line.length());

						if(!users.contains(name)){
							users.add(name);
							persons.add(new Person(name));
						}
						for(Person p : persons){
							if(p.getName().equals(name)){
								Message message = new Message(content, date, time, p);
								p.addMessage(message);
								allMessages.addMessage(message);
								lastPerson = p;
								break;
							}
						}
					}
				}
			}
			s.close();
		}

		catch (FileNotFoundException e){
			System.out.println("File not found");
			System.exit(0);
		}

		System.out.println("File successfully loaded! \n");
	}

	public static ArrayList<String> allWords(){
		ArrayList<String> allWords = new ArrayList<String>();
		for(Message m : allMessages.messages){
			m.setContent(m.getContent().replaceAll("[^a-zA-Z ]", ""));
			for(String s : m.getContent().split(" ")){
				if(!s.equals("") && !s.equals(" ")){
					allWords.add(s.toLowerCase());
				}
			}
		}
		Collections.sort(allWords);
		return allWords;
	}

	public static ArrayList<Word> wordOccurances(ArrayList<String> allWords){
		ArrayList<Word> words = new ArrayList<>();
		int count = 1;
		String lastWord = allWords.get(0);
		for(String word : allWords){
			if(word.equals(lastWord)){
				count++;
			}
			else{
				words.add(new Word(lastWord, count));
				lastWord = word;
				count = 1;
			}
		}

		return words;
	}

	public static Word mostpopularWord(ArrayList<String> words, boolean caseSensitiveS, boolean wholeWordS){
		ArrayList<Word> popularWords = new ArrayList<Word>();
		String longestWord = "----";
		Word mostpopular = new Word("",0);
		int wordTotal=0;
		for(String s : words){
			if(s.length()>longestWord.length()){
				longestWord=s;
			}
			int popularity = 0;
			for(Person p : persons){
				popularity+=findExpressionOccuranceForPerson(p, s, caseSensitiveS, wholeWordS);
			}
			wordTotal+=popularity;
			Word word = new Word(s, popularity);
			popularWords.add(word);
			if(word.getOccurances()>mostpopular.getOccurances()){
				mostpopular=word;
			}
		}

		String leftAlignFormat = "| %-"+longestWord.length()+"s | %-5s | %-7s | %n";
		String finalLineFormat = "| %-"+(longestWord.length()+18)+"s | %n";
		String breakerFormat = "+%-"+(longestWord.length()+2)+"s+%-7s+%-9s+%n";

		String wordDashes = longestWord.replaceAll(".", "-");
		System.out.println("**Most populair word**");
		System.out.format(breakerFormat,"-"+wordDashes+"-","-------","---------");
		System.out.format(leftAlignFormat, "Word", "Count", "Percent");
		System.out.format(breakerFormat,"-"+wordDashes+"-","-------","---------");
		for(Word w : popularWords){
			System.out.format(leftAlignFormat, w.word, w.occurances, df.format(w.occurances*100f/wordTotal)+"%");
		}
		System.out.format(breakerFormat,"-"+wordDashes+"-","-------","---------");
		System.out.format(finalLineFormat, "Most popular: \"" + mostpopular.getWord()+"\"");
		System.out.format(breakerFormat,"-"+wordDashes+"-","-------","---------");
		System.out.println("");

		return mostpopular;
	}

	public static void findExpressionOccurance(String expression, boolean caseSensitiveS, boolean wholeWordS){
		ArrayList<Message> relevantMessages = new ArrayList<Message>();
		String longestMessage = "-------";
		String longestName = "-----";
		for(Message m : allMessages.getMessages()){
			String content = m.getContent();
			int expresLength = expression.length();
			for(int i=0;i<=content.length()-expresLength;i++){
				if(caseSensitiveS ? (content.substring(i, expresLength+i).equals(expression)) : (content.substring(i, expresLength+i).equalsIgnoreCase(expression))){
					if(wholeWordS ? (isWholeWord(content, expresLength, i)) : (true)){
						relevantMessages.add(m);
						if(m.getContent().length()>103){
							longestMessage=m.getContent().substring(0,103);
						}
						else if(m.getContent().length()>longestMessage.length()){
							longestMessage=m.getContent();
						}
						if(m.getOwner().getName().length()>longestName.length()){
							longestName=m.getOwner().getName();
						}
						break;
					}
				}
			}
		}

		String leftAlignFormat = "| %-"+longestMessage.length()+"s | %-"+longestName.length()+"s | %-9s | %-8s | %n";
		String breakerFormat = "+%-"+(longestMessage.length()+2)+"s+%-"+(longestName.length()+2)+"s+%-11s+%-10s+%n";

		String messageDashes = longestMessage.replaceAll(".", "-");
		String nameDashes = longestName.replaceAll(".", "-");

		System.out.format(breakerFormat,"-"+messageDashes+"-","-"+nameDashes+"-","-----------","---------");
		System.out.format(leftAlignFormat, "Message", "Owner", "Date", "Time");
		System.out.format(breakerFormat,"-"+messageDashes+"-","-"+nameDashes+"-","-----------","---------");
		for(Message m : relevantMessages){
			if(m.getContent().length()>100){
				System.out.format(leftAlignFormat, m.getContent().substring(0, 100)+"...", m.getOwner().getName(), m.getDate(), m.getTime());
				continue;
			}
			System.out.format(leftAlignFormat, m.getContent(), m.getOwner().getName(), m.getDate(), m.getTime());
		}
		System.out.format(breakerFormat,"-"+messageDashes+"-","-"+nameDashes+"-","-----------","---------");
	}

	public static int findExpressionOccuranceForPerson(Person pers, String expression, boolean caseSensitiveS, boolean wholeWordS){
		int expresLength = expression.length();
		int count = 0;
		for(Message m : pers.getMessages()){
			String content = m.getContent();
			for(int i=0;i<=content.length()-expresLength;i++){
				if(caseSensitiveS ? (content.substring(i, expresLength+i).equals(expression)) : (content.substring(i, expresLength+i).equalsIgnoreCase(expression))){
					if(wholeWordS ? (isWholeWord(content, expresLength, i)) : (true)){
						count++;
					}
				}
			}
		}
		return count;
	}

	public static void findExpressionsForUser(String expression, boolean caseSensitiveS, boolean wholeWordS){
		ArrayList<String> persNames = new ArrayList<>();
		ArrayList<Integer> wordCounts = new ArrayList<>();
		ArrayList<Float> wordPercents = new ArrayList<>();
		String mostUsedP = "";
		int mostUsedN=0;
		String longestName = "-----";
		int totalCount=0;

		for(Person p : persons){
			int personCount = findExpressionOccuranceForPerson(p, expression, caseSensitiveS, wholeWordS);
			if(personCount!=0){
				totalCount+=personCount;
				persNames.add(p.getName());
				wordCounts.add(personCount);
				if(personCount>mostUsedN){
					mostUsedP=p.getName();
					mostUsedN=personCount;
				}
				if(p.getName().length()>longestName.length()){
					longestName=p.getName();
				}
			}
		}
		for(int i : wordCounts){
			float percent = (i*100f/totalCount);
			wordPercents.add(percent);
		}

		String leftAlignFormat = "| %-"+longestName.length()+"s | %-5s | %-7s | %n";
		String finalLineFormat = "| %-"+(longestName.length()+18)+"s | %n";
		String breakerFormat = "+%-"+(longestName.length()+2)+"s+%-7s+%-9s+%n";

		String nameDashes = longestName.replaceAll(".", "-");

		System.out.println("**Expression count: \"" + expression + "\"**");
		System.out.format(breakerFormat,"-"+nameDashes+"-","-------","---------");
		System.out.format(leftAlignFormat, "Name", "Count", "Percent");
		System.out.format(breakerFormat,"-"+nameDashes+"-","-------","---------");
		for(int i=0;i<persNames.size();i++) {
			System.out.format(leftAlignFormat, persNames.get(i), wordCounts.get(i).toString(), df.format(wordPercents.get(i))+"%");
		}
		System.out.format(breakerFormat,"-"+nameDashes+"-","-------","---------");
		System.out.format(leftAlignFormat, "Total", totalCount, "100%");
		System.out.format(breakerFormat,"-"+nameDashes+"-","-------","---------");
		System.out.format(finalLineFormat, "Most used by " + mostUsedP);
		System.out.format(breakerFormat,"-"+nameDashes+"-","-------","---------");
	}
	
	public static void mostMessages(ArrayList<Person> persons){
		int totMessages=0;
		String longestName = "------";
		for(Person p : persons){
			totMessages+=p.getMessages().size();
			if(p.getName().length()>longestName.length()){
				longestName=p.getName();
			}
		}
		
		String leftAlignFormat = "| %-"+longestName.length()+"s | %-6s | %-7s | %n";
		String breakerFormat = "+%-"+(longestName.length()+2)+"s+%-8s+%-9s+%n";
		
		String nameDashes = longestName.replaceAll(".", "-");
		
		System.out.println("**Message count**");
		System.out.format(breakerFormat,"-"+nameDashes+"-","-------","---------");
		System.out.format(leftAlignFormat, "Name", "Count", "Percent");
		System.out.format(breakerFormat,"-"+nameDashes+"-","-------","---------");
		for(Person p : persons){
			System.out.format(leftAlignFormat, p.getName(), p.getMessages().size(), df.format(p.getMessages().size()*100f/totMessages)+"%");
		}
		System.out.format(breakerFormat,"-"+nameDashes+"-","-------","---------");
		System.out.format(leftAlignFormat, "Total:", totMessages, "100%");
		System.out.format(breakerFormat,"-"+nameDashes+"-","-------","---------");
	}

	public static String getNameFromMessage(String line){
		String name = line.substring(20, (line.indexOf(": ")));
		return name;
	}

	public static String cleanLine(String line){
		line=line.replace("‪", "");
		line=line.replace("‬", "");
		return line;
	}

}
