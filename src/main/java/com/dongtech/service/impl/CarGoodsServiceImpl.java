package com.dongtech.service.impl;


import com.dongtech.dao.CarGoodsDao;
import com.dongtech.dao.impl.CarGoodsDaoImpl;
import com.dongtech.service.CarVGoodsService;
import com.dongtech.vo.*;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CarGoodsServiceImpl implements CarVGoodsService {

    CarGoodsDao dao = new CarGoodsDaoImpl();


    @Override
    public List<CarGoods> queryList(CarGoods carGoods) throws SQLException {
        return dao.queryList(carGoods);
    }

    @Override
    public List<CarOrders> queryOrders() {
        return dao.queryOrders();
    }

    @Override
    public List<CarOrderDetails> queryOrdersDetails(String id) {
        return dao.queryOrdersDetails(id);
    }

    @Override
    public void saveOrders(List<Cart> cartInCookie) {
        dao.saveOrders(cartInCookie);
    }

    @Override
    public TearDownDetails queryOrderTearsDetails(CarOrderDetails c) {
        return dao.queryOrderTearsDetails(c);
    }

    @Override
    public void saveTearDownDetails(TearDownDetails t) {
        dao.saveTearDownDetails(t);
    }

    @Override
    public List<TearDownDetails> queryAllOrderTearsDetails(String order_id) {
        return dao.queryAllOrderTearsDetails(order_id);
    }


}
