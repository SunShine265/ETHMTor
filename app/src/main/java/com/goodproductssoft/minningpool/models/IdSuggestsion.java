package com.goodproductssoft.minningpool.models;

/**
 * Created by user on 5/4/2018.
 */

public class IdSuggestsion {
    String id;
    Miner.CoinType type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Miner.CoinType getType() {
        return type;
    }

    public void setType(Miner.CoinType type) {
        this.type = type;
    }
}
