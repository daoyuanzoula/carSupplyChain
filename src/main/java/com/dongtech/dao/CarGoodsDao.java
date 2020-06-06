package com.dongtech.dao;


import com.dongtech.vo.*;

import java.util.List;

public interface CarGoodsDao {
    List<CarGoods> queryList(CarGoods carGoods) ;

    List<CarOrders> queryOrders();

    List<CarOrderDetails> queryOrdersDetails(String id);

    void saveOrders(List<Cart> cartInCookie);

    TearDownDetails queryOrderTearsDetails(CarOrderDetails c);

    void saveTearDownDetails(TearDownDetails t);

    List<TearDownDetails> queryAllOrderTearsDetails(String order_id);
}
