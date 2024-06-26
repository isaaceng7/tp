package budgetbuddy.transaction.type;

import budgetbuddy.account.Account;
import budgetbuddy.categories.Category;
import budgetbuddy.exceptions.InvalidCategoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {

    private Account account;

    @BeforeEach
    public void setUp() {
        account = new Account(1);
    }

    @Test
    public void testTransactionConstructor() {
        Transaction transaction = new Income(1, "test","Groceries",
                50.0f, "14-03-2024", new Account(1));
        assertEquals("Groceries", transaction.getDescription());
        assertEquals(50.0f, transaction.getAmount(), 0.001);
        LocalDate date = LocalDate.parse("14-03-2024", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        assertEquals(date, transaction.getDate());
    }

    @Test
    public void testGetDescription() {
        Transaction transaction = new Income(1, "test","Groceries",
                50.0f, "14-03-2024",
                account);
        assertEquals("Groceries", transaction.getDescription());
    }

    @Test
    public void testGetAmount() {
        Transaction transaction = new Income(1, "test","Groceries",
                50.0f, "14-03-2024",
                account);
        assertEquals(50.0f, transaction.getAmount(), 0.001);
    }

    @Test
    public void testGetCategory() throws InvalidCategoryException {
        Transaction transaction = new Income(1, "test","Groceries",
                50.0f, "14-03-2024",
                account);
        transaction.setCategory(Category.fromNumber(1));
        assertEquals("Dining", transaction.getCategory().getCategoryName());
    }

    @Test
    public void testGetDate() {
        Transaction transaction = new Income(1, "test","Groceries",
                50.0f, "14-03-2024",
                account);
        LocalDate date = LocalDate.parse("14-03-2024", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        assertEquals(date, transaction.getDate());
    }

    @Test
    public void testToString() throws InvalidCategoryException {
        Transaction transaction = new Income(1, "test","Groceries",
                50.0f, "14-03-2024",
                account);
        transaction.setCategory(Category.fromNumber(1));
        String expected = " Account Number: 1 |  Account Name: test |  Transaction Type: Income |" +
                "  Description: Groceries |  Date: 2024-03-14 |  Amount: 50.0 | " +
                " Category: Dining";
        assertEquals(expected, transaction.toString());
    }
}
