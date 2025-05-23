import java.util.Scanner
import kotlin.system.exitProcess
import java.io.File
import java.time.Instant

// Book class represents a book in the library
data class Book(
    val title: String, 
    val author: String, 
    val isbn: String, 
    var available: Boolean = true
)

//Users  are the readers
data class Users(
    val username: String,
    val pass: String,
    var numberOfBooksRead: Int
)

//Librarians
data class Librarians(
    val username: String,
    val pass: String
)


// Library class represents the collection of books and members
class Library {

    private val books = loadBooksFromCSV("books.csv").toMutableList()

    fun searchBook(query: String): List<Book> {
        val results = books.filter { it.title.equals(query, ignoreCase = true) ||
                                      it.author.equals(query, ignoreCase = true) ||
                                      it.isbn.equals(query, ignoreCase = true) }
        return if (results.isEmpty()) {
            println("No books found for '$query'")
            emptyList()
        } else {
            results.forEach { println("Found: ${it.title} by ${it.author}, ISBN: ${it.isbn}") }
            results
        }
    }

    
    //Main Dashboard 
    fun MainDashBoard() {
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
        when(choice){
            1 -> {
                LibraryMethods()
            }
        }
    }

    fun LibraryMethods(){
        val scanner = Scanner(System.`in`)
        var choice : Int? = null
        while(true){

            println("+-------------------------------+")
            println("|   LIBRARY MANAGEMENT SYSTEM   |")
            println("+-------------------------------+")
            println("|  (1) Display all books        |")
            println("|  (2) Read a book              |")
            println("|  (3) Search a book            |")
            println("|  (4) Back                     |")
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

        when(choice){
            1 -> {
                displayBooks()
            }
            //TODO
            2 -> {
                
            }
            3 ->{
                // var bookName : String 
                print("Enter book title: ")
                var bookName = scanner.next()
                println("Searching...")
                searchBook(bookName)
            }
        }
    }

    //TODO
    fun readerOfTheWeek(){

    }

    fun displayBooks() {
        println("+----------------------------------------------------------------+")
        println("|              LIBRARY MANAGEMENT SYSTEM                         |")
        println("+----------------------------------------------------------------+")
        println("| Title                  | Author         | ISBN     | Available |")
        println("+----------------------------------------------------------------+")
        for (book in books) {
            println(
                "| ${book.title.padEnd(22)} | ${book.author.padEnd(14)} | ${book.isbn.padEnd(8)} | ${book.available.toString().padEnd(9)} |"
            )
        }
        println("+----------------------------------------------------------------+")
        println("Press Enter to continue...")
        readLine()
        return
    }
    
    fun loadBooksFromCSV(filePath: String): List<Book> {
        val books = mutableListOf<Book>()
        try {
            File(filePath).useLines { lines ->
                lines.drop(1).forEach { line -> // Skip header
                    val parts = line.split(",")
                    if (parts.size >= 4) {
                        val title = parts[0]
                        val author = parts[1]
                        val isbn = parts[2]
                        val available = parts[3].toBooleanStrictOrNull() ?: true
                        books.add(Book(title, author, isbn, available))
                    }
                }
            }
        } catch (e: Exception) {
            println("Error reading CSV: ${e.message}")
        }
        return books
    }
}



class LogInSignUp{
    var retrievedUsers = retrieveReaders().toMutableList()
    var retrievedLibrariansUsers = retrievedLibrarians().toMutableList()

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
        println("+-------------------------------------------------+")
        println("|                  Log In                         |")
        println("+-------------------------------------------------+")
        print("Username: ")
        var username = scanner.nextLine()
        print("Password: ")
        var password = scanner.nextLine()

        println(username + " " + password)
        if (isLogInReader(username, password) == true){
            var user = userReader(username, password)
            user.readerPage()
        }
        else{
            println("No existing account...")
            println("Sign up first..")
            return
        } 
    }


    fun logInLibrarian(){
        println("Log In Librarian")
        val scanner = Scanner(System.`in`)
        println("+-------------------------------------------------+")
        println("|                  Log In                         |")
        println("+-------------------------------------------------+")
        print("Username: ")
        var username = scanner.nextLine()
        print("Password: ")
        var password = scanner.nextLine()

        println(username + " " + password)
        if (isLogInLibrarian(username, password) == true){
            var user = userLibrarian(username, password)
            user.librarianPage()
        }
        else{
            println("No existing account...")
            println("Sign up first..")
            return
        }
    }

    fun signUpReader(){
        println("Sign Up Reader")
        val scanner = Scanner(System.`in`)  

        println("+-------------------------------------------------+")
        println("|                  Sign Up                        |")
        println("+-------------------------------------------------+")
        println("| Note: Credentials are case sensitive so you must|")
        println("| remember yours.                                 |")
        println("+-------------------------------------------------+")
        print("Set Username: ")
        var username = scanner.nextLine()

        print("Set Password: ")
        var password = scanner.nextLine()

        if (isLogInReader(username, password)) {
            println("Username already exists! Try a different one.")
            return
        }   

        saveSignUpReaderAcc(username, password)
        println("Account created successfully for $username.")

        var user = userReader(username, password)
        user.readerPage()
    }

    fun signUpLibrarian(){
        println("Sign Up Librarian")
        val scanner = Scanner(System.`in`)

        println("+-------------------------------------------------+")
        println("|                  Sign Up                        |")
        println("+-------------------------------------------------+")
        println("| Note: Credentials are case sensitive so you must|")
        println("| remember yours.                                 |")
        println("+-------------------------------------------------+")
        print("Set Username: ")
        var username = scanner.nextLine()

        print("Set Password: ")
        var password = scanner.nextLine()

        saveSignUpLibrarianAcc(username, password)
        println("Account created successfully for $username.")

        var user = userLibrarian(username, password)
        user.librarianPage()
    }

    fun retrieveReaders(): List<Users>{
        val users = mutableListOf<Users>()
        try {
            File("user.csv").useLines { lines ->
                lines.drop(1).forEach { line -> // Skip header
                    val parts = line.split(",")
                    if (parts.size >= 3) {
                        val username = parts[0]
                        val pass = parts[1]
                        val numberOfBooksRead = parts[2].toIntOrNull() ?: 0 
                        users.add(Users(username, pass, numberOfBooksRead))
                    }
                }
            }
        } catch (e: Exception) {
            println("Error reading CSV: ${e.message}")
        }
        return users
    }

    fun retrievedLibrarians(): List<Librarians>{
        val users = mutableListOf<Librarians>()
        try {
            File("librarian.csv").useLines { lines ->
                lines.drop(1).forEach { line -> // Skip header
                    val parts = line.split(",")
                    if (parts.size >= 2) {
                        val username = parts[0]
                        val pass = parts[1]
                        users.add(Librarians(username, pass))
                    }
                }
            }
        } catch (e: Exception) {
            println("Error reading CSV: ${e.message}")
        }
        return users
    }

    fun isLogInReader(username : String, password : String): Boolean {
        return retrievedUsers.any { it.username == username && it.pass == password }
    }

    fun isLogInLibrarian(username : String, password : String): Boolean {
        return retrievedLibrariansUsers.any { it.username == username && it.pass == password }
    }

    fun saveSignUpReaderAcc(username: String, password: String) {
        val file = File("user.csv")
        val newLine = "$username,$password,0" // numberOfBooksRead starts at 0

        try {
            // If the file doesn't exist, create it and write the header first
            if (!file.exists()) {
                file.writeText("readerName,password,numberOfBooksRead\n")
            }

            // Ensure the file ends with a newline before appending
            if (!file.readText().endsWith("\n")) {
                file.appendText("\n")
            }   

            // Append the new user line
            file.appendText("$newLine\n")
            println("User account for '$username' created successfully.")
        } catch (e: Exception) {
            println("Error saving account: ${e.message}")
        }
        return
    }

    fun saveSignUpLibrarianAcc(username: String, password: String) {
        val file = File("librarian.csv")
        val newLine = "$username,$password" 

        try {
            // If the file doesn't exist, create it and write the header first
            if (!file.exists()) {
                file.writeText("readerName,password\n")
            }

            // Ensure the file ends with a newline before appending
            if (!file.readText().endsWith("\n")) {
                file.appendText("\n")
            }   

            // Append the new user line
            file.appendText("$newLine\n")
            println("User account for '$username' created successfully.")
        } catch (e: Exception) {
            println("Error saving account: ${e.message}")
        }
    }

}

// TODO
// This class need to be private
// You need to have save function to update the user.csv
class userReader(var username: String, var password: String){

    init{
        println("+---------------------------------------+")
        println("|       LIBRARY MANAGEMENT SYSTEM       |")
        println("+---------------------------------------+")
        println("Initialized...")
    }

    fun readerPage(){
        println("+---------------------------------------+")
        println("WELCOME  $username!")
        println("+---------------------------------------+")
        
        val scanner = Scanner(System.`in`)
        var choice : Int? = null

        while (true) {
            println("+---------------------------------------+")
            println("|       LIBRARY MANAGEMENT SYSTEM       |")
            println("+---------------------------------------+")
            println("|  (1) View Library                     |")
            println("|  (2) Publish a Book                   |")
            println("|  (3) Borrow a Book                    |")
            println("|  (4) Return a Book                    |")
            println("|  (5) Rate a Book                      |")
            println("|  (6) View Borrowed Books              |")
            println("|  (7) View Reading History             |")
            println("|  (8) Add to Favorites                 |")
            println("|  (9) Leave a Review                   |")
            println("| (10) Manage your Account              |")
            println("| (11) Log Out                          |")
            println("+---------------------------------------+")
            print("Select(1-11): ")
            
            try {
                choice = scanner.nextInt()
                if(choice !in 1..11){
                    println("Invalid choice. Please select a number between 1 and 11.")
                }
            } catch(e: Exception){
                println("Error: Invalid input. Please enter a valid number.")
            }
            
            when(choice) {
                1 -> {
                    println("Library")
                    val library = Library()
                    library.MainDashBoard()
                }
                2 -> publishBook()
                3 -> borrowBook()
                4 -> returnBook()
                5 -> rateBook()
                6 -> viewBorrowedBooks()
                7 -> viewReadingHistory()
                8 -> addToFavorites()
                9 -> leaveReview()
                10 -> {
                    val deleted = manageAccount(scanner)
                    if (deleted) {
                        println("Returning to main menu after account deletion...")
                        break
                    }
                }
                11 -> break
            }
        }
    }

    //TODO
    //Helper Functions
    private fun changeUsername(scanner: Scanner) {
        print("Enter new username: ")
        val newUsername = scanner.nextLine().trim()

        if (newUsername.isEmpty()) {
            println("Username cannot be empty.")
            return
        }

        val file = File("user.csv")
        val lines = file.readLines().toMutableList()
        val header = lines.firstOrNull() ?: return

        if (lines.drop(1).any { it.split(",")[0] == newUsername }) {
            println("Username \"$newUsername\" is already taken.")
            return
        }

        for (i in 1 until lines.size) {
            val parts = lines[i].split(",").toMutableList()
            if (parts[0] == username) {
                parts[0] = newUsername
                lines[i] = parts.joinToString(",")
                break
            }
        }
        file.writeText(lines.joinToString("\n") + "\n")

        updateUsernameInFile("library-data/borrowed_books.csv", newUsername)
        updateUsernameInFile("library-data/reading_history.csv", newUsername)
        updateUsernameInFile("library-data/ratings.csv", newUsername)
        updateUsernameInFile("library-data/favorites.csv", newUsername)
        updateUsernameInFile("library-data/reviews.csv", newUsername)

        println("Username changed from \"$username\" to \"$newUsername\" successfully.")
        username = newUsername
    }

    private fun updateUsernameInFile(filePath: String, newUsername: String) {
        val file = File(filePath)
        if (!file.exists()) return

        val lines = file.readLines().toMutableList()
        if (lines.isEmpty()) return

        val header = lines[0]
        val updatedLines = mutableListOf(header)

        for (i in 1 until lines.size) {
            val parts = lines[i].split(",").toMutableList()
            if (parts[0] == username) {
                parts[0] = newUsername
            }
            updatedLines.add(parts.joinToString(","))
        }

        file.writeText(updatedLines.joinToString("\n") + "\n")
    }

    private fun changePassword(scanner: Scanner) {
        print("Enter your current password: ")
        val currentPass = scanner.nextLine()

        val file = File("user.csv")
        val lines = file.readLines().toMutableList()
        val header = lines.firstOrNull() ?: return

        val userIndex = lines.indexOfFirst {
            it.split(",")[0] == username && it.split(",")[1] == currentPass
        }

        if (userIndex == -1) {
            println("Incorrect current password.")
            return
        }

        print("Enter new password: ")
        val newPass1 = scanner.nextLine()

        print("Confirm new password: ")
        val newPass2 = scanner.nextLine()

        if (newPass1 != newPass2) {
            println("Passwords do not match.")
            return
        }

        val parts = lines[userIndex].split(",").toMutableList()
        parts[1] = newPass1
        lines[userIndex] = parts.joinToString(",")
        file.writeText(lines.joinToString("\n") + "\n")

        println("Password changed successfully.")
    }

    private fun confirmDelete(scanner: Scanner): Boolean {
        println("WARNING: This will permanently delete your account and all associated data.")
        print("Type 'DELETE' to confirm or anything else to cancel: ")
        val confirmation = scanner.nextLine()
        return confirmation.equals("DELETE", ignoreCase = true)
    }

    private fun deleteAccount() {
        fun deleteFromFile(filePath: String) {
            val file = File(filePath)
            if (!file.exists()) return

            val lines = file.readLines()
            val filtered = lines.filterIndexed { i, line ->
                if (i == 0) true else !line.startsWith("$username,")
            }
            file.writeText(filtered.joinToString("\n") + "\n")
        }

        val userFile = File("user.csv")
        if (userFile.exists()) {
            val lines = userFile.readLines()
            val filtered = lines.filterIndexed { i, line ->
                if (i == 0) true else !line.split(",")[0].equals(username, ignoreCase = true)
            }
            userFile.writeText(filtered.joinToString("\n") + "\n")
        }

        deleteFromFile("library-data/borrowed_books.csv")
        deleteFromFile("library-data/reading_history.csv")
        deleteFromFile("library-data/ratings.csv")
        deleteFromFile("library-data/favorites.csv")
        deleteFromFile("library-data/reviews.csv")
    }

    private fun updateBooksReadInCSV(newCount: Int) {
        val file = File("user.csv")
        val lines = file.readLines().toMutableList()
        for (i in 1 until lines.size) {
            val parts = lines[i].split(",")
            if (parts.size >= 3 && parts[0] == username) {
                lines[i] = "${parts[0]},${parts[1]},$newCount"
                break
            }
        }
        file.writeText(lines.joinToString("\n") + "\n")
    }

    private fun getBooksRead(): Int {
        try {
            File("user.csv").useLines { lines ->
                lines.drop(1).forEach { line ->
                    val parts = line.split(",")
                    if (parts.size >= 3 && parts[0] == username) {
                        return parts[2].toIntOrNull() ?: 0
                    }
                }
            }
        } catch (_: Exception) {}
        return 0
    }

    private fun getBorrowedBooks(): List<Book> {
        val books = mutableListOf<Book>()
        val file = File("library-data/borrowed_books.csv")

        if (!file.exists()) return books

        file.useLines { lines ->
            val allLines = lines.toList()
            allLines.drop(1).forEach { line ->
                val parts = line.trim().split(",")
                if (parts.size >= 5) {
                    val fileUsername = parts[0].trim()
                    val currentUsername = username.trim()

                    if (fileUsername == currentUsername) {
                        val title = parts[1].trim()
                        val author = parts[2].trim()
                        val isbn = parts[3].trim()
                        val dateBorrowed = parts[4].trim()

                        books.add(Book(title, author, isbn))
                    }
                }
            }
        }

        return books
    }

    private fun getReadingHistory(): List<Book> {
        val books = mutableListOf<Book>()
        val file = File("library-data/reading_history.csv")
        if (!file.exists()) return books

        file.useLines { lines ->
            lines.drop(1).forEach { line ->
                val parts = line.split(",")
                if (parts.size >= 4) {
                    val fileUsername = parts[0].trim()
                    val currentUsername = username.trim()

                    if (fileUsername.equals(currentUsername, ignoreCase = true)) {
                        val title = parts[1].trim()
                        val author = parts[2].trim()
                        val isbn = parts[3].trim()
                        books.add(Book(title, author, isbn))
                    }
                }
            }
        }

        return books
    }

    
    // MAIN FUNCTIONS
    fun publishBook(){
        val scanner = Scanner(System.`in`)
        var bookName: String = ""
        var isbn: String = ""
        println("+-------------------------------------------+")
        println("|           PUBLISH CONTROL PANEL           |")
        println("+-------------------------------------------+")
        println("| Publish a Book                            |")
        println("+-------------------------------------------+")
        println()

        print("Enter a book name: ")
        bookName = scanner.nextLine()
        while(true){
            try{
                print("Enter isbn number (5-digit): ")
                isbn = scanner.next()
                if(isbn.length != 5){
                    println("Invalid isbn...")
                }
                else {break}
            }
            catch (e: Exception){
                println("Invalid Input...")
            }
        }
        println("Your book \"$bookName\" has been successfully added to the book approval list of librarians.")
    }

    fun borrowBook() {
        val timestamp = Instant.now().toString()

        val library = Library()
        library.displayBooks()
        val scanner = Scanner(System.`in`)
        print("Enter the ISBN of the book you want to borrow: ")
        val isbn = scanner.nextLine()
        val book = library.searchBook(isbn).firstOrNull()
        if (book != null && book.available) {
            File("library-data/borrowed_books.csv").appendText("$username,${book.title},${book.author},${book.isbn},$timestamp\n")
            File("library-data/reading_history.csv").appendText("$username,${book.title},${book.author},${book.isbn},$timestamp\n")
            val newCount = getBooksRead() + 1
            updateBooksReadInCSV(newCount)
            println("You have borrowed \"${book.title}\".")
        } else {
            println("Book not available or not found.")
        }
    }

    fun returnBook() {
        println("+-------------------------------------------+")
        println("|               RETURN A BOOK               |")
        println("+-------------------------------------------+")

        val borrowedBooks = getBorrowedBooks()
        if (borrowedBooks.isEmpty()) {
            println("You have no borrowed books.")
            return
        }
        println("Your borrowed books:")
        borrowedBooks.forEachIndexed { idx, book ->
            println("${idx + 1}. ${book.title} by ${book.author} (ISBN: ${book.isbn})")
        }
        val scanner = Scanner(System.`in`)
        print("Enter the number of the book to return: ")
        val idx = scanner.nextInt() - 1
        scanner.nextLine()
        if (idx in borrowedBooks.indices) {
            val book = borrowedBooks[idx]
            val file = File("library-data/borrowed_books.csv")
            val lines = file.readLines().toMutableList()
            val filtered = lines.filterIndexed { i, line ->
                if (i == 0) true else {
                    val parts = line.split(",")
                    !(parts[0] == username && parts[3] == book.isbn)
                }
            }
            file.writeText(filtered.joinToString("\n") + "\n")
            println("You have returned \"${book.title}\".")
        } else {
            println("Invalid selection.")
        }
    }

    fun rateBook() {
        println("+-------------------------------------------+")
        println("|                RATE A BOOK                |")
        println("+-------------------------------------------+")

        val borrowedBooks = getBorrowedBooks()
        if (borrowedBooks.isEmpty()) {
            println("You haven't borrowed any book yet.")
            return
        }

        println("+----------------------------------------------------------------+")
        println("| Title                  | Author         | ISBN                 |")
        println("+----------------------------------------------------------------+")
        borrowedBooks.forEachIndexed { idx, book ->
            println("| ${book.title.padEnd(22)} | ${book.author.padEnd(13)} | ${book.isbn.padEnd(8)} |")
        }
        println("+----------------------------------------------------------------+")
        val scanner = Scanner(System.`in`)
        print("Enter the ISBN of the book you want to rate: ")
        val isbn = scanner.nextLine()
        if (borrowedBooks.none { it.isbn == isbn }) {
            println("You haven't borrowed a book with ISBN $isbn.")
            return
        }
        print("Enter your rating (1-5): ")
        val rating = scanner.nextLine()
        File("library-data/ratings.csv").appendText("$username,$isbn,$rating\n")
        println("Thank you for rating book $isbn with $rating stars!")
    }

    fun viewBorrowedBooks() {
        println("+-------------------------------------------+")
        println("|            YOUR BORROWED BOOKS            |")
        println("+-------------------------------------------+")

        val borrowedBooks = getBorrowedBooks()
        if (borrowedBooks.isEmpty()) {
            println("You have no borrowed books.")
        } else {
            borrowedBooks.forEach { book ->
                println("${book.title} by ${book.author} (ISBN: ${book.isbn})")
            }
        }
        println("Press Enter to continue...")
        readLine()
    }

    fun viewReadingHistory() {
        println("+-------------------------------------------+")
        println("|            YOUR READING HISTORY           |")
        println("+-------------------------------------------+")

        val history = getReadingHistory()
        if (history.isEmpty()) {
            println("No reading history yet.")
        } else {
            history.forEach { book ->
                println("${book.title} by ${book.author} (ISBN: ${book.isbn})")
            }
        }
        println("Press Enter to continue...")
        readLine()
    }

    fun addToFavorites() {
        println("+-------------------------------------------+")
        println("|            ADD TO FAVORITES               |")
        println("+-------------------------------------------+")

        val borrowedBooks = getBorrowedBooks()
        if (borrowedBooks.isEmpty()) {
            println("You haven't borrowed any book yet.")
            return
        }

        println("+----------------------------------------------------------------+")
        println("| Title                  | Author         | ISBN                 |")
        println("+----------------------------------------------------------------+")
        borrowedBooks.forEach { book ->
            println("| ${book.title.padEnd(22)} | ${book.author.padEnd(14)} | ${book.isbn.padEnd(8)} |")
        }
        println("+----------------------------------------------------------------+")

        val scanner = Scanner(System.`in`)
        print("Enter the ISBN of the book to add to favorites: ")
        val isbn = scanner.nextLine()
        if (borrowedBooks.none { it.isbn == isbn }) {
            println("You haven't borrowed a book with ISBN $isbn.")
            return
        }
        val book = borrowedBooks.first { it.isbn == isbn }
        File("library-data/favorites.csv").appendText("$username,${book.title},${book.author},${book.isbn}\n")
        println("\"${book.title}\" added to favorites.")
    }

    fun leaveReview() {
        println("+-------------------------------------------+")
        println("|               LEAVE A REVIEW              |")
        println("+-------------------------------------------+")
        
        val borrowedBooks = getBorrowedBooks()
        if (borrowedBooks.isEmpty()) {
            println("You haven't borrowed any book yet.")
            return
        }

        println("+----------------------------------------------------------------+")
        println("| Title                  | Author         | ISBN                 |")
        println("+----------------------------------------------------------------+")
        borrowedBooks.forEach { book ->
            println("| ${book.title.padEnd(22)} | ${book.author.padEnd(14)} | ${book.isbn.padEnd(8)} |")
        }
        println("+----------------------------------------------------------------+")

        val scanner = Scanner(System.`in`)
        print("Enter the ISBN of the book to review: ")
        val isbn = scanner.nextLine()
        if (borrowedBooks.none { it.isbn == isbn }) {
            println("You haven't borrowed a book with ISBN $isbn.")
            return
        }
        print("Enter your review: ")
        val review = scanner.nextLine()
        File("library-data/reviews.csv").appendText("$username,$isbn,$review\n")
        println("Thank you for creating a review for book $isbn!")
    }

    fun manageAccount(scanner: Scanner): Boolean { 
        while (true) {
            println("+-------------------------------------------+")
            println("|              MANAGE ACCOUNT               |")
            println("+-------------------------------------------+")
            println("| 1. Change Username                        |")
            println("| 2. Change Password                        |")
            println("| 3. Delete Account                         |")
            println("| 4. Back to Main Menu                      |")
            println("+-------------------------------------------+")
            print("Enter your choice: ")

            when (scanner.nextLine().trim()) {
                "1" -> changeUsername(scanner)
                "2" -> changePassword(scanner)
                "3" -> {
                    if (confirmDelete(scanner)) {
                        deleteAccount()
                        println("Account deleted. Exiting...")
                        return true
                    }
                }
                "4" -> return false
                else -> println("Invalid choice. Please try again.")
            }
        }
    }
}

// TODO
// You need to have save function to update the librarian.csv
class userLibrarian(var username: String, var pass: String){
    fun librarianPage(){
        println("+---------------------------------------+")
        println("WELCOME  $username!")
        println("+---------------------------------------+")

        val scanner = Scanner(System.`in`)
        var choice : Int? = null

        while(true){
            println("+-------------------------------------------+")
            println("|          LIBRARIAN CONTROL PANEL          |")
            println("+-------------------------------------------+")
            println("|  (1) Add a Book                           |")
            println("|  (2) Remove a Book                        |")
            println("|  (3) Update Book Info                     |")
            println("|  (4) View All Books                       |")
            println("|  (5) View Borrowed Books                  |")
            println("|  (6) View Overdue Books                   |")
            println("|  (7) Manage Your Account                  |")
            println("|  (8) View Ratings and Reviews             |")
            println("|  (9) Generate Library Reports             |")
            println("| (10) Log Out                              |")
            println("+-------------------------------------------+")
            print("Select: 1-10: ")

            try
            {
                choice = scanner.nextInt()
                if(choice !in 1..11){
                    println("Invalid choice. Please select a number between 3 and 10.")
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
                //TODO
            }
            2 -> {
                //TODO
            }
            3 -> {
                //TODO
            }
            4 -> {
                //TODO
            }
            5 -> {
                //TODO
            }
            6 -> {
                //TODO
            }
            7 -> {
                //TODO
            }
            8 -> {
                //TODO
            }
            9 -> {
                //TODO
            }
            10 -> {
                //TODO
            }

        }
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
}
