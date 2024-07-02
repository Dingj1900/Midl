package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class MainController {

    private final AccountDao accountDao;
    private final TransferDao transferDao;
    public MainController(AccountDao accountDao, TransferDao transferDao){
        this.accountDao = accountDao;
        this.transferDao = transferDao;

    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/account/balance/{account_id}")
    public BigDecimal getAccountBalanceById(@RequestParam int account_id) {
        try {
            return accountDao.getBalanceById(account_id);
        } catch (DaoException e ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/transfer/user/{user_id}")
    public List<Transfer> getTransferListByUserId(@RequestParam int user_id ){
        try {
            return transferDao.getTransferListById(user_id);
        }catch (DaoException e ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

}
