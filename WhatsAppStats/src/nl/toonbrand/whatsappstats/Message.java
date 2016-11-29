package nl.toonbrand.whatsappstats;

public class Message {
	String content;
	String date;
	String time;
	Person owner;
	
	public Message(String content, String date, String time, Person owner) {
		super();
		this.content = content;
		this.date = date;
		this.time = time;
		this.owner = owner;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}
	
}
