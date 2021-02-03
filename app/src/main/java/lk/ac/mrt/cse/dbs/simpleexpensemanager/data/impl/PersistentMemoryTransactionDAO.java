package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentMemoryTransactionDAO implements TransactionDAO {
    SQLiteDatabase sqLiteDatabase;

    public PersistentMemoryTransactionDAO(SQLiteDatabase db){
        this.sqLiteDatabase =db;
    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount){
        String insertionQuery = "INSERT INTO TRANSACTION_TBL (accountNo,expenseType,amount,date) VALUES (?,?,?,?)";
        SQLiteStatement statement = sqLiteDatabase.compileStatement(insertionQuery);
        statement.bindString(1,accountNo);
        statement.bindLong(2,(expenseType == ExpenseType.EXPENSE) ? 0 : 1);
        statement.bindDouble(3,amount);
        statement.bindLong(4,date.getTime());
        statement.executeInsert();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transaction = new ArrayList<>();
        String Transaction_Selection = "SELECT * FROM TRANSACTION_TBL";
        Cursor result = sqLiteDatabase.rawQuery(Transaction_Selection, null);
        try {
            if (result.moveToFirst()) {
                do {
                    Transaction trans = new Transaction(
                            new Date(result.getLong(result.getColumnIndex("date"))),
                            result.getString(result.getColumnIndex("accountNo")),
                            (result.getInt(result.getColumnIndex("expenseType")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                            result.getDouble(result.getColumnIndex("amount")));
                    transaction.add(trans);
                } while (result.moveToNext());
            }
        }catch (Exception e) {
                e.printStackTrace();
            }

        return transaction;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transLog = new ArrayList<>();
        String query = "SELECT * FROM TRANSACTION_TBL LIMIT"+limit;
        Cursor result = sqLiteDatabase.rawQuery(query, null);
        if (result.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(new Date(result.getLong(result.getColumnIndex("date"))) ,
                        result.getString(result.getColumnIndex("accountNo")),
                        (result.getInt(result.getColumnIndex("expenseType")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                        result.getDouble(result.getColumnIndex("amount")));
                transLog.add(transaction);
            } while (result.moveToNext());
        }
        return  transLog;
    }
}
