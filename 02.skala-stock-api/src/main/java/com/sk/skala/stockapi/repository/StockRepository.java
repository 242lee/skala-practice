package com.sk.skala.stockapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.stockapi.data.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {
	Page<Stock> findAll(Pageable pageable);

	Optional<Stock> findByStockNameLike(String keyword);
}
