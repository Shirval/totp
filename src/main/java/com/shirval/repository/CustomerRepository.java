package com.shirval.repository;

import com.shirval.exception.NotFoundException;
import com.shirval.model.Customer;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CustomerRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Customer create() {
        String sql = "INSERT INTO CUSTOMER VALUES (DEFAULT)";
        SqlParameterSource params = new MapSqlParameterSource();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder);
        int id = ((Number) (keyHolder.getKeys().get("id"))).intValue();
        return new Customer(id, null);
    }

    public void updateLastValidTill(int customerId, Instant validTill) {
        String sql = "UPDATE CUSTOMER SET last_valid_till = :lastValidTill WHERE id = :customerId";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("lastValidTill", validTill.atOffset(ZoneOffset.UTC))
                .addValue("customerId", customerId);
        jdbcTemplate.update(sql, params);
    }

    public List<Customer> getAll() {
        String sql = "SELECT * FROM CUSTOMER";
        return jdbcTemplate.query(sql, customerMapper);
    }

    public void remove(int id) {
        jdbcTemplate.update("delete from customer where id = :id", new MapSqlParameterSource("id", id));
    }

    public Customer getById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM CUSTOMER WHERE id = :id",
                    new MapSqlParameterSource("id", id),
                    customerMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException(ex);
        }
    }

    private final RowMapper<Customer> customerMapper = (rs, i) ->
            new Customer(
                    rs.getInt("id"),
                    Optional
                            .ofNullable(rs.getTimestamp("last_valid_till"))
                            .map(Timestamp::toInstant)
                            .orElse(null)
            );
}
