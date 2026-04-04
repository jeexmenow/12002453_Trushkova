package ru.bsuedu.cad.lab.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ru.bsuedu.cad.lab.CategoryProductSummary;

public class CategoryMultiProductRowMapper implements RowMapper<CategoryProductSummary> {

    @Override
    public CategoryProductSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CategoryProductSummary(
                rs.getLong(1),
                rs.getString(2),
                rs.getString(3),
                rs.getLong(4));
    }
}
