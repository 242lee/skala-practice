package com.sk.skala.stockapi.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.dto.StockOrder;
import com.sk.skala.stockapi.data.model.Player;
import com.sk.skala.stockapi.data.model.PlayerStock;
import com.sk.skala.stockapi.data.model.Stock;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.PlayerRepository;
import com.sk.skala.stockapi.repository.StockRepository;
import com.sk.skala.stockapi.tools.StringTool;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerService {

	// 플레이어 및 주식 데이터 접근을 위한 Repository 주입
	private final StockRepository stockRepository;
	private final PlayerRepository playerRepository;

	/**
	 * 플레이어 목록을 페이지 단위로 조회하는 메소드
	 * @param offset 페이지 번호 (0부터 시작)
	 * @param count 한 페이지에 포함될 데이터 개수
	 * @return Response 객체로 플레이어 목록 반환
	 */
	public Response getAllPlayers(int offset, int count) {
		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Order.asc("playerId")));
		Page<Player> paged = playerRepository.findAll(pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(offset);
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	/**
	 * 특정 플레이어 조회
	 * @param playerId 조회할 플레이어의 ID
	 * @return Response 객체로 플레이어 데이터 반환
	 * @throws ResponseException 플레이어가 존재하지 않을 경우 예외 발생
	 */
	public Response getPlayerById(String playerId) {
		Optional<Player> option = playerRepository.findById(playerId);
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		Response response = new Response();
		response.setBody(option.get());
		return response;
	}

	/**
	 * 새로운 플레이어 생성
	 * @param player 생성할 플레이어 객체
	 * @return Response 객체 반환
	 * @throws ParameterException 필수 값이 누락된 경우 예외 발생
	 * @throws ResponseException 플레이어 ID가 이미 존재하는 경우 예외 발생
	 */
	public Response createPlayer(Player player) {
		if (StringTool.isAnyEmpty(player.getPlayerId()) || player.getPlayerMoney() <= 0) {
			throw new ParameterException("playerId", "playerMoney");
		}

		if (playerRepository.existsById(player.getPlayerId())) {
			throw new ResponseException(Error.DATA_DUPLICATED);
		}
		playerRepository.save(player);
		return new Response();
	}

	/**
	 * 플레이어 정보 업데이트
	 */
	public Response updatePlayer(Player player) {
		if (StringTool.isAnyEmpty(player.getPlayerId()) || player.getPlayerMoney() <= 0) {
			throw new ParameterException("playerId", "playerMoney");
		}

		if (!playerRepository.existsById(player.getPlayerId())) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		playerRepository.save(player);
		return new Response();
	}

	/**
	 * 플레이어 삭제
	 */
	public Response deletePlayer(Player player) {
		if (!playerRepository.existsById(player.getPlayerId())) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		playerRepository.deleteById(player.getPlayerId());
		return new Response();
	}

	/**
	 * 플레이어가 주식을 구매하는 기능 (트랜잭션 적용)
	 */
	@Transactional
	public Response buyPlayerStock(StockOrder order) {
		Player player = playerRepository.findById(order.getPlayerId())
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));
		Stock stock = stockRepository.findById(order.getStockId())
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

		double totalCost = stock.getStockPrice() * order.getStockQuantity();
		if (totalCost > player.getPlayerMoney()) {
			throw new ResponseException(Error.INSUFFICIENT_FUNDS);
		}

		player.setPlayerMoney(player.getPlayerMoney() - totalCost);
		PlayerStock playerStock = new PlayerStock(stock, order.getStockQuantity());

		player.getPlayerStockList().stream()
				.filter(existingStock -> existingStock.getStockName().equals(playerStock.getStockName()))
				.findFirst()
				.ifPresentOrElse(
						existingStock -> existingStock.setStockQuantity(existingStock.getStockQuantity() + order.getStockQuantity()),
						() -> player.getPlayerStockList().add(playerStock)
				);

		playerRepository.save(player);
		return new Response();
	}

	/**
	 * 플레이어가 주식을 판매하는 기능 (트랜잭션 적용)
	 */
	@Transactional
	public Response sellPlayerStock(StockOrder order) {
		Player player = playerRepository.findById(order.getPlayerId())
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));
		Stock stock = stockRepository.findById(order.getStockId())
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

		PlayerStock existingStock = player.getPlayerStockList().stream()
				.filter(ps -> ps.getStockName().equals(stock.getStockName()))
				.findFirst()
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

		if (existingStock.getStockQuantity() < order.getStockQuantity()) {
			throw new ResponseException(Error.INSUFFICIENT_QUANTITY);
		}

		existingStock.setStockQuantity(existingStock.getStockQuantity() - order.getStockQuantity());
		if (existingStock.getStockQuantity() == 0) {
			player.getPlayerStockList().remove(existingStock);
		}

		player.setPlayerMoney(player.getPlayerMoney() + (stock.getStockPrice() * order.getStockQuantity()));
		playerRepository.save(player);

		return new Response();
	}
}
