package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class PersistentMemoryDemoExpenseManager extends ExpenseManager {
    Context context;
    public PersistentMemoryDemoExpenseManager(Context context)  {
        this.context=context;
        try {
            setup();
        } catch (ExpenseManagerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setup() throws ExpenseManagerException {
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("180610UDB", context.MODE_PRIVATE, null);


        sqLiteDatabase.execSQL("create table if not exists ACCOUNT(" + "accountNo varchar primary key," + "bankName VARCHAR," + "accountHolderName varchar," + "balance REAL" + " );");

        sqLiteDatabase.execSQL("create table if not exists TRANSACTION_TBL(" + "transaction_id integer primary key," + "accountNo VARCHAR," + "expenseType INT," + "amount REAL," + "date DATE," + "foreign key(accountNo) references ACCOUNT(accountNo)" + ");");


        PersistentMemoryAccountDAO accountDAO = new PersistentMemoryAccountDAO(sqLiteDatabase);
        setAccountsDAO(accountDAO);
        setTransactionsDAO(new PersistentMemoryTransactionDAO(sqLiteDatabase));

        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        getAccountsDAO().addAccount(dummyAcct1);
        getAccountsDAO().addAccount(dummyAcct2);

    }
}
