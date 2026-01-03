package com.shirval.repository;

import com.shirval.exception.NotFoundException;
import com.shirval.model.Code;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

@Repository
public class CodeRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CodeRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(Code code) {
        String sql = """
                INSERT INTO CODE
                (customer_id, code, valid_from, valid_till)
                values
                (:customerId, :code, :validFrom, :validTill)
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("customerId", code.customerId())
                .addValue("code", code.code())
                .addValue("validFrom", code.validFrom().atOffset(ZoneOffset.UTC))
                .addValue("validTill", code.validTill().atOffset(ZoneOffset.UTC));
        jdbcTemplate.update(sql, params);
    }

    public boolean checkCode(int customerId, String code) {
        String sql = """
                SELECT count(1) FROM CODE
                WHERE customer_id = :customerId
                AND valid_from <= :now
                AND valid_till > :now
                AND code = :code
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("customerId", customerId)
                .addValue("code", code)
                .addValue("now", Instant.now().atOffset(ZoneOffset.UTC));

        return jdbcTemplate.queryForObject(sql, params, Integer.class) > 0;
    }

    public List<Code> getCodes(int customerId) {
        String sql = "SELECT * FROM CODE WHERE customer_id = :customerId ORDER BY valid_till DESC";
        SqlParameterSource params = new MapSqlParameterSource("customerId", customerId);
        return jdbcTemplate.query(sql, params, codeRowMapper);
    }

    public Code getCode(int customerId, Instant time) {
        String sql = "SELECT * FROM CODE WHERE customer_id = :customerId and valid_from <= :time and valid_till > :time limit 1";
        SqlParameterSource params = new MapSqlParameterSource("customerId", customerId).addValue("time", time.atOffset(ZoneOffset.UTC));
        try {
            return jdbcTemplate.queryForObject(sql, params, codeRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException(ex);
        }
    }

    public void removeByCustomerId(int customerId) {
        jdbcTemplate.update(
                "DELETE FROM CODE WHERE customer_id = :customerId",
                new MapSqlParameterSource("customerId", customerId)
        );
    }

    public void removeOlderThan(Instant threshold) {
        jdbcTemplate.update(
                "DELETE FROM CODE WHERE valid_till < :threshold",
                new MapSqlParameterSource("threshold", threshold.atOffset(ZoneOffset.UTC))
        );
    }

    private final RowMapper<Code> codeRowMapper = (rs, i) -> new Code(
            rs.getInt("customer_id"),
            rs.getString("code"),
            rs.getTimestamp("valid_from").toInstant(),
            rs.getTimestamp("valid_till").toInstant()
    );
}
