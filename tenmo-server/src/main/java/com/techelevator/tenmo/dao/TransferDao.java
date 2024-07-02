package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {
    List<Transfer> getTransferListById(int id);
    Transfer getTransferById(int id);
    Transfer updateTransferById(Transfer transfer);
    Transfer createTransfer(Transfer transfer);

}
