import java.util.Scanner
import kotlin.system.exitProcess

// Book class represents a book in the library
data class Book(
    val title: String, 
    val author: String, 
    val isbn: String, 
    var available: Boolean = true
)

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

//Main Dashboard 
fun MainDashBoard(): Int {
    val scanner = Scanner(System.`in`)
    var choice : Int? = null
    while(true){

        println("+-------------------------------+")
        println("|   LIBRARY MANAGEMENT SYSTEM   |")
        println("+-------------------------------+")
        println("|  (1) Library                  |")
        println("|  (2) Reader of the Week       |")
        println("|  (3) Exit                     |")
        println("+-------------------------------+")
        print("Select(1-3): ")

        try
        {
            choice = scanner.nextInt()
            if(choice !in 1..3){
                println("Invalid choice. Please select a number between 1 and 3.")
            }
            else{
                break
            }
        }
        catch(e: Exception){
            println("Error: Invalid input. Please enter a valid number.")
        }
    }
    return choice
}



class LogInSignUp{
    var readerUsername : String? = null

    fun InitUser() {
        val scanner = Scanner(System.`in`)
        var choice : Int? = null

        
        while(true){
            println("+-------------------------------------------------+")
            println("|           LIBRARY MANAGEMENT SYSTEM             |")
            println("+-------------------------------------------------+")
            println("|           (1) Log In                            |")
            println("|           (2) Sign Up                           |")
            println("|           (3) Exit                              |")
            println("+-------------------------------------------------+")
            print("Select(1-3): ")
            try
            {
                choice = scanner.nextInt()
                if(choice !in 1..3){
                    println("Invalid choice. Please select a number between 1 and 3.")
                }
                else{
                    break
                }
            }
            catch(e: Exception){
                println("Error: Invalid input. Please enter a valid number.")
            }
        }
        
        when(choice){
            1 -> {
                println("Log In")
                val userType = readerOrLibrarian()
                if(userType == 1){
                    logInReader() 
                }
                else if(userType == 2){
                    logInLibrarian()
                }
                else println("You must specify what type of user you are")
            }
            2 -> {
                println("Sign Up")
                val userType = readerOrLibrarian()
                if(userType == 1){
                    signUpReader()
                    
                }
                else if(userType == 2){
                    signUpLibrarian()
                
                }
                else println("You must specify what type of user you are")
            }
            3 -> println("Exit...")
        }

    }

    fun readerOrLibrarian(): Int {
        val scanner = Scanner(System.`in`)
        var choice : Int? = null

        while(true){
            println("+-------------------------------------------------+")
            println("|                  TYPE OF USER                   |")
            println("+-------------------------------------------------+")
            println("|                 (1) Reader                      |")
            println("|                 (2) Librarian                   |")
            println("|                 (3) Back                        |")
            println("+-------------------------------------------------+")
            print("Select(1-3): ")
            try
            {
                choice = scanner.nextInt()
                if(choice !in 1..3) println("Invalid choice. Please select a number between 1 and 3.")
                else return choice
            }
            catch(e: Exception){
                println("Error: Invalid input. Please enter a valid number.")
            }
        }
    }

    fun logInReader(){
        println("Log In Reader")
        val scanner = Scanner(System.`in`)
        var username : String? = null
        var password : String? = null

        println("+-------------------------------------------------+")
        println("|                  Log In                         |")
        println("+-------------------------------------------------+")
        print("Username: ")
        username = scanner.next()
        print("Password: ")
        password = scanner.next()

        println(username + " " + password)

        val user = userReader()
        user.ReaderPage(username, password)
    }

    fun logInLibrarian(){
        println("Log In Librarian")
        val scanner = Scanner(System.`in`)
        var username : String? = null
        var password : String? = null

        println("+-------------------------------------------------+")
        println("|                  Log In                         |")
        println("+-------------------------------------------------+")
        print("Username: ")
        username = scanner.next()
        print("Password: ")
        password = scanner.next()

        println(username + " " + password)
    }

    fun signUpReader(){
        println("Sign Up Reader")
        val scanner = Scanner(System.`in`)
        var username : String? = null
        var password : String? = null

        println("+-------------------------------------------------+")
        println("|                  Sign Up                        |")
        println("+-------------------------------------------------+")
        println("| Note: Credentials are case sensitive so you must|")
        println("| remember yours.                                 |")
        println("+-------------------------------------------------+")
        print("Set Username: ")
        username = scanner.next()
        print("Set Password: ")
        password = scanner.next()

        println(username + " " + password)
    }

    fun signUpLibrarian(){
        println("Sign Up Librarian")
        println("Sign Up Reader")
        val scanner = Scanner(System.`in`)
        var username : String? = null
        var password : String? = null

        println("+-------------------------------------------------+")
        println("|                  Sign Up                        |")
        println("+-------------------------------------------------+")
        println("| Note: Credentials are case sensitive so you must|")
        println("| remember yours.                                 |")
        println("+-------------------------------------------------+")
        print("Set Username: ")
        username = scanner.next()
        print("Set Password: ")
        password = scanner.next()

        println(username + " " + password)
    }
}

// TODO
// This class need to be private
class userReader(){
    fun ReaderPage(username: String, pass: String){
        // var currentUser = username

        println("+---------------------------------------+")
        println("WELCOME " + username + "!")
        println("+---------------------------------------+")


        val scanner = Scanner(System.`in`)
        var choice : Int? = null

        while(true){
            println("+---------------------------------------+")
            println("|        LIBRARY MANAGEMENT SYSTEM      |")
            println("+---------------------------------------+")
            println("|  (1) View Library                     |")
            println("|  (2) Publish a Book                   |")
            println("|  (3) Borrow a Book                    |")
            println("|  (4) Return a Book                    |")
            println("|  (5) Rate a Book                      |")
            println("|  (6) View Borrowed Books              |")
            println("|  (7) View Reading History             |")
            println("|  (8) Add to Favorites                 |")
            println("| (9) Leave a Review                    |")
            println("| (10) Update Account Info              |")
            println("| (11) Log Out                          |")
            println("+---------------------------------------+")
            print("Select(1-11): ")

            try
            {
                choice = scanner.nextInt()
                if(choice !in 1..11){
                    println("Invalid choice. Please select a number between 1 and 11.")
                }
                else{
                    break
                }
            }
            catch(e: Exception){
                println("Error: Invalid input. Please enter a valid number.")
            }
        }
        
        // TODO
        when(choice){
            1 -> {
                println("Library")
            }
            2 -> {
                println("Publish")
            }
            3 -> {
                println("Borrow")
            }
            4 -> {
                println("Return")
            }
            5 -> {
                println("Rate")
            }
            6 -> {
                println("Exit")
                ExitPage()
            }
        }
    }

    fun publishBook(){

    }

    fun borrowBook(){

    }

    fun returnBook(){

    }

    fun rateBook(){
        
    }
}

// TO DO
// This class need to be private
class userLibrarian(){
    fun librarianControlPanel(){
        println("+-------------------------------------------+")
        println("|          LIBRARIAN CONTROL PANEL          |")
        println("+-------------------------------------------+")
        println("|  (1) Add a Book                           |")
        println("|  (2) Remove a Book                        |")
        println("|  (3) Update Book Info                     |")
        println("|  (4) View All Books                       |")
        println("|  (5) View Borrowed Books                  |")
        println("|  (6) View Overdue Books                   |")
        println("|  (7) Manage Reader Accounts               |")
        println("|  (8) View Ratings and Reviews             |")
        println("|  (9) Generate Library Reports             |")
        println("| (10) Add/Remove Librarian                 |")
        println("| (11) Log Out                              |")
        println("+-------------------------------------------+")
        print("Select: 1-11: ")
    }
}

fun ExitPage(){
    println("+---------------------------------------------------+")
    println("|   THANK YOU FOR USING LIBRARY MANAGEMENT SYSTEM!  |")
    println("+---------------------------------------------------+")
    println("|   DEVELOPERS:   IRWEN FRONDA                      |")
    println("|                 AXEL AARON ARCELETA               |")
    println("|                 JOHN CARLO TRAJICO                |")
    println("|                                                   |")
    println("|   Year&Section: BSCS-2A                           |")
    println("|                                                   |")
    println("|   Project Details are uploaded in README.md file  |")
    println("+---------------------------------------------------+")
    exitProcess(0)
}

fun main() {
    val user = LogInSignUp()
    user.InitUser()

    // println("Choice: " + MainDashBoard())


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
