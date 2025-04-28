package com.sk.skala.stockapi.data.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PlayerStock extends Stock {
	private int stockQuantity;


	public PlayerStock(Stock stock, int quantity) {
		super(stock.getStockName(), stock.getStockPrice());
		super.setId(stock.getId());
		this.stockQuantity = quantity;
	}
}
