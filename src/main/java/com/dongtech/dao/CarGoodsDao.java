package com.dongtech.dao;


import com.dongtech.vo.*;

import java.util.List;
import java.util.Map;

public interface CarGoodsDao {
    /**
     * 查询商品清单
     * @param carGoods
     * @return
     */
    List<CarGoods> queryList(CarGoods carGoods) ;

    /**
     * 查询订单
     * @return
     */
    List<CarOrders> queryOrders();

    /**
     * 根据订单ID查询订单明细
     * @param id
     * @return
     */
    List<CarOrderDetails> queryOrdersDetails(Integer id);

    /**
     * 保存订单头信息
     * @param carOrders
     * @return 订单Id
     */
    int saveOrders(CarOrders carOrders);

    /**
     * 批量保存订单明细息
     * @param carOrderDetails
     */
    void batchSaveOrderDetails(List<CarOrderDetails> carOrderDetails);

    /**
     * 查询拆单明细
     * @param carOrderDetail
     * @return
     */
    List<TearDownDetails> queryOrdersTearDownDetails(CarOrderDetails carOrderDetail);

    /**
     * 保存拆单结果
     * @param tearDownDetail
     */
    void saveTearDownDetails(TearDownDetails tearDownDetail);

    /**
     * 统计全部订单不同商品销售数量
     * @return
     */
    Map<String, Integer> statisticCarOrderDetails();

}
