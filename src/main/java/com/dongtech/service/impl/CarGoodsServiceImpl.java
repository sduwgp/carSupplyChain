package com.dongtech.service.impl;


import com.dongtech.dao.CarGoodsDao;
import com.dongtech.dao.impl.CarGoodsDaoImpl;
import com.dongtech.service.CarVGoodsService;
import com.dongtech.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

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
    public List<CarOrderDetails> queryOrdersDetails(Integer id) {
        return dao.queryOrdersDetails(id);
    }

    @Override
    public List<Cart> addGoodsToCart(List<Cart> cartList, int goodsId) {
        Optional<Cart> cartOptional = cartList.stream()
                .filter(cart -> cart.getId() == goodsId).findFirst();
        if (cartOptional.isPresent()) {
            cartList.forEach(cart -> {
                if (cart.getId() == goodsId) {
                    cart.setNum(cart.getNum() + 1);
                }
            });
        } else {
            Cart cart = new Cart();
            CarGoods queryCarGoods = new CarGoods();
            queryCarGoods.setId((long) goodsId);
            CarGoods carGoods = dao.queryList(queryCarGoods).get(0);
            BeanUtils.copyProperties(carGoods, cart);
            cart.setPrice(carGoods.getPrice().intValue());
            cartList.add(cart);
        }
        return cartList;
    }

    @Override
    public void saveOrders(List<Cart> cartInCookie) {
        Integer totalPrice = cartInCookie.stream().map(cart->cart.getNum()*cart.getPrice()).reduce((x, y) -> x + y).orElse(0);
        String number = UUID.randomUUID().toString();
        CarOrders carOrders = new CarOrders(number,new BigDecimal(totalPrice));
        int orderId = dao.saveOrders(carOrders);
        List<CarOrderDetails> carOrderDetails = new ArrayList<>(cartInCookie.size());
        cartInCookie.forEach(cart->{
            CarOrderDetails carOrderDetail = new CarOrderDetails();
            carOrderDetail.setGoodsname(cart.getName());
            carOrderDetail.setNum(cart.getNum());
            carOrderDetail.setPrice(new BigDecimal(cart.getPrice()));
            carOrderDetail.setProduce(cart.getProduce());
            carOrderDetail.setOrderId(orderId);
            carOrderDetails.add(carOrderDetail);
        });
        dao.batchSaveOrderDetails(carOrderDetails);
    }

    @Override
    public TearDownDetails queryOrdersTearDownDetails(CarOrderDetails carOrderDetail) {
        return dao.queryOrdersTearDownDetails(carOrderDetail).isEmpty() ? null :dao.queryOrdersTearDownDetails(carOrderDetail).get(0);
    }

    @Override
    public void saveTearDownDetails(TearDownDetails tearDownDetail) {
        dao.saveTearDownDetails(tearDownDetail);
    }

    @Override
    public Map<String, Object> statisticCarOrderDetails() {
        Map<String, Integer> stringIntegerMap = dao.statisticCarOrderDetails();
        List<String> xAxisCategory = new ArrayList<>();
        List<Integer> datas = new ArrayList<>();
        for(Map.Entry<String, Integer> entry: stringIntegerMap.entrySet())
        {
            xAxisCategory.add(entry.getKey());
            datas.add(entry.getValue());
        }
        Map<String,Object> echarts = new HashMap<>();
        echarts.put("data", datas);
        echarts.put("categories", xAxisCategory);
        return echarts;
    }

    @Override
    public Map<String, Object> statisticCarOrderDetailsByPie() {
        Map<String, Integer> stringIntegerMap = dao.statisticCarOrderDetails();
        List<String> xAxisCategory = new ArrayList<>();
        List<Object> datas = new ArrayList<>();
        for(Map.Entry<String, Integer> entry: stringIntegerMap.entrySet())
        {
            xAxisCategory.add(entry.getKey());
            Map<String,Object> map = new HashMap<>();
            map.put("name", entry.getKey());
            map.put("value", entry.getValue());
            datas.add(map);
        }
        Map<String,Object> echarts = new HashMap<>();
        echarts.put("data", datas);
        echarts.put("categories", xAxisCategory);
        return echarts;
    }

}
