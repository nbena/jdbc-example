import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class DbManagerTest {

	// in-memory database just for testing
	private static final String DB_PATH = "jdbc:sqlite::memory:";

	private static Book commedia = new Book("La Divina Commedia", "Dante Alighieri", "123-456",
			"Alighieri Publicantions");
	private static Book sposi = new Book("I Promessi Sposi", "Alessandro Manzoni", "678-910", "Manzoni & co");

	@Test
	void testDB() {
		try {

			// first we have to create the database
			DbManager manager = new DbManager(DB_PATH);
			manager.createTable();

			// at first, the db is empty
			assertTrue(manager.getBooks().size() == 0);

			// first we insert the two books
			Book commediaInserted = manager.createBook(commedia);
			Book sposiInserted = manager.createBook(sposi);

			// making sure they have been inserted
			assertTrue(manager.getBooks().size() == 2);

			// now we search the two books with their id
			// making sure it is not null
			assertNotNull(manager.getBook(commediaInserted.getID()));
			assertNotNull(manager.getBook(sposiInserted.getID()));

			// we make an update
			commediaInserted.setTitle("La Divinissima Commedia");
			manager.updateBook(commediaInserted);

			// we retrieve the book making sure it has been modified
			assertTrue(manager.getBook(commediaInserted.getID()).getTitle().equals("La Divinissima Commedia"));

			// we delete it...
			manager.deleteBook(commediaInserted);

			// we try to retrieve it, and we will see that it is null
			assertNull(manager.getBook(commediaInserted.getID()));

			// also, we make sure our list now it is 1 item long
			assertTrue(manager.getBooks().size() == 1);

		} catch (SQLException e) {
			e.printStackTrace();
			fail("exceptions!");
		}
	}

}
