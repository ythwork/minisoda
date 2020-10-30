package com.ythwork.soda.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ythwork.soda.domain.Transaction;

// 거래내역 검색을 위해 JpaSpecificationExecutor 
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
	
}
