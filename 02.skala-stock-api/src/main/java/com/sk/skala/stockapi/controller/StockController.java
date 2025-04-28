package com.sk.skala.stockapi.controller;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.model.Stock;
import com.sk.skala.stockapi.service.StockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock API", description = "주식 관련 API")
public class StockController {

	private final StockService stockService;

	@Operation(summary = "모든 주식 조회", description = "지정된 offset 및 count 값을 기준으로 주식 목록을 가져옵니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 주식 목록을 반환"),
			@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
	})
	@GetMapping("/list")
	public Response getAllStocks(
			@RequestParam(defaultValue = "0") @Parameter(description = "조회 시작 위치") Integer offset,
			@RequestParam(defaultValue = "10") @Parameter(description = "조회할 개수") Integer count) {
		return stockService.getAllStocks(offset, count);
	}

	@Operation(summary = "특정 주식 조회", description = "ID를 기준으로 주식 정보를 조회합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 주식 정보를 반환"),
			@ApiResponse(responseCode = "404", description = "해당 ID의 주식을 찾을 수 없음")
	})
	@GetMapping("/{id}")
	public Response getStockById(@PathVariable @Parameter(description = "조회할 주식 ID") Long id) {
		return stockService.getStockById(id);
	}

	@Operation(summary = "새로운 주식 생성", description = "주식 정보를 저장합니다.")
	@PostMapping
	public Response createStock(@RequestBody @Parameter(description = "저장할 주식 객체") Stock stock) {
		return stockService.createStock(stock);
	}

	@Operation(summary = "주식 정보 수정", description = "기존 주식 정보를 수정합니다.")
	@PutMapping
	public Response updateStock(@RequestBody @Parameter(description = "수정할 주식 객체") Stock stock) {
		return stockService.updateStock(stock);
	}

	@Operation(summary = "주식 삭제", description = "주식 정보를 삭제합니다.")
	@DeleteMapping
	public Response deleteStock(@RequestBody @Parameter(description = "삭제할 주식 객체") Stock stock) {
		return stockService.deleteStock(stock);
	}
}
