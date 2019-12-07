import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class DbManager {

	private Connection connection;

	/**
	 * Connects to the SQLite database located at dbFilePath
	 * 
	 * @param dbFilePath path to the database file
	 * @throws SQLException if the connection fails
	 */
	public DbManager(String dbFilePath) throws SQLException {

		// Class.forName(driver_class_name) is no longer needed in newer version of Java
		this.connection = DriverManager.getConnection(dbFilePath);

	}

	public void close() throws SQLException {
		this.connection.close();
	}

	public List<Book> getBooks() throws SQLException {
		// we create a LinkedList holding the books
		List<Book> books = new LinkedList<Book>();

		// the first step to run a query is to create a Statement or PreparedStatement
		// object
		// - use Statement when the query has no parameters
		// - use PreparedStatement when the query has parameters obtained from the
		// outside,
		// it can prevent SQL Inject
		//
		// we don't need to use a PreparedStatement in this case since our query has no
		// parameters
		Statement stat = this.connection.createStatement();

		// next, we execute the statement obtaining a ResultSet
		ResultSet rs = stat.executeQuery(DbManagerUtils.SELECT_ALL_BOOKS);

		// next we have to get the results from the ResultSet
		// we use the utility method of DbManagerUtils
		// ResultSet can be seen as an iterator over A SET OF ROWS
		// therefore we must "iterate over it" by calling the "next()"
		// method.
		// It is necessary also to get a single row of data.
		// next() returns a boolean indicating whether there is a new row
		// of data or not, so we use it in a while.
		// We will stay in the loop WHILE RS.NEXT() RETURNS TRUE, so
		// while there is a new row of data, and we exit the loop when
		// there are no rows.
		while (rs.next()) {
			// then we call our utility method to effectively grab the object
			Book book = DbManagerUtils.getBookFromResult(rs);
			// and we append the book to our list of books
			books.add(book);
		}

		return books;

	}

	public void deleteBook(Book book) throws SQLException {
		// this query is parameterized by the book id, as such,
		// we must use PreparedStatement to avoid injections.

		// this first step is to create a PreparedStatement
		// the query string is delete from books where id = ?
		PreparedStatement pstmt = this.connection.prepareStatement(DbManagerUtils.DELETE_BOOK);

		// next, we fix the placeholder "?" with the actual value
		// we use method set<Type> where the first parameter is the index of the
		// placeholder we want to fix
		// in the query, in this case it is the first one so we use index "1".
		// The second parameter is the actual value.
		// Since we set an integer, we use setInt.
		pstmt.setInt(1, book.getID());

		// now we can safely execute our query
		// since the query execution won't return a ResultSet
		// we call "execute()" instead of "executeQuery()"
		pstmt.execute();
		// and we are done
	}

	public Book getBook(int id) throws SQLException {
		Book book = null;
		// this query is parameterized by the book id, as such,
		// we must use PreparedStatement to avoid injections.

		// this first step is to create a PreparedStatement
		// the query string is select id, title, author, isbn, publishing_house from
		// books where id=?
		PreparedStatement pstmt = this.connection.prepareStatement(DbManagerUtils.SELECT_BOOK_WHERE_ID);

		// fix the placeholder
		pstmt.setInt(1, id);

		// execute the query
		ResultSet rs = pstmt.executeQuery();

		// we need to call next() even if we need
		// just one row of data. In this case we may use it in a if.
		if (rs.next()) {
			book = DbManagerUtils.getBookFromResult(rs);
		}
		// if the book does not exist we don't fill the Book variable
		// and we will return NULL.
		return book;

	}

	public void updateBook(Book book) throws SQLException {
		// this query is parameterized by the book id, as such,
		// we must use PreparedStatement to avoid injections.
		PreparedStatement pstmt = this.connection.prepareStatement(DbManagerUtils.UPDATE_BOOK_WHERE_ID);

		// we fix the placeholder, the query is:
		// update books set title=?, author=?, isbn=?, publishing_house=? where id=?
		pstmt.setString(1, book.getTitle());
		pstmt.setString(2, book.getAuthor());
		pstmt.setString(3, book.getISBN());
		pstmt.setString(4, book.getPublishingHouse());

		pstmt.setInt(5, book.getID());

		// as for delete, we don't care about the ResultSet because it is
		// just an update query, so we use "execute()"
		pstmt.execute();
	}

	public Book createBook(Book book) throws SQLException {
		// "cloning" the input - sort of
		Book result = new Book(book);

		// we have to do two queries (see below) so we fire a transaction
		this.connection.setSavepoint();

		// this query is parameterized by the book id, as such,
		// we must use PreparedStatement to avoid injections.

		// THE CORRECT WAY TO RETRIEVE THE ID AFTER THE INSERTION (DOES NOT WORK WITH
		// SQLITE)
		// what is the second parameter? The issue is: how can we get the id of the book
		// after the insertion? Some databases, such as Postgres, can do something like:
		// "INSERT INTO ... RETURNIND <FIELD-NAME>". But SQLite cannot. Instead, by
		// using the second argument we tell the database to return the value of
		// auto-generated keys,
		// like the id in our case.
		// We will access it using usual ResultSet interface.
//		PreparedStatement pstmt = this.connection.prepareStatement(DbManagerUtils.CREATE_BOOK,
//				Statement.RETURN_GENERATED_KEYS);

		// since it does not work we will do two queries
		PreparedStatement pstmt = this.connection.prepareStatement(DbManagerUtils.CREATE_BOOK);

		// query is:
		// insert into books(title, author, isbn, publishing_house) values (?,?,?,?)
		pstmt.setString(1, book.getTitle());
		pstmt.setString(2, book.getAuthor());
		pstmt.setString(3, book.getISBN());
		pstmt.setString(4, book.getPublishingHouse());

		pstmt.execute();

		// now the second query to get the id
		Statement stat = this.connection.createStatement();

		// we need the ResultSet to access the id
		ResultSet rs = stat.executeQuery(DbManagerUtils.GET_LAST_ID);
		if (rs.next()) {
			int id = rs.getInt(1);
			result.setID(id);
		} else {
			// if we cannot access the row there is a problem
			// so we throw an exception
			// but before we rollback
			this.connection.rollback();
			throw new SQLException("cannot access the id");
		}

		return result;
	}

	protected void createTable() throws SQLException {
		Statement stat = this.connection.createStatement();
		stat.execute(DbManagerUtils.CREATE_TABLE);
	}

}
