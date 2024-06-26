package budgetbuddy.transaction;

import budgetbuddy.account.Account;
import budgetbuddy.account.AccountManager;
import budgetbuddy.categories.Category;
import budgetbuddy.exceptions.EmptyArgumentException;
import budgetbuddy.exceptions.InvalidAddTransactionSyntax;
import budgetbuddy.exceptions.InvalidCategoryException;
import budgetbuddy.exceptions.InvalidIndexException;
import budgetbuddy.exceptions.InvalidTransactionTypeException;
import budgetbuddy.transaction.type.Income;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import budgetbuddy.transaction.type.Transaction;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionListTest {

    private TransactionList transactionList;
    private Account account;
    private Account account2;
    private AccountManager accountManager;

    @BeforeEach
    public void setUp() throws IOException {
        transactionList = new TransactionList();
        account = new Account(1);
        account2 = new Account(2);
        accountManager = new AccountManager();
        accountManager.getAccounts().add(account); // need to change this
    }

    @Test
    public void getTransactions_initiallyEmpty() {
        assertEquals(0, transactionList.getTransactions().size());
    }

    @Test
    public void processTransaction_addsTransaction()
            throws InvalidTransactionTypeException, InvalidAddTransactionSyntax,
            EmptyArgumentException, InvalidCategoryException {
        Transaction testTransaction = new Income(1, "test","Test", 200,
                "14-03-2024",
                account);
        testTransaction.setCategory(Category.fromNumber(1));
        transactionList.processTransaction("add /a/ 1 /t/Income /n/Test /$/200 /d/14-03-2024 /c/1", account);

        assertEquals(1, transactionList.getTransactions().size());
        assertEquals(testTransaction.getDescription(), transactionList.getTransactions().get(0).getDescription());
        assertEquals(testTransaction.getAmount(), transactionList.getTransactions().get(0).getAmount());
        assertEquals(testTransaction.getCategory().getCategoryName(),
                transactionList.getTransactions().get(0).getCategory().getCategoryName());
        assertEquals(testTransaction.getDate(), transactionList.getTransactions().get(0).getDate());
    }
    @Test

    public void processTransaction_withInvalidAddSyntax_throwsInvalidAddTransactionSyntax() {

        assertThrows(InvalidAddTransactionSyntax.class, () -> transactionList.processTransaction(
                "add Expense /n/Shopping /$/50 /d/14-03-2024 /c/2", account));
        assertThrows(InvalidAddTransactionSyntax.class, () -> transactionList.processTransaction(
                "add /t/Expense Shopping /$/50 /d/14-03-2024 /c/2", account));
        assertThrows(InvalidAddTransactionSyntax.class, () -> transactionList.processTransaction(
                "add /t/Expense /n/Shopping 50 /d/14-03-2024 /c/2", account));
        assertThrows(InvalidAddTransactionSyntax.class, () -> transactionList.processTransaction(
                "add /t/Expense /n/Shopping /$/50 14-03-2024 /c/2", account));
    }

    @Test
    public void processTransaction_withInvalidTransactionType_throwsTransactionTypeException() {

        assertThrows(InvalidTransactionTypeException.class, () -> transactionList.processTransaction(
                "add /a/ 1 /t/Donation /n/Test /$/200 /d/14-03-2024 /c/2", account));
    }

    @Test
    public void removeTransaction_removesCorrectTransaction() throws EmptyArgumentException,
            InvalidIndexException, InvalidCategoryException {
        Transaction testTransaction1 = new Income(1, "test","Test1", 100,
                "14-03-2024", account);
        testTransaction1.setCategory(Category.fromNumber(1));
        Transaction testTransaction2 = new Income(1, "test","Test2", 200,
                "16-03-2024", account);
        testTransaction1.setCategory(Category.fromNumber(2));
        transactionList.addTransaction(testTransaction1);
        transactionList.addTransaction(testTransaction2);

        transactionList.removeTransaction("delete 1", accountManager);

        assertEquals(1, transactionList.getTransactions().size());
        assertEquals(testTransaction2, transactionList.getTransactions().get(0));
    }

    @Test
    public void removeTransaction_withInvalidIndex_throwsIndexOutOfBoundsException() {
        Transaction testTransaction = new Income(1, "test","Test", 200,
                "14-03-2024", account);
        transactionList.addTransaction(testTransaction);

        assertThrows(InvalidIndexException.class, () -> transactionList.removeTransaction(
                "delete 2", accountManager));
    }

    @Test
    public void removeTransaction_withMissingIndex_throwsEmptyArgumentException() {
        Transaction testTransaction = new Income(1, "test","Test", 100,
                "14-03-2024", account);
        transactionList.addTransaction(testTransaction);

        assertThrows(EmptyArgumentException.class, () -> transactionList.removeTransaction(
                "delete", accountManager));
    }

    @Test
    public void removeTransaction_withInvalidIndex_throwsNumberFormatException() {
        Transaction testTransaction = new Income(1, "test","Test", 100,
                "14-03-2024", account);
        transactionList.addTransaction(testTransaction);

        assertThrows(NumberFormatException.class, () -> transactionList.removeTransaction(
                "delete one", accountManager));
    }

    @Test
    public void getAccountTransactions_filtersCorrectTransactions() throws InvalidCategoryException {
        Transaction testTransaction1 = new Income(1, "test","Test1", 100,
                "15-03-2024", account);
        testTransaction1.setCategory(Category.fromNumber(1));
        Transaction testTransaction2 = new Income(1, "test","Test2", 200,
                "16-03-2024", account);
        testTransaction2.setCategory(Category.fromNumber(1));
        Transaction testTransaction3 = new Income(2, "test","Test3", 300,
                "18-03-2024", account2);
        testTransaction3.setCategory(Category.fromNumber(1));
        transactionList.addTransaction(testTransaction1);
        transactionList.addTransaction(testTransaction2);
        transactionList.addTransaction(testTransaction3);

        ArrayList<Transaction> accountTransactions;
        accountTransactions = TransactionList.getAccountTransactions(transactionList.getTransactions(),1);

        assertEquals(2, accountTransactions.size());
        assertEquals(testTransaction1, accountTransactions.get(0));
        assertEquals(testTransaction2, accountTransactions.get(1));
    }

    @Test
    public void getCategoryTransactions_filtersCorrectTransactions() throws InvalidCategoryException {
        Transaction testTransaction1 = new Income(1, "test","Test1", 100,
                "15-03-2024", account);
        testTransaction1.setCategory(Category.fromNumber(1));
        Transaction testTransaction2 = new Income(1, "test","Test2", 200,
                "16-03-2024", account);
        testTransaction2.setCategory(Category.fromNumber(1));
        Transaction testTransaction3 = new Income(1, "test","Test3", 300,
                "18-03-2024", account);
        testTransaction3.setCategory(Category.fromNumber(5));
        transactionList.addTransaction(testTransaction1);
        transactionList.addTransaction(testTransaction2);
        transactionList.addTransaction(testTransaction3);

        ArrayList<Transaction> categoryTransactions;
        categoryTransactions = TransactionList.getCategoryTransactions(transactionList.getTransactions(),
                Category.fromNumber(1));

        assertEquals(2, categoryTransactions.size());
        assertEquals(testTransaction1, categoryTransactions.get(0));
        assertEquals(testTransaction2, categoryTransactions.get(1));
    }
}
