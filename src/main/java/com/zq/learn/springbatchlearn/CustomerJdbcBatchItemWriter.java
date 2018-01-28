package com.zq.learn.springbatchlearn;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcParameterUtils;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义jdbc批处理写事件
 * @param <T>
 */
public class CustomerJdbcBatchItemWriter<T> extends JdbcBatchItemWriter {

    private NamedParameterJdbcOperations namedParameterJdbcTemplate;

    private ItemPreparedStatementSetter<T> itemPreparedStatementSetter;

    private ItemSqlParameterSourceProvider<T> itemSqlParameterSourceProvider;

    private String sql;

    private boolean assertUpdates = true;

    private int parameterCount;

    private boolean usingNamedParameters;

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(namedParameterJdbcTemplate, "A DataSource or a NamedParameterJdbcTemplate is required.");
    }

    @Override
    public void write(List items) throws Exception {
        beforeWrite(items);

        //assert
        Assert.notNull(sql, "An SQL statement is required.");
        List<String> namedParameters = new ArrayList<String>();
        parameterCount = JdbcParameterUtils.countParameterPlaceholders(sql, namedParameters);
        if (namedParameters.size() > 0) {
            if (parameterCount != namedParameters.size()) {
                throw new InvalidDataAccessApiUsageException("You can't use both named parameters and classic \"?\" placeholders: " + sql);
            }
            usingNamedParameters = true;
        }
        if (!usingNamedParameters) {
            Assert.notNull(itemPreparedStatementSetter, "Using SQL statement with '?' placeholders requires an ItemPreparedStatementSetter");
        }

        super.write(items);
    }

    protected void beforeWrite(List items) {

    }
}
