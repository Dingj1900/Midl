package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {

    //Return BigDecimal object = null for errors and DaoException
    BigDecimal getBalanceById(int userId);

    //Return Account object = null if account_id is not found and DaoException
    Account getAccountByAccountId(int accountId);

    //Return BigDecimal object = null for errors and DaoException
    BigDecimal getBalanceByUserId(int userId);

    //Returns false for fail, true for success and DaoException
    boolean updateBalanceById(Account account);

}
