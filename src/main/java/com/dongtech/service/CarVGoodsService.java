package com.dongtech.service;

import com.dongtech.vo.*;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public interface CarVGoodsService {

    List<CarGoods> queryList(CarGoods carGoods) throws SQLException;



    List<CarOrders> queryOrders();

    List<CarOrderDetails> queryOrdersDetails(Integer id);


    /**
     * 向购物车中添加商品
     * @param cartList 购物车商品列表
     * @param goodsId 商品Id
     * @return
     */
    List<Cart> addGoodsToCart(List<Cart> cartList, int goodsId);

    /**
     * 保存订单
     * @param cartInCookie
     */
    void saveOrders(List<Cart> cartInCookie);

    /**
     * 查询拆单
     * @param carOrderDetail
     * @return
     */
    TearDownDetails queryOrdersTearDownDetails(CarOrderDetails carOrderDetail);

    /**
     * 保存拆单
     * @param tearDownDetail
     */
    void saveTearDownDetails(TearDownDetails tearDownDetail);

    /**
     * 统计商品销售情况
     * @return
     */
    Map<String,Object> statisticCarOrderDetails();

    /**
     * 按饼状图统计销售情况
     * @return
     */
    Map<String, Object> statisticCarOrderDetailsByPie();
}
