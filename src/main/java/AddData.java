import Entities.*;
import Repositories.*;
import java.sql.Date;

/**
 * A utility class for populating the Library Management System database with sample data.
 * This class demonstrates creating and persisting entities such as Users, Books, Copies, Publishers, Borrowings, and Librarians.
 */
public class AddData {
    public static void main(String[] args) {
        UserRepo userRepo = new UserRepo();
        BookRepo bookRepo = new BookRepo();
        CopyRepo copyRepo = new CopyRepo();
        BorrowingRepo borrowingRepo = new BorrowingRepo();
        PublisherRepo publisherRepo = new PublisherRepo();
        LibrarianRepo librarianRepo = new LibrarianRepo();

        Publisher publisher1 = new Publisher();
        publisher1.setName("Penguin Random House");
        publisher1.setAddress("123 Publisher Lane");
        publisher1.setPhoneNumber("555-1234");
        publisherRepo.createPublisher(publisher1);

        Publisher publisher2 = new Publisher();
        publisher2.setName("HarperCollins");
        publisher2.setAddress("456 Publisher St");
        publisher2.setPhoneNumber("555-5678");
        publisherRepo.createPublisher(publisher2);

        Publisher publisher3 = new Publisher();
        publisher3.setName("Simon & Schuster");
        publisher3.setAddress("789 Publisher Ave");
        publisher3.setPhoneNumber("555-8765");
        publisherRepo.createPublisher(publisher3);

        Publisher publisher4 = new Publisher();
        publisher4.setName("Macmillan Publishers");
        publisher4.setAddress("101 Publisher Blvd");
        publisher4.setPhoneNumber("555-4321");
        publisherRepo.createPublisher(publisher4);

        Book book1 = new Book();
        book1.setTitle("1984");
        book1.setAuthor("George Orwell");
        book1.setPublisherEntity(publisher1);
        book1.setPublicationYear(1949);
        book1.setIsbn("9780451524935");
        bookRepo.createBook(book1);

        Book book2 = new Book();
        book2.setTitle("To Kill a Mockingbird");
        book2.setAuthor("Harper Lee");
        book2.setPublisherEntity(publisher2);
        book2.setPublicationYear(1960);
        book2.setIsbn("9780061120084");
        bookRepo.createBook(book2);

        Book book3 = new Book();
        book3.setTitle("The Catcher in the Rye");
        book3.setAuthor("J.D. Salinger");
        book3.setPublisherEntity(publisher3);
        book3.setPublicationYear(1951);
        book3.setIsbn("9780316769488");
        bookRepo.createBook(book3);

        Book book4 = new Book();
        book4.setTitle("Pride and Prejudice");
        book4.setAuthor("Jane Austen");
        book4.setPublisherEntity(publisher4);
        book4.setPublicationYear(1813);
        book4.setIsbn("9780141439518");
        bookRepo.createBook(book4);

        Copy copy1 = new Copy();
        copy1.setBook(book1);
        copy1.setCopyNumber(1);
        copy1.setStatus("Available");
        copyRepo.createCopy(copy1);

        Copy copy2 = new Copy();
        copy2.setBook(book2);
        copy2.setCopyNumber(1);
        copy2.setStatus("Borrowed");
        copyRepo.createCopy(copy2);

        Copy copy3 = new Copy();
        copy3.setBook(book3);
        copy3.setCopyNumber(1);
        copy3.setStatus("Available");
        copyRepo.createCopy(copy3);

        Copy copy4 = new Copy();
        copy4.setBook(book4);
        copy4.setCopyNumber(1);
        copy4.setStatus("Available");
        copyRepo.createCopy(copy4);

        Copy copy5 = new Copy();
        copy5.setBook(book4);
        copy5.setCopyNumber(2);
        copy5.setStatus("Borrowed");
        copyRepo.createCopy(copy5);

        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPhoneNumber("555-1111");
        user1.setAddress("123 Elm Street");
        userRepo.createUser(user1);

        User user2 = new User();
        user2.setName("Jane Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setPhoneNumber("555-2222");
        user2.setAddress("456 Oak Avenue");
        userRepo.createUser(user2);

        User user3 = new User();
        user3.setName("Robert Brown");
        user3.setEmail("robert.brown@example.com");
        user3.setPhoneNumber("555-3333");
        user3.setAddress("789 Pine Drive");
        userRepo.createUser(user3);

        User user4 = new User();
        user4.setName("Emily White");
        user4.setEmail("emily.white@example.com");
        user4.setPhoneNumber("555-4444");
        user4.setAddress("101 Maple Blvd");
        userRepo.createUser(user4);

        User user5 = new User();
        user5.setName("New Librarian");
        user5.setEmail("librarian.new@example.com");
        user5.setPhoneNumber("555-5555");
        user5.setAddress("123 Library Lane");
        userRepo.createUser(user5);

        Librarian newLibrarian = new Librarian();
        newLibrarian.setUser(user5);
        newLibrarian.setEmploymentDate(Date.valueOf("2023-01-01"));
        newLibrarian.setPosition("Librarian");
        librarianRepo.createLibrarian(newLibrarian);

        Borrowing borrowing1 = new Borrowing();
        borrowing1.setUser(user1);
        borrowing1.setCopy(copy2);
        borrowing1.setBorrowDate(new java.util.Date());
        borrowingRepo.createBorrowing(borrowing1);

        Borrowing borrowing2 = new Borrowing();
        borrowing2.setUser(user3);
        borrowing2.setCopy(copy5);
        borrowing2.setBorrowDate(new java.util.Date());
        borrowingRepo.createBorrowing(borrowing2);

        System.out.println("Sample data added successfully!");
    }
}