package com.sk.skala.stockapi.controller;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.dto.StockOrder;
import com.sk.skala.stockapi.data.model.Player;
import com.sk.skala.stockapi.service.PlayerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/players")
@Tag(name = "Player API", description = "플레이어 관련 API")
public class PlayerController {

	private final PlayerService playerService;

	@Operation(summary = "모든 플레이어 조회", description = "지정된 offset 및 count 값을 기준으로 플레이어 목록을 가져옵니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 플레이어 목록을 반환"),
			@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
	})
	@GetMapping("/list")
	public Response getAllPlayers(
			@RequestParam(value = "offset", defaultValue = "0") @Parameter(description = "조회 시작 위치") int offset,
			@RequestParam(value = "count", defaultValue = "10") @Parameter(description = "조회할 개수") int count) {
		return playerService.getAllPlayers(offset, count);
	}

	@Operation(summary = "특정 플레이어 조회", description = "ID를 기준으로 플레이어 정보를 조회합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 플레이어 정보를 반환"),
			@ApiResponse(responseCode = "404", description = "해당 ID의 플레이어를 찾을 수 없음")
	})
	@GetMapping("/{playerId}")
	public Response getPlayerById(@PathVariable @Parameter(description = "조회할 플레이어 ID") String playerId) {
		return playerService.getPlayerById(playerId);
	}

	@Operation(summary = "새로운 플레이어 생성", description = "플레이어 정보를 저장합니다.")
	@PostMapping
	public Response createPlayer(@RequestBody @Parameter(description = "저장할 플레이어 객체") Player player) {
		return playerService.createPlayer(player);
	}

	@Operation(summary = "플레이어 정보 수정", description = "기존 플레이어 정보를 수정합니다.")
	@PutMapping
	public Response updatePlayer(@RequestBody @Parameter(description = "수정할 플레이어 객체") Player player) {
		return playerService.updatePlayer(player);
	}

	@Operation(summary = "플레이어 삭제", description = "플레이어 정보를 삭제합니다.")
	@DeleteMapping
	public Response deletePlayer(@RequestBody @Parameter(description = "삭제할 플레이어 객체") Player player) {
		return playerService.deletePlayer(player);
	}

	@Operation(summary = "플레이어 주식 구매", description = "주어진 주문 정보를 기반으로 플레이어 주식을 구매합니다.")
	@PostMapping("/buy")
	public Response buyPlayerStock(@RequestBody @Parameter(description = "구매할 주식 주문 정보") StockOrder order) {
		return playerService.buyPlayerStock(order);
	}

	@Operation(summary = "플레이어 주식 판매", description = "주어진 주문 정보를 기반으로 플레이어 주식을 판매합니다.")
	@PostMapping("/sell")
	public Response sellPlayerStock(@RequestBody @Parameter(description = "판매할 주식 주문 정보") StockOrder order) {
		return playerService.sellPlayerStock(order);
	}
}
