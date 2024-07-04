package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getBalanceById(int userId) {
        BigDecimal bd = null;
        String sql = "SELECT balance FROM account WHERE user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                bd = results.getBigDecimal("balance");

            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return bd;
    }

    @Override
    public Account getAccountByAccountId(int account_Id){
        Account account = null;
        String sql = "SELECT * FROM account WHERE account_id = ? ";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account_Id);
            if (results.next()) {
                account = mapRowToAccount(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return account;
    }

    @Override
    public BigDecimal getBalanceByUserId(int userId) {
        BigDecimal bd = null;
        String sql = "SELECT balance FROM account WHERE user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
               bd = results.getBigDecimal("balance");

            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return bd;
    }

    @Override
    public boolean updateBalanceByUserId(int user_id, BigDecimal balance) {
        boolean success = false;
        String sql = "UPDATE account SET balance = ? WHERE user_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, balance, user_id);
            if (rowsAffected == 0) {
                throw new DaoException("Unable to update");
            } else {
                success = true;
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return success;
    }
    @Override
    public Account getAccountByUserId(int userId){
        String sql = "SELECT * FROM account WHERE user_id = ?";

        Account account = null;
        try{
           SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if(results.next()) {
                account = mapRowToAccount(results);
            }
        }catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return account;

    }


    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setId(rs.getInt("account_id"));
        return account;
    }

}
