package nl.toonbrand.whatsappstats;

import java.util.ArrayList;

public class Person {
	String name;
	ArrayList<Message> messages = new ArrayList<Message>();
	
	public Person(String name) {
		super();
		this.name=name;
	}

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}
	
	public void addMessage(Message message){
		this.messages.add(message);
	}

	public void setName(String naam){
		this.name = naam;
	}

	public String getName() {
		return name;
	}
}
