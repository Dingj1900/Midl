package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transfer> getTransferListById(int id, int status) {
        List<Transfer> returnedTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer " +
                "join account AS accountTo ON account_to = accountTo.account_id " +
                "join account As accountFrom On account_from = accountFrom.account_id " +
                "where transfer_status_id = ? AND (accountTo.user_id = ? OR accountFrom.user_id = ?)";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, status, id, id);
            while (results.next()) {
                returnedTransfers.add(mapRowToTransfer(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return returnedTransfers;
    }

    @Override
    public Transfer getTransferById(int id) {
        String sql = "SELECT * " +
                     "FROM transfer " +
                     "WHERE transfer_id = ?";

        Transfer returnedTransfer = null;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                returnedTransfer = mapRowToTransfer(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }catch (Exception error){
            System.out.println("pain");
        }

        return returnedTransfer;
    }

    @Override
    public Transfer updateTransferById(Transfer transfer) {
        Transfer updatedTransfer = null;
        String sql = "UPDATE transfer " +
                     "SET transfer_type_id = ?, transfer_status_id = ?, account_from = ?, account_to = ?, amount = ?\n" +
                     "WHERE transfer_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, transfer.getTransfer_type_id(), transfer.getTransfer_status_id(),
                    transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAmount(), transfer.getTransfer_id());
            if (rowsAffected == 0) {
                throw new DaoException("Unable to update");
            }
            updatedTransfer = getTransferById(transfer.getTransfer_id());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return updatedTransfer;
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {
        Transfer newTransfer = null;
        String sql =
                "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?,?,?,?,?) RETURNING transfer_id";

        int newTransferId = 0;
        try {
            newTransferId = jdbcTemplate.queryForObject(sql, int.class,
                    transfer.getTransfer_type_id(),
                    transfer.getTransfer_status_id(),
                    transfer.getAccount_from(),
                    transfer.getAccount_to(),
                    transfer.getAmount());

            newTransfer = getTransferById(newTransferId);

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return newTransfer;
    }


    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransfer_id(rs.getInt("transfer_id"));
        transfer.setTransfer_type_id(rs.getInt("transfer_type_id"));
        transfer.setTransfer_status_id(rs.getInt("transfer_status_id"));
        transfer.setAccount_from(rs.getInt("account_from"));
        transfer.setAccount_to(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }
}
