package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    //Returns empty list and DaoException for errors
    List<Transfer> getTransferListById(int id);

    //Returns null Transfer object and DaoException for errors
    Transfer getTransferById(int id);

    //Returns null Transfer object and DaoException for errors
    Transfer updateTransferById(Transfer transfer);

    //Returns null Transfer object and DaoException for errors
    Transfer createTransfer(Transfer transfer);

}
