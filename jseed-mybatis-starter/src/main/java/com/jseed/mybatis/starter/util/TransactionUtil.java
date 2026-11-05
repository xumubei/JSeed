package com.jseed.mybatis.starter.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Transaction manager
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@Slf4j
@AllArgsConstructor
public class TransactionUtil {


    private final DataSourceTransactionManager transactionManager;

    /**
     * Runs the operation with the default transaction propagation.
     *
     * @param runnable operation to run
     */
    public void execute(Runnable runnable) {
        execute(TransactionDefinition.PROPAGATION_REQUIRED, runnable);
    }

    /**
     * Runs the operation with the specified transaction propagation.
     *
     * @param transactionDefinition transaction propagation level, defaults to REQUIRED
     * @param runnable              operation to execute
     */
    public void execute(int transactionDefinition, Runnable runnable) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition(transactionDefinition));
        try {
            runnable.run();
            // Commit the transaction explicitly.
            transactionManager.commit(status);
        } catch (Exception e) {
            // Roll back the transaction explicitly.
            transactionManager.rollback(status);
            throw e;
        }
    }
}
