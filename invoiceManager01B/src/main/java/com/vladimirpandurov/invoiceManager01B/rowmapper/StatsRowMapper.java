package com.vladimirpandurov.invoiceManager01B.rowmapper;

import com.vladimirpandurov.invoiceManager01B.domain.Stats;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class StatsRowMapper implements RowMapper<Stats> {

    @Override
    public Stats mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Stats.builder()
                .totalCustomers(resultSet.getInt("total_customers"))
                .totalInvoices(resultSet.getInt("total_invoice"))
                .totalBilled(resultSet.getDouble("total_billed"))
                .build();
    }

}
