package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
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
    public BigDecimal getAccountBalanceByUserId(Principal principal) {
        try {
            return accountDao.getBalanceByUserId(userDao.getUserByUsername(principal.getName()).getId());

        } catch (DaoException e ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/transfer/user")
    public List<Transfer> getTransferListByUserId(Principal principal){

        try {
            List <Transfer> approved= transferDao.getTransferListById(userDao.getUserByUsername(principal.getName()).getId(), TRANSFER_STATUS_APPROVED);
            List <Transfer> rejected= transferDao.getTransferListById(userDao.getUserByUsername(principal.getName()).getId(), TRANSFER_STATUS_REJECTED);
            List <Transfer> pending= transferDao.getTransferListById(userDao.getUserByUsername(principal.getName()).getId(), TRANSFER_STATUS_PENDING);

            List <Transfer> totalList = new ArrayList<>(approved.size() + rejected.size() + pending.size());


            totalList.addAll(approved);
            totalList.addAll(rejected);
            totalList.addAll(pending);

            return totalList;
        } catch (DaoException e ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/transfer/send")
    public Transfer sendTransferToUser(@Valid @RequestBody Transfer transfer) {
        if (transfer.getAccount_to() == transfer.getAccount_from()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot send money to yourself");
        }
        BigDecimal userFromBalance  =  accountDao.getAccountByAccountId(transfer.getAccount_from()).getBalance();
        BigDecimal transferAmount = transfer.getAmount();
        if(userFromBalance.compareTo(transferAmount) == -1){
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
        }
        BigDecimal empty = new BigDecimal(0);
        if(transferAmount.compareTo(empty) == 0 || transferAmount.compareTo(empty) == -1){
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot have 0 or negative transfer Amount");
        }
        try {
            transfer.setTransfer_type_id(TRANSFER_TYPE_SEND);
            transfer.setTransfer_status_id(TRANSFER_STATUS_APPROVED);
            Transfer newTransfer = transferDao.createTransfer(transfer);

            BigDecimal newBalanceAccountFrom = userFromBalance.subtract(transferAmount);
            BigDecimal userToBalance = accountDao.getAccountByAccountId(transfer.getAccount_to()).getBalance();
            BigDecimal newBalanceAccountTo = userToBalance.add(transferAmount);

            accountDao.updateBalanceByAccountId(transfer.getAccount_from(), newBalanceAccountFrom);

            accountDao.updateBalanceByAccountId(transfer.getAccount_to() ,newBalanceAccountTo);

            return newTransfer;
        } catch (DaoException e ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/transfer")
    public List<Transfer> getAllTransfers(@RequestParam(defaultValue = "0") int transferId, Principal principal) {
        int userId = userDao.getUserByUsername(principal.getName()).getId();

        if (transferId == 0) {
            try {
                return transferDao.getTransferListById(userId, TRANSFER_STATUS_APPROVED);
            } catch (DaoException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } else {
            try {
                List<Transfer> singleTransfer = new ArrayList<>();
                singleTransfer.add(transferDao.getTransferById(userId));
                return singleTransfer;
            } catch (DaoException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/transfer/request")
    public Transfer postRequest(@Valid @RequestBody Transfer transfer, Principal principal){

        int userId = userDao.getUserByUsername(principal.getName()).getId();
        BigDecimal userFromBalance  =  accountDao.getAccountByAccountId(transfer.getAccount_from()).getBalance();
        BigDecimal transferAmount = transfer.getAmount();
        int accountId = accountDao.getAccountByUserId(userId).getId();

        if (transfer.getAccount_to() == transfer.getAccount_from()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot send money to yourself");
        }
        if (accountId != transfer.getAccount_to()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        BigDecimal empty = new BigDecimal(0);
        if(transferAmount.compareTo(empty) == 0 || transferAmount.compareTo(empty) == -1){
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot have 0 or negative transfer Amount");
        }
        try {
            transfer.setTransfer_type_id(TRANSFER_TYPE_REQUEST);
            transfer.setTransfer_status_id(TRANSFER_STATUS_PENDING);
            Transfer newTransfer = transferDao.createTransfer(transfer);
            return newTransfer;
        } catch (DaoException e ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/transfer/pending")
    public List<Transfer> getPendingRequests(Principal principal) {
        try {
            List <Transfer> pending = transferDao.getTransferListById(userDao.getUserByUsername(principal.getName()).getId(), TRANSFER_STATUS_PENDING);
            return pending;
        } catch (DaoException e ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/transfer/pending/approved")
    public Transfer changeStatusToApproved(@Valid@RequestBody Transfer transfer){
        if(transfer.getTransfer_status_id() != TRANSFER_STATUS_APPROVED){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer should be approved");
        }

        Account accountFrom = accountDao.getAccountByAccountId(transfer.getAccount_from());
        Account accountTo = accountDao.getAccountByAccountId(transfer.getAccount_to());
        BigDecimal transferAmount = transfer.getAmount();

        if(accountFrom.getBalance().compareTo(transferAmount) == -1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Insufficient Funds");
        }

        if(accountFrom.getId() == accountTo.getId()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Can't send transfer amount to yourself");
        }


        try{

            BigDecimal newAccountFromBalance = accountFrom.getBalance().subtract(transferAmount);
            BigDecimal newAccountToBalance = accountTo.getBalance().add(transferAmount);

            accountDao.updateBalanceByAccountId(accountFrom.getId(), newAccountFromBalance);
            accountDao.updateBalanceByAccountId(accountTo.getId(), newAccountToBalance);

            return transferDao.updateTransferById(transfer);
        }catch (DaoException e ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping(path = "/transfer/pending/reject")
    public Transfer changeStatusToRejected(@Valid@RequestBody Transfer transfer){
        if(transfer.getTransfer_status_id() != TRANSFER_STATUS_REJECTED){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer should be rejected");
        }
        try {
            return transferDao.updateTransferById(transfer);
        } catch (DaoException e ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

}
