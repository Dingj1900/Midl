package com.techelevator.tenmo.model;


import javax.validation.constraints.*;
import java.math.BigDecimal;

public class Transfer {

    private int transfer_id;
    @Min(value = 1, message = "Can only be 1 or 2")
    @Max(value = 2, message = "Can only be 1 or 2")
    private int transfer_type_id;
    @Min(value = 1, message = "Can only be 1, 2, or 3")
    @Max(value = 3, message = "Can only be 1, 2, or 3")
    private int transfer_status_id;
    private int account_from;
    private int account_to;

    @NotNull
    @PositiveOrZero(message = "Can only be positive")
    private BigDecimal amount;

    public int getTransfer_id() {
        return transfer_id;
    }

    public void setTransfer_id(int transfer_id) {
        this.transfer_id = transfer_id;
    }

    public int getTransfer_type_id() {
        return transfer_type_id;
    }

    public void setTransfer_type_id(int transfer_type_id) {
        this.transfer_type_id = transfer_type_id;
    }

    public int getTransfer_status_id() {
        return transfer_status_id;
    }

    public void setTransfer_status_id(int transfer_status_id) {
        this.transfer_status_id = transfer_status_id;
    }

    public int getAccount_from() {
        return account_from;
    }

    public void setAccount_from(int account_from) {
        this.account_from = account_from;
    }

    public int getAccount_to() {
        return account_to;
    }

    public void setAccount_to(int account_to) {
        this.account_to = account_to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}