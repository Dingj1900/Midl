package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class MainController {

    private final AccountDao accountDao;
    private final TransferDao transferDao;
    private final UserDao userDao;

    private final int TRANSFER_TYPE_REQUEST = 1;
    private final int TRANSFER_TYPE_SEND = 2;

    private final int TRANSFER_STATUS_PENDING = 1;
    private final int TRANSFER_STATUS_APPROVED = 2;
    private final int TRANSFER_STATUS_REJECTED = 3;

    public MainController(AccountDao accountDao, TransferDao transferDao, UserDao userDao){
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    @GetMapping(path = "/user")
    public List<User> listOfUsers() {
        return userDao.getUsers();
    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/account/balance")
    public BigDecimal getAccountBalanceByUserId(Principal principle) {
        try {
            return accountDao.getBalanceByUserId(userDao.getUserByUsername(principle.getName()).getId());

        } catch (DaoException e ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/transfer/user")
    public List<Transfer> getTransferListByUserId(Principal principal){
        try {
            return transferDao.getTransferListById(user_id);
        }catch (DaoException e ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

}
