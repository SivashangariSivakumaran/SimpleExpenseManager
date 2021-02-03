package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentMemoryAccountDAO implements AccountDAO {
    SQLiteDatabase sqLiteDatabase;
    private static String TB_NAME="ACCOUNT";

    public PersistentMemoryAccountDAO(SQLiteDatabase sqLiteDatabase){
        this.sqLiteDatabase = sqLiteDatabase;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumList = new ArrayList<>();
        Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TB_NAME, null);

        result.moveToFirst();
        try {
            if (result.moveToFirst()) {
                do {
                    accountNumList.add(result.getString(result.getColumnIndex("accountNo")));
                } while (result.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result != null && !result.isClosed()) {
                result.close();
            }
        }
        return accountNumList;

    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accountList = new ArrayList<>();
            Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TB_NAME, null);

        try {
            if (result.moveToFirst()) {
                do {
                    String accountNum = result.getString(result.getColumnIndex("accountNo"));
                    String bank = result.getString(result.getColumnIndex("bankName"));
                    String holder = result.getString(result.getColumnIndex("accountHolderName"));
                    Double balance = result.getDouble(result.getColumnIndex("balance"));
                    Account account = new Account(accountNum, bank, holder, balance);
                    accountList.add(account);
                } while (result.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result != null && !result.isClosed()) {
                result.close();
            }
        }

        return accountList;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        Cursor result=sqLiteDatabase.rawQuery("SELECT * FROM "+TB_NAME+" WHERE accountNo=?",new String[]{accountNo});
        String bank=result.getString(result.getColumnIndex("bankName"));
        String holder=result.getString(result.getColumnIndex("accountHolderName"));
        Double balance=result.getDouble(result.getColumnIndex("balance"));
        Account account=new Account(accountNo,bank,holder,balance);
        return account;
    }

    @Override
    public void addAccount(Account account) {
        ContentValues contentValues=new ContentValues();
        contentValues.put("accountNo",account.getAccountNo());
        contentValues.put("bankName",account.getBankName());
        contentValues.put("accountHolderName",account.getAccountHolderName());
        contentValues.put("balance",account.getBalance());
        sqLiteDatabase.insert(TB_NAME,null,contentValues);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
            sqLiteDatabase.execSQL("DELETE FROM "+TB_NAME+" WHERE accountNo=?",new String[]{accountNo});
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        String query = "UPDATE ACCOUNT SET balance = balance + ? ";
        SQLiteStatement statement = sqLiteDatabase.compileStatement(query);
        if(expenseType == ExpenseType.EXPENSE){
            statement.bindDouble(1,-amount);
        }else{
            statement.bindDouble(1,amount);
        }
        statement.executeUpdateDelete();
    }
}
