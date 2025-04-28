import { reactive } from "vue";

const storage = reactive({
  playerId: '',
  playerMoney: 0,
  playerStockList: []
})

export const storePlayer = (player) => {
  storage.playerId = player.playerId;
  storage.playerMoney = player.playerMoney;
  storage.playerStockList = player.playerStockList;
}

export const usePlayer = () => {
  return storage;
}