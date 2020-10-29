package com.ythwork.soda.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ythwork.soda.domain.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
