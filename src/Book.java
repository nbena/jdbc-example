
public class Book {

	private int id;
	private String title;
	private String author;
	private String ISBN;
	private String publishingHouse;

	public Book() {
	}

	public Book(String title, String author, String iSBN, String publishingHouse) {
		this.title = title;
		this.author = author;
		ISBN = iSBN;
		this.publishingHouse = publishingHouse;
	}

	public Book(int id) {
		this.id = id;
	}

	public Book(int id, String title, String author, String iSBN, String publishingHouse) {
		this.id = id;
		this.title = title;
		this.author = author;
		ISBN = iSBN;
		this.publishingHouse = publishingHouse;
	}

	public Book(Book other) {
		this.id = other.getID();
		this.title = other.getTitle();
		this.author = other.getAuthor();
		this.ISBN = other.getISBN();
		this.publishingHouse = other.getPublishingHouse();
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

	public String getPublishingHouse() {
		return publishingHouse;
	}

	public void setPublishingHouse(String publishingHouse) {
		this.publishingHouse = publishingHouse;
	}

}
