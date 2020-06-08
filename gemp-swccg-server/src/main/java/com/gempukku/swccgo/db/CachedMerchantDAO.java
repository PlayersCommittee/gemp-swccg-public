package com.gempukku.swccgo.db;

import com.gempukku.swccgo.cache.Cached;
import org.apache.commons.collections.map.LRUMap;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class CachedMerchantDAO implements MerchantDAO, Cached {
    private MerchantDAO _delegate;
    private Map<String, Transaction> _blueprintIdLastTransaction = Collections.synchronizedMap(new LRUMap(4000));

    public CachedMerchantDAO(MerchantDAO delegate) {
        _delegate = delegate;
    }

    @Override
    public void clearCache() {
        _blueprintIdLastTransaction.clear();
    }

    @Override
    public int getItemCount() {
        return _blueprintIdLastTransaction.size();
    }

    @Override
    public void addTransaction(String blueprintId, float price, Date date, TransactionType transactionType) {
        _delegate.addTransaction(blueprintId, price, date, transactionType);
        _blueprintIdLastTransaction.remove(blueprintId);
    }

    @Override
    public Transaction getLastTransaction(String blueprintId) {
        Transaction transaction = (Transaction) _blueprintIdLastTransaction.get(blueprintId);
        if (transaction == null) {
            transaction = _delegate.getLastTransaction(blueprintId);
            _blueprintIdLastTransaction.put(blueprintId, transaction);
        }
        return transaction;
    }
}
