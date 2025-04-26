// Book class represents a book in the library
data class Book(val title: String, val author: String, val isbn: String, var available: Boolean = true)

// Member class represents a member of the library
open class Member(val name: String, val memberId: String) {
    private val borrowedBooks = mutableListOf<Book>()

    fun borrowBook(book: Book): Boolean {
        if (book.available) {
            book.available = false
            borrowedBooks.add(book)
            println("$name borrowed '${book.title}'")
            return true
        }
        println("Sorry, '${book.title}' is currently unavailable.")
        return false
    }

    fun returnBook(book: Book): Boolean {
        if (borrowedBooks.contains(book)) {
            book.available = true
            borrowedBooks.remove(book)
            println("$name returned '${book.title}'")
            return true
        }
        println("Error: You don't have '${book.title}' borrowed.")
        return false
    }

    fun viewBorrowedBooks() {
        if (borrowedBooks.isEmpty()) {
            println("$name has no borrowed books.")
        } else {
            println("$name's borrowed books:")
            borrowedBooks.forEach { println("- ${it.title} by ${it.author}") }
        }
    }
}

// Librarian class represents the librarian who manages the library
class Librarian(name: String, memberId: String) : Member(name, memberId) {

    fun addBook(library: Library, book: Book) {
        library.addBook(book)
        println("'${book.title}' by ${book.author} has been added to the library.")
    }

    fun removeBook(library: Library, book: Book) {
        library.removeBook(book)
        println("'${book.title}' by ${book.author} has been removed from the library.")
    }

    fun searchBook(library: Library, searchQuery: String): List<Book> {
        return library.searchBook(searchQuery)
    }
}

// Library class represents the collection of books and members
class Library {

    private val books = mutableListOf<Book>()
    private val members = mutableListOf<Member>()

    fun addBook(book: Book) {
        books.add(book)
    }

    fun removeBook(book: Book) {
        books.remove(book)
    }

    fun searchBook(query: String): List<Book> {
        val results = books.filter { it.title.contains(query, ignoreCase = true) ||
                                      it.author.contains(query, ignoreCase = true) ||
                                      it.isbn.contains(query, ignoreCase = true) }
        return if (results.isEmpty()) {
            println("No books found for '$query'")
            emptyList()
        } else {
            results.forEach { println("Found: ${it.title} by ${it.author}, ISBN: ${it.isbn}") }
            results
        }
    }

    fun addMember(member: Member) {
        members.add(member)
    }

    fun removeMember(member: Member) {
        members.remove(member)
    }
}

fun main() {
    // Create the library and librarian
    val library = Library()
    val librarian = Librarian("Alice", "L123")

    // Create some books
    val book1 = Book("Kotlin Programming", "John Doe", "12345")
    val book2 = Book("Advanced Kotlin", "Jane Smith", "67890")

    // Add books to the library
    librarian.addBook(library, book1)
    librarian.addBook(library, book2)

    // Create some members
    val member1 = Member("Bob", "M001")
    val member2 = Member("Charlie", "M002")

    // Add members to the library
    library.addMember(member1)
    library.addMember(member2)

    // Member borrows a book
    member1.borrowBook(book1)

    // Member tries to borrow the same book again (should show unavailable)
    member2.borrowBook(book1)

    // Member returns a book
    member1.returnBook(book1)

    // Search for books by title
    librarian.searchBook(library, "Kotlin")
}
