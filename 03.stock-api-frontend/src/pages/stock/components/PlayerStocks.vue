<script setup>
import { ref, reactive, onMounted } from 'vue';
import { storePlayer, usePlayer } from '@/scripts/store-player';
import apiCall from '@/scripts/api-call';
import { useRouter } from 'vue-router';

const router = useRouter();

const stockId = ref('');
const stockQuantity = ref('');

const table = reactive({
  headers: [
    { label: "주식ID", value: "id" },
    { label: "주식명", value: "stockName" },
    { label: "주식가격", value: "stockPrice" },
    { label: "보유수량", value: "stockQuantity" }
  ],
  items: []
})

const player = usePlayer();

const getPlayerInfo = async () => {
  table.items.length = 0
  const url = '/api/players/' + player.playerId;
  const response = await apiCall.get(url, null, null);
  if (response.result === apiCall.Response.SUCCESS) {
    storePlayer(response.body); // 플레이어 정보를 저장
    table.items = response.body.playerStockList;
  }
}

const buyPlayerStock = async () => {
  const url = '/api/players/buy';
  const requestBody = {
    playerId: player.playerId,
    stockId: stockId.value,
    stockQuantity: stockQuantity.value
  }

  await apiCall.post(url, null, requestBody);
  getPlayerInfo();
  stockId.value = ''
  stockQuantity.value = ''
}

const sellPlayerStock = async () => {
  const url = '/api/players/sell';
  const requestBody = {
    playerId: player.playerId,
    stockId: stockId.value,
    stockQuantity: stockQuantity.value
  }

  await apiCall.post(url, null, requestBody);
  getPlayerInfo();
  stockId.value = ''
  stockQuantity.value = ''
}

onMounted(() => {
  if (player.playerId === '') {
    router.push("/");
  } else {
    table.items = player.playerStockList;
  }
})
</script>

<template>
  <div class="row mt-2">
    <span class="fs-4"><i class="bi bi-person m-2"></i>{{ player.playerId }} 플레이어</span>
  </div>
  <div class="row border-bottom">
    <div class="col d-flex justify-content-end">
      <button class="btn btn-sm btn-primary m-1" @click="getPlayerInfo">
        <i class="bi bi-arrow-counterclockwise m-2"></i>갱신
      </button>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <InlineInput class="m-2" label="플레이어ID" v-model="player.playerId" :disabled="true" />
      <InlineInput class="m-2" label="보유금액" v-model="player.playerMoney" :disabled="true" />
    </div>
  </div>
  <div class="row g-2 align-items-center m-2 mt-0">
    <div class="col-2 d-flex justify-content-end">
      <label class="col-form-label form-control-sm p-1">보유주식목록</label>
    </div>
    <div class="col">
      <ItemsTable :headers="table.headers" :items="table.items" :nosetting="true" />
    </div>
  </div>
  <div class="row g-2 align-items-center m-2 mt-0">
    <div class="col-2 d-flex justify-content-end">
      <label class="col-form-label form-control-sm p-1">주식선택</label>
    </div>
    <div class="col">
      <InlineInput v-model="stockId" placeholder="주식ID" />
    </div>
    <div class="col">
      <InlineInput v-model="stockQuantity" placeholder="주식수량" />
    </div>
    <div class="col d-flex justify-content-start">
      <button class="btn btn-sm btn-outline-primary m-1" @click="buyPlayerStock">주식 구매</button>
      <button class="btn btn-sm btn-outline-primary m-1" @click="sellPlayerStock">주식 판매</button>
    </div>
  </div>
</template>
