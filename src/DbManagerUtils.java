import java.sql.ResultSet;
import java.sql.SQLException;

public class DbManagerUtils {

	// default path of the db
	protected static final String DRIVER_NAME = "jdbc:sqlite:sample.db";

	protected static final String SELECT_ALL_BOOKS = "select id, title, author, isbn, publishing_house from books";
	// we use the same order of the fields of above so we can reuse
	// getBookFromResult
	protected static final String SELECT_BOOK_WHERE_ID = "select id, title, author, isbn, publishing_house from books where id=?";
	// id is the primary key chosen by the db so we don't touch it.
	protected static final String UPDATE_BOOK_WHERE_ID = "update books set title=?, author=?, isbn=?, publishing_house=? where id=?";
	protected static final String CREATE_BOOK = "insert into books(title, author, isbn, publishing_house) values (?,?,?,?)";
	protected static final String DELETE_BOOK = "delete from books where id = ?";

	protected static final String GET_LAST_ID = "select last_insert_rowid()";

	protected static final String CREATE_TABLE = "create table books(id integer primary key autoincrement, "
			+ "title varchar(256)," + "author varchar(256)," + "isbn varchar(20) unique,"
			+ "publishing_house varchar(256)" + ")";

	protected static Book getBookFromResult(ResultSet rs) throws SQLException {

		Book book = new Book();

		// we access the attributes of the select
		// in the order in which they have been specified in the query
		// id has been specified as the first one, so we access it using the index 1
		// since id is an integer we use getInt
		int id = rs.getInt(1);
		// title has been specified as the second one, so we access it using the index 2
		// it is a string, so we call getString
		// and so on
		String title = rs.getString(2);
		String author = rs.getString(3);
		String isbn = rs.getString(4);
		String publishingHouse = rs.getString(5);

		// now we fill the Book object using setters
		book.setID(id);
		book.setAuthor(author);
		book.setTitle(title);
		book.setISBN(isbn);
		book.setPublishingHouse(publishingHouse);

		// and return the result
		return book;

	}

}
