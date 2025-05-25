import java.util.Scanner
import kotlin.system.exitProcess
import java.io.File
import java.time.Instant

// Import all members from AppState enum class
import AppState.*
// Import all members from UserLoginResult sealed class
import UserLoginResult.*

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

enum class AppState {
    LOGIN_SIGNUP,
    READER_DASHBOARD,
    LIBRARIAN_DASHBOARD,
    EXIT
}

// Sealed class to represent the result of a login/signup attempt
sealed class UserLoginResult {
    object StayOnLogin : UserLoginResult() // Failed login/signup, stay on login screen
    object ExitApp : UserLoginResult()     // User chose to exit from the initial menu
    data class ReaderLoggedIn(val user: Users) : UserLoginResult()
    data class LibrarianLoggedIn(val librarian: Librarians) : UserLoginResult()
}


// Library class represents the collection of books and members
class Library {
    val userManager = LogInSignUp()
    var currentUsers = userManager.retrieveReaders().toMutableList()
    private val books = loadBooksFromCSV("books.csv").toMutableList()
    private val scanner = Scanner(System.`in`)

    fun readBook(query: String, username: String): Boolean {
        val searchResults = books.filter { it.title.equals(query, ignoreCase = true) ||
                                      it.author.equals(query, ignoreCase = true) ||
                                      it.isbn.equals(query, ignoreCase = true) }
        
        if (searchResults.isEmpty()) {
            println("No books found for '$query'")
            return false // Indicate failure
        }

        println("\n--- Search Results ---")
        searchResults.forEachIndexed { index, book ->
            println("${index + 1}. Title: ${book.title}, Author: ${book.author}, ISBN: ${book.isbn}, Available: ${book.available}")
        }
        println("----------------------\n")
        
         // Prompt user to select a book if multiple results
        val selectedBook: Book? = if (searchResults.size == 1) {
            searchResults.first()
        } else {
            println("Enter the number of the book you want to read (or 0 to cancel):")
            val choice = scanner.nextLine().toIntOrNull()
            if (choice != null && choice > 0 && choice <= searchResults.size) {
                searchResults[choice - 1]
            } else {
                println("Invalid selection or cancelled.")
                null
            }
        }

        if (selectedBook == null) {
            return false // No book selected or invalid choice
        }

        if (!selectedBook.available) {
            println("Sorry, '${selectedBook.title}' is currently not available.")
            return false
        }


        val currentUser = currentUsers.find { it.username.equals(username, ignoreCase = true) }
        if (currentUser == null) {
            println("Error: User '$username' not found in system.")
            return false // User not found
        }


        println("+-------------------------------+")
        println("|           READING...          |") 
        println("+-------------------------------+")
        
        println("Congratulations! You have just finished reading '${selectedBook.title}'")
        currentUser.numberOfBooksRead++
         // Save the updated user data back to the CSV
        return saveReadChanges(currentUsers) // Delegate saving to a dedicated function       
    }

    
    //Main Dashboard 
    fun MainDashBoard(username: String): AppState {
        val scanner = Scanner(System.`in`)
        var choice : Int? = null

        while(true){
            println("+-------------------------------+")
            println("|   LIBRARY MAIN DASHBOARD      |") 
            println("+-------------------------------+")
            println("|   (1) Browse Books            |")
            println("|   (2) Reader of the Week      |")
            println("|   (3) Back to Reader Menu     |") 
            println("+-------------------------------+")
            print("Select(1-3): ")

            try
            {
                choice = scanner.nextInt()
                if(choice !in 1..3){
                    println("Invalid choice. Please select a number between 1 and 3.")
                    scanner.nextLine()
                }
                else{
                    break
                }
            }
            catch(e: Exception){
                println("Error: Invalid input. Please enter a valid number.")
                scanner.nextLine()
            }
        }
        return when (choice) {
            1 -> LibraryMethods(username) // This now needs to return an AppState
            2 -> {
                readerOfTheWeek()
                READER_DASHBOARD // After showing reader of the week, return to the reader dashboard
            }
            3 -> READER_DASHBOARD // Return to the reader's main menu
            else -> READER_DASHBOARD // Fallback (shouldn't be hit due to input validation)
        }
    }

    fun LibraryMethods(username: String): AppState {
        var choice : Int? = null
        while(true){
            println("+-------------------------------+")
            println("|   LIBRARY MANAGEMENT SYSTEM   |")
            println("+-------------------------------+")
            println("|  (1) Display all books        |")
            println("|  (2) Read a book              |")
            println("|  (3) Back                     |")
            println("+-------------------------------+")
            print("Select(1-3): ")

            try
            {
                choice = scanner.nextInt()
                scanner.nextLine()
                if(choice !in 1..3){
                    println("Invalid choice. Please select a number between 1 and 4.")
                }
                else{
                    break
                }
            }
            catch(e: Exception){
                println("Error: Invalid input. Please enter a valid number.")
                scanner.nextLine()
            }
        }

        when(choice){
            1 -> {
                displayBooks()
                return LibraryMethods(username) // After displaying, stay in LibraryMethods menu
            }
            2 -> { 
                println("+-------------------------------+")
                println("|   LIBRARY MANAGEMENT SYSTEM   |")
                println("+-------------------------------+")
                print("Enter book title & author, or ISBN only to read: ")
                val query = scanner.nextLine()
                println("Searching for '$query'...")
                readBook(query, username)
                return LibraryMethods(username)
            }
            3 -> {
                return READER_DASHBOARD 
            }
            else -> {
                // This 'else' should ideally not be reached due to the `break` in the loop
                // but if it is, you might want to return to a default state or re-enter the loop.
                return LibraryMethods(username) // Default to staying in this menu
            }
        }
    }

    //TODO
    fun readerOfTheWeek() {
        var reader = loadReaderOfTheWeek()
        println("+----------------------------------------------------------------+")
        println("|                      READER OF THE WEEK                        |")
        println("+----------------------------------------------------------------+")
        if(reader != null){
            println("CONGRATULATIONS TO ${reader.username} who read ${reader.numberOfBooksRead} books!")
        } else {
            println("No reader of the week found or an error occurred.")
        }
        println("Press Enter to continue...")
        scanner.nextLine() 
        readLine()
    }

    // fun loadReaderOfTheWeek(): Int{
    //     var maxReader: Users? = null
    //     try {
    //         File("user.csv").useLines { lines ->
    //             lines.drop(1).forEach { line -> // Skip header
    //                 val parts = line.split(",")
    //                 if (parts.size >= 3) {
    //                     val username = parts[0]
    //                     val pass = parts[1]
    //                     val numberOfBooksRead = parts[2].toIntOrNull() ?: 0 
    //                     if (maxReader == null || numberOfBooksRead > maxReader.numberOfBooksRead) {
    //                         maxReader = Users(username, pass, numberOfBooksRead)
    //                     }
    //                 }
    //             }
    //         }
    //     } catch (e: Exception) {
    //         println("Error reading CSV: ${e.message}")
    //     }
    //     return maxReader
    // }


    fun loadReaderOfTheWeek(): Users? {
        val allUsers = userManager.retrieveReaders() // Load fresh data
        // Use maxByOrNull to find the user with the maximum numberOfBooksRead
        return allUsers.maxByOrNull { it.numberOfBooksRead }
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
        scanner.nextLine()
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

    private fun saveReadChanges(users: List<Users>): Boolean{
        val USER_CSV_FILE = File("user.csv")
        try{
            val lines = mutableListOf<String>()
            lines.add("username,password,numberOfBooksRead") // Add header

            users.forEach { user ->
                lines.add("${user.username},${user.pass},${user.numberOfBooksRead}")
            }

            USER_CSV_FILE.writeText(lines.joinToString("\n")) // Write all lines back to the file
            println("User data saved successfully.")
            return true
        } catch (e: Exception) {
            println("Error saving user data to CSV: ${e.message}")
            return false
        }
    }
    
    fun searchBook(query: String): List<Book> {
        val results = books.filter { it.title.equals(query, ignoreCase = true) ||
                                      it.author.equals(query, ignoreCase = true) ||
                                      it.isbn.equals(query, ignoreCase = true) }
        return if (results.isEmpty()) {
            println("No books found for '$query'")
            emptyList()
        } else {
            println("\n--- Search Results ---")
            results.forEach { println("Found: ${it.title} by ${it.author}, ISBN: ${it.isbn}, Available: ${it.available}") }
            println("----------------------\n")
            results
        }
    }
}



class LogInSignUp{
    var retrievedUsers = retrieveReaders().toMutableList()
    var retrievedLibrariansUsers = retrievedLibrarians().toMutableList()

    fun InitUserAndGetUser(): UserLoginResult  {
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
                    scanner.nextLine()
                }
                else{
                    break
                }
            }
            catch(e: Exception){
                println("Error: Invalid input. Please enter a valid number.")
                scanner.nextLine()
            }
        }
        
        return when(choice){
            1 -> { //Log In
                val userType = readerOrLibrarian()
                when(userType){
                    1-> logInReader() 
                    2-> logInLibrarian()
                    else ->{
                        println("You must specify what type of user you are")
                        StayOnLogin
                    }
                }
            }
            2 -> { //Sign UP
                println("Sign Up")
                val userType = readerOrLibrarian()
                when (userType) {
                    1 -> signUpReader() 
                    2 -> signUpLibrarian()
                    else ->{
                        println("You must specify what type of user you are")
                        StayOnLogin // Invalid user type selection
                    }
                }
        
            }
            3 -> ExitApp
            else -> StayOnLogin
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
                if(choice !in 1..3) println("Invalid choice. Please select a number between 1 and 2.")
                else return choice
            }
            catch(e: Exception){
                println("Error: Invalid input. Please enter a valid number.")
                scanner.nextLine()
            }
        }
    }

    fun logInReader(): UserLoginResult {
        val scanner = Scanner(System.`in`)
        println("+-------------------------------------------------+")
        println("|                  Log In                         |")
        println("+-------------------------------------------------+")
        print("Username: ")
        var username = scanner.nextLine()
        print("Password: ")
        var password = scanner.nextLine()

        val foundUser = retrievedUsers.find { it.username == username && it.pass == password }
        return if (foundUser != null){
            println("Reader login successful.")
            ReaderLoggedIn(foundUser) // <--- Correctly returns UserLoginResult
        } else {
            println("No existing account or incorrect credentials.")
            println("Sign up first or try again.")
            StayOnLogin // <--- Correctly returns UserLoginResult
        } 
    }


    fun logInLibrarian(): UserLoginResult {
        println("Log In Librarian")
        val scanner = Scanner(System.`in`)
        println("+-------------------------------------------------+")
        println("|                  Log In                         |")
        println("+-------------------------------------------------+")
        print("Username: ")
        var username = scanner.nextLine()
        print("Password: ")
        var password = scanner.nextLine()

        val foundLibrarian = retrievedLibrariansUsers.find { it.username == username && it.pass == password }
        return if (foundLibrarian != null){
            println("Librarian login successful.")
            LibrarianLoggedIn(foundLibrarian) // <--- Correctly returns UserLoginResult
        } else {
            println("No existing account or incorrect credentials.")
            println("Sign up first or try again.")
            StayOnLogin // <--- Correctly returns UserLoginResult
        }
    }

    fun signUpReader(): UserLoginResult {
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

        if (retrievedUsers.any { it.username == username }) { // Use `any` for clearer "exists" check
            println("Username already exists! Try a different one.")
            return StayOnLogin
        }   

        saveSignUpReaderAcc(username, password)
        val newUser = Users(username, password, 0)
        retrievedUsers.add(newUser) // Add to in-memory list
        println("Account created successfully for $username.")
        return ReaderLoggedIn(newUser) // <--- Correctly returns UserLoginResult with the new user
    }

    fun signUpLibrarian(): UserLoginResult {
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
        
        if (retrievedLibrariansUsers.any { it.username == username }) { // Use `any` for clearer "exists" check
            println("Username already exists! Try a different one.")
            return StayOnLogin
        }

        saveSignUpLibrarianAcc(username, password)
        val newLibrarian = Librarians(username, password)
        retrievedLibrariansUsers.add(newLibrarian) // Add to in-memory list
        println("Account created successfully for $username.")
        return LibrarianLoggedIn(newLibrarian) // <--- Correctly returns UserLoginResult with the new librarian
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


class userReader(var username: String, var password: String){

    init {
        println("+---------------------------------------+")
        println("|       LIBRARY MANAGEMENT SYSTEM       |")
        println("+---------------------------------------+")
        println("Initialized reader dashboard...") // More specific message
    }

    fun readerPage(): AppState{ 
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
                    scanner.nextLine()
                }
            } catch(e: Exception){
                println("Error: Invalid input. Please enter a valid number.")
                scanner.nextLine()
            }
            
            when(choice) {
                1 -> {
                    println("Library")
                    val library = Library()
                    library.MainDashBoard(username)
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
                        return LOGIN_SIGNUP
                    }
                }
                11 -> {
                    println("Logging out...")
                    return LOGIN_SIGNUP // Indicate that we want to go back to login/signup
                }
                else -> { // Handle invalid choices
                    println("Invalid choice. Please select a number between 1 and 11.")
                }
            }
        }
    }

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
    fun librarianPage(): AppState{
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
                if(choice !in 1..10){
                    println("Invalid choice. Please select a number between 3 and 10.")
                }
            }
            catch(e: Exception){
                println("Error: Invalid input. Please enter a valid number.")
                scanner.nextLine()
            }
        
        
            // TODO
            when(choice){
                // TODO: Implement librarian functionalities
                1 -> println("Add Book functionality not implemented yet.")
                2 -> println("Remove Book functionality not implemented yet.")
                3 -> println("Update Book Info functionality not implemented yet.")
                4 -> println("View All Books functionality not implemented yet.")
                5 -> println("View Borrowed Books functionality not implemented yet.")
                6 -> println("View Overdue Books functionality not implemented yet.")
                7 -> println("Manage Your Account functionality not implemented yet.")
                8 -> println("View Ratings and Reviews functionality not implemented yet.")
                9 -> println("Generate Library Reports functionality not implemented yet.")
                10 -> {
                    println("Logging out...")
                    return LOGIN_SIGNUP // <--- Correct (imported from AppState.*)
                }
                else -> {
                    // Similar to readerPage, loop continues for invalid input
                }
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
    val loginSignUp = LogInSignUp()
    var currentAppState: AppState = LOGIN_SIGNUP // <--- Correct (imported from AppState.*)

    var activeReader: Users? = null
    var activeLibrarian: Librarians? = null

    while (currentAppState != EXIT) { // <--- Correct (imported from AppState.*)
        when (currentAppState) {
            LOGIN_SIGNUP -> { // <--- Correct (imported from AppState.*)
                val result = loginSignUp.InitUserAndGetUser()
                when (result) { // This 'when' is exhaustive because UserLoginResult is a sealed class
                    is ReaderLoggedIn -> { // <--- Correct (imported from UserLoginResult.*)
                        // User is logged in as a reader, transition to reader dashboard
                        currentAppState = userReader(result.user.username, result.user.pass).readerPage()
                    }
                    is LibrarianLoggedIn -> { // <--- Correct (imported from UserLoginResult.*)
                        // User is logged in as a librarian, transition to librarian dashboard
                        currentAppState = userLibrarian(result.librarian.username, result.librarian.pass).librarianPage()
                    }
                    ExitApp -> { // <--- Correct (imported from UserLoginResult.*)
                        // User chose to exit from the initial login/signup menu
                        currentAppState = EXIT // <--- Correct (imported from AppState.*)
                    }
                    StayOnLogin -> { // <--- Correct (imported from UserLoginResult.*)
                        // Login or signup failed, stay on the login/signup screen
                        // currentAppState is already LOGIN_SIGNUP, so no change needed.
                    }
                }
            }

            READER_DASHBOARD -> {
                // If we are in READER_DASHBOARD, we should call the reader's main page.
                // It's crucial that `activeReader` is not null here.
                if (activeReader != null) {
                    // Call the readerPage, which handles all reader interactions
                    // and returns the *next* AppState (e.g., LOGIN_SIGNUP on logout/delete).
                    currentAppState = userReader(activeReader.username, activeReader.pass).readerPage()

                    // If the reader logged out or deleted their account, clear the activeReader.
                    if (currentAppState == LOGIN_SIGNUP) {
                        activeReader = null
                    }
                } else {
                    // This state should ideally only be entered after a successful ReaderLoggedIn.
                    // If activeReader is somehow null, it's an unexpected state; return to login.
                    println("Error: Reader dashboard reached without active reader. Returning to login.")
                    currentAppState = LOGIN_SIGNUP
                }
            }
            LIBRARIAN_DASHBOARD -> {
                // Similar logic for librarians
                if (activeLibrarian != null) {
                    // Call the librarianPage, which handles all librarian interactions
                    currentAppState = userLibrarian(activeLibrarian.username, activeLibrarian.pass).librarianPage()

                    // If the librarian logged out, clear the activeLibrarian.
                    if (currentAppState == LOGIN_SIGNUP) { // Assuming librarians also log out to LOGIN_SIGNUP
                        activeLibrarian = null
                    }
                } else {
                    println("Error: Librarian dashboard reached without active librarian. Returning to login.")
                    currentAppState = LOGIN_SIGNUP
                }
            }
            EXIT -> {
                // When currentAppState becomes EXIT, the `while` loop condition (currentAppState != EXIT)
                // becomes false, and the loop naturally terminates.
                // We'll call ExitPage() after the loop.
            }
        }
    }
    ExitPage() // Call ExitPage only when the main loop exits
}
