package com.sk.skala.stockapi.data.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sk.skala.stockapi.tools.JsonTool;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

	@Id
	private String playerId;

	private double playerMoney;

	@JsonIgnore
	private String playerStocks;

	public Player(String id, double money) {
		this.playerId = id;
		this.playerMoney = money;
	}

	public List<PlayerStock> getPlayerStockList() {
		if (this.playerStocks != null) {
			return JsonTool.toList(playerStocks, PlayerStock.class);
		} else {
			return new ArrayList<>();
		}
	}

	public void setPlayerStockList(List<PlayerStock> list) {
		this.playerStocks = JsonTool.toString(list);
	}
}
