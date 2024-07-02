package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface AccountDao {

    //Return null for errors
    //Returns balance by id
    BigDecimal getBalanceById(int id);
}
