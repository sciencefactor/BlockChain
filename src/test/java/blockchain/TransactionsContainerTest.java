package blockchain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.junit.Test;

public class TransactionsContainerTest {
    @Test
    public void testConstructor() {
        TransactionsContainer actualTransactionsContainer = new TransactionsContainer();
        ArrayList<Transaction> expectedTransactionsBuffer = actualTransactionsContainer.transactionsBuffer;
        assertSame(expectedTransactionsBuffer, actualTransactionsContainer.getTransactionsBuffer());
        assertEquals("Transactions Container: ", actualTransactionsContainer.toString());
    }

    @Test
    public void testConvertContainerToBytes() {
        assertEquals(152, TransactionsContainer.convertContainerToBytes(new TransactionsContainer()).length);
    }

    @Test
    public void testConvertBytesToContainer() throws UnsupportedEncodingException {
        assertNull(TransactionsContainer.convertBytesToContainer("AAAAAAAA".getBytes("UTF-8")));
        assertNull(TransactionsContainer.convertBytesToContainer(new byte[]{}));
    }

    @Test
    public void testAddTransaction() {
        TransactionsContainer transactionsContainer = new TransactionsContainer();
        Client name = new Client("Name");
        transactionsContainer.addTransaction(new Transaction(name, new Client("Name"), 1L));
        assertEquals(1, transactionsContainer.getTransactionsBuffer().size());
    }

    @Test
    public void testCalcMoneyOfClientInContainer() {
        Client client = new Client("Name");
        assertEquals(0L, TransactionsContainer.calcMoneyOfClientInContainer(client, new TransactionsContainer()));
    }

    @Test
    public void testCalcMoneyOfClientInContainer2() {
        Client client = new Client("Name");

        TransactionsContainer transactionsContainer = new TransactionsContainer();
        Client name = new Client("Name");
        transactionsContainer.addTransaction(new Transaction(name, new Client("Name"), 1L));
        assertEquals(0L, TransactionsContainer.calcMoneyOfClientInContainer(client, transactionsContainer));
    }

    @Test
    public void testCalcMoneyOfClientInContainer3() {
        Client client = new Client("Name");

        TransactionsContainer transactionsContainer = new TransactionsContainer();
        Client name = new Client("Name");
        transactionsContainer.addTransaction(new Transaction(name, new Client("Name"), 1L));
        Client name1 = new Client("Name");
        transactionsContainer.addTransaction(new Transaction(name1, new Client("Name"), 1L));
        assertEquals(0L, TransactionsContainer.calcMoneyOfClientInContainer(client, transactionsContainer));
    }

    @Test
    public void testCalcMoneyOfClientInContainer4() {
        Client client = new Client("Name");

        TransactionsContainer transactionsContainer = new TransactionsContainer();
        Client name = new Client("BLOCKCHAIN");
        transactionsContainer.addTransaction(new Transaction(name, new Client("Name"), 1L));
        assertEquals(1L, TransactionsContainer.calcMoneyOfClientInContainer(client, transactionsContainer));
    }

    @Test
    public void testClear() {
        TransactionsContainer transactionsContainer = new TransactionsContainer();
        transactionsContainer.clear();
        assertTrue(transactionsContainer.getTransactionsBuffer().isEmpty());
    }
}

