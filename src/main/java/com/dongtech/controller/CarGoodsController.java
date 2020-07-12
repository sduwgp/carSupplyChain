package com.dongtech.controller;

import com.dongtech.service.CarVGoodsService;
import com.dongtech.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.json.CookieList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gzl
 * @date 2020-04-15
 * @program: springboot-jsp
 * @description: ${description}
 */
@RestController
@RequestMapping("cargoods")
@Slf4j
public class CarGoodsController {


    @Resource
    private  CarVGoodsService carVGoodsService;


    /**
     * @Author gzl
     * @Description：查询商品列表
     * @Exception
     */
    @RequestMapping("/queryList")
    public ModelAndView queryList(CarGoods carGoods)  {
        List<CarGoods> list = new ArrayList<>();
        try {
            list = carVGoodsService.queryList(carGoods);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /**
         * 模型和视图
         * model模型: 模型对象中存放了返回给页面的数据
         * view视图: 视图对象中指定了返回的页面的位置
         */
        ModelAndView modelAndView = new ModelAndView();
        //将返回给页面的数据放入模型和视图对象中
        modelAndView.addObject("list", list);
        //指定返回的页面位置
        modelAndView.setViewName("carGoods/list");
        return modelAndView;
    }

    /**
     * @Author wgp
     * @Description：添加购物车
     * @Exception
     * @Date： 2020/7/11 11:59 PM
     */
    @RequestMapping("/addGoodsToCart")
    @ResponseBody
    public String addGoodsToCart(@RequestParam("id") String goodId, HttpServletRequest request, HttpServletResponse response)throws UnsupportedEncodingException{
        //从cookie中获取购物车列表
        List<Cart> cartList = getCartInCookie(response,request);
        Cookie cookie_2st;
        CarGoods carGoods = new CarGoods();
        try{
            CarGoods carGoods1 =new CarGoods();
            carGoods1.setId(Long.parseLong(goodId+""));
            List<CarGoods> carGoodsList = carVGoodsService.queryList(carGoods1);
            carGoods = carGoodsList.get(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        //如果购物车列表为空
        if(cartList.size() <= 0){
            Cart cart = new Cart();
            cart.setNum(1);
            cart.setPrice(carGoods.getPrice().intValue());
            cart.setId(carGoods.getId());
            cart.setType(carGoods.getType());
            cart.setName(carGoods.getName());
            cart.setProduce(carGoods.getProduce());
            cart.setDescription(carGoods.getDescription());
            //将当前传来的商品添加到购物车列表
            cartList.add(cart);
            if(getCookie(request)==null){
                cookie_2st = new Cookie("cart", URLEncoder.encode(makeCookieValue(cartList),"utf-8"));
                //设置在该项目下都可以访问cookie
                cookie_2st.setPath("/");
                //设置cookie有效时间为30分钟
                cookie_2st.setMaxAge(60*30);
                response.addCookie(cookie_2st);
           }else {
                cookie_2st=getCookie(request);
                //设置在该项目下都可以访问cookie
                cookie_2st.setPath("/");
                //设置cookie有效时间为30分钟
                cookie_2st.setMaxAge(60*30);
                cookie_2st.setValue(URLEncoder.encode(makeCookieValue(cartList)));
                response.addCookie(cookie_2st);
            }
        }else {
            //获取的购物车列表不为空时
            boolean googInCart =false;
            for(Cart cart:cartList){
                if(cart.getId().toString().equals(goodId)){
                    cart.setNum(cart.getNum()+1);
                    googInCart=true;
                    break;
                }
            }
            if(!googInCart){
                Cart cart = new Cart();
                cart.setNum(1);
                cart.setPrice(carGoods.getPrice().intValue());
                cart.setId(carGoods.getId());
                cart.setType(carGoods.getType());
                cart.setName(carGoods.getName());
                cart.setProduce(carGoods.getProduce());
                cart.setDescription(carGoods.getDescription());
                //将当前传来的商品添加到购物车列表
                cartList.add(cart);
            }
            cookie_2st =getCookie(request);
            cookie_2st.setPath("/");
            //设置cookie有效时间为30分钟
            cookie_2st.setMaxAge(60*30);
            cookie_2st.setValue(URLEncoder.encode(makeCookieValue(cartList)));
            response.addCookie(cookie_2st);
        }
        return cartList.toString();
    }

    @RequestMapping("/getCart")
    public ModelAndView getCart(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
        List<Cart> cartList =getCartInCookie(response,request);
        ModelAndView modelAndView= new ModelAndView();
        modelAndView.addObject("list",cartList);
        modelAndView.setViewName("carGoods/carlist");
        return modelAndView;
    }

    /**
     * @Author gzl
     * @Description：查询下单列表
     * @Exception
     * @Date： 2020/4/19 11:59 PM
     */
    @RequestMapping("/queryorders")
    public ModelAndView QueryOrders()  {
        List<CarOrders> list =carVGoodsService.queryOrders();
        /**
         * 模型和视图
         * model模型: 模型对象中存放了返回给页面的数据
         * view视图: 视图对象中指定了返回的页面的位置
         */
        ModelAndView modelAndView = new ModelAndView();
        //将返回给页面的数据放入模型和视图对象中
        modelAndView.addObject("list", list);
        //指定返回的页面位置
        modelAndView.setViewName("carGoods/orderlist");
        return modelAndView;
    }
    /**
     * 下单
     *
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/addOrders")
    public ModelAndView addOrders(HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException {
        List<Cart> cartInCookie = getCartInCookie(response, request);
        carVGoodsService.saveOrders(cartInCookie);
        List<CarGoods> list = new ArrayList<>();
        try {
            CarGoods carGoods = new CarGoods();
            list = carVGoodsService.queryList(carGoods);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ModelAndView modelAndView = new ModelAndView();
        //将返回给页面的数据放入模型和视图对象中
        modelAndView.addObject("list", list);
        //指定返回的页面位置
        modelAndView.setViewName("carGoods/list");
        return modelAndView;

    }

    /**
     * 拆单
     *
     * @param request
     * @param response
     * @param id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("teardowndetails")
    public ModelAndView teardowndetails(HttpServletRequest request, HttpServletResponse response, Integer id)
            throws UnsupportedEncodingException {
        List<CarOrderDetails> carOrderDetails = carVGoodsService.queryOrdersDetails(id);

        carOrderDetails.forEach(carOrderDetail -> {
            TearDownDetails tearDownDetails = carVGoodsService.queryOrdersTearDownDetails(carOrderDetail);
            if (tearDownDetails != null) {
                tearDownDetails.setNum(tearDownDetails.getNum() + carOrderDetail.getNum());
            } else {
                TearDownDetails tearDownDetail = new TearDownDetails();
                tearDownDetail.setNum(carOrderDetail.getNum());
                tearDownDetail.setCargoods_name(carOrderDetail.getGoodsname());
                tearDownDetail.setOrderId(carOrderDetail.getOrderId());
                tearDownDetail.setProduce(carOrderDetail.getProduce());
                carVGoodsService.saveTearDownDetails(tearDownDetail);
            }
        });
        List<CarOrders> list = carVGoodsService.queryOrders();
        ModelAndView modelAndView = new ModelAndView();
        //将返回给页面的数据放入模型和视图对象中
        modelAndView.addObject("list", list);
        //指定返回的页面位置
        modelAndView.setViewName("carGoods/orderlist");
        return modelAndView;
    }

    /**
     * 清空购物车
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("deleteAllCookie")
    public String deleteAllCookie(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        deleteCookie(request, response);
        return "清空成功";
    }

    /**
     * 根据ID删除购物车商品
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("deleteCarGoodsById")
    public ModelAndView deleteCarGoodsById(Integer id, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        deleteCartInCookie(id, request, response);
        List<Cart> cartInCookie = getCartInCookie(response, request);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("list", cartInCookie);
        modelAndView.setViewName("carGoods/carlist");
        return modelAndView;
    }

    /**
     * 根据指定ID删除Cookie
     * @param id
     * @param request
     * @param response
     * @throws IOException
     */
    private void deleteCartInCookie(Integer id, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Cart> cartGoodsList = getCartInCookie(response, request);
        List<Cart> cartList =
                cartGoodsList.stream().filter(cart -> (cart.getId().intValue() != id)).collect(Collectors.toList());
        deleteAllCookie(request,response);
        addCookie(request, response, cartList);
    }

    /**
     * 购物车信息保存至cookie
     *
     * @param request
     * @param response
     * @param cartVos
     * @throws UnsupportedEncodingException
     */
    private void addCookie(HttpServletRequest request, HttpServletResponse response, List<Cart> cartVos) throws UnsupportedEncodingException {
        Cookie cookie = getCookie(request) == null ?
                new Cookie("cart", null) : getCookie(request);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 30);
        cookie.setValue(URLEncoder.encode(makeCookieValue(cartVos), "utf-8"));
        response.addCookie(cookie);
    }
    /**
     * @Author gzl
     * @Description：查询下单详情列表
     * @Exception
     * @Date： 2020/4/19 11:59 PM
     */
    @RequestMapping("/queryordersdetails")
    public ModelAndView QueryOrdersDetails(Integer id)  {
        List<CarOrderDetails> list =carVGoodsService.queryOrdersDetails(id);
        /**
         * 模型和视图
         * model模型: 模型对象中存放了返回给页面的数据
         * view视图: 视图对象中指定了返回的页面的位置
         */
        ModelAndView modelAndView = new ModelAndView();
        //将返回给页面的数据放入模型和视图对象中
        modelAndView.addObject("list", list);
        //指定返回的页面位置
        modelAndView.setViewName("carGoods/orderdetailslist");
        return modelAndView;
    }



    /**
     * 获取cookie中的购物车列表
     *
     * @param response
     * @param request
     * @return 购物车列表
     * @throws UnsupportedEncodingException 抛出异常
     */
    public List<Cart> getCartInCookie(HttpServletResponse response, HttpServletRequest request) throws
            UnsupportedEncodingException {
        // 定义空的购物车列表
        List<Cart> items = new ArrayList<>();
        String value_1st ;
        // 购物cookie
        Cookie cart_cookie = getCookie(request);
        // 判断cookie是否为空
        if (cart_cookie != null) {
            // 获取cookie中String类型的value,从cookie获取购物车
            value_1st = URLDecoder.decode(cart_cookie.getValue(), "utf-8");
            // 判断value是否为空或者""字符串
            if (value_1st != null && !"".equals(value_1st)) {
                // 解析字符串中的数据为对象并封装至list中返回给上一级
                String[] arr_1st = value_1st.split("==");
                for (String value_2st : arr_1st) {
                    String[] arr_2st = value_2st.split("=");
                    Cart item = new Cart();
                    item.setId(Long.parseLong(arr_2st[0])); //商品id
                    item.setType(arr_2st[1]); //商品类型ID
                    item.setName(arr_2st[2]); //商品名
                    item.setDescription(arr_2st[4]);//商品详情
                    item.setPrice(Integer.parseInt(arr_2st[3])); //商品市场价格
                    item.setNum(Integer.parseInt(arr_2st[5]));//加入购物车数量
                    item.setProduce(arr_2st[6]);//加入供应商
                    items.add(item);
                }
            }
        }
        return items;

    }
    /**
     * 删除所有Cookie
     * @param request
     * @param response
     */
    public void deleteCookie(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    /**
     * 获取名为"cart"的cookie
     *
     * @param request
     * @return cookie
     */
    public Cookie getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Cookie cart_cookie = null;
        for (Cookie cookie : cookies) {
            //获取购物车cookie
            if ("cart".equals(cookie.getName())) {
                cart_cookie = cookie;
            }
        }
        return cart_cookie;
    }

    /**
     * 制作cookie所需value
     *
     * @param cartVos 购物车列表
     * @return 解析为字符串的购物车列表，属性间使用"="相隔，对象间使用"=="相隔
     */
    public String makeCookieValue(List<Cart> cartVos) {
        StringBuffer buffer_2st = new StringBuffer();
        for (Cart item : cartVos) {
            buffer_2st.append(item.getId() + "=" + item.getType() + "=" + item.getName() + "="
                    + item.getPrice() + "=" + item.getDescription() + "=" + item.getNum() + "==");
        }
        return buffer_2st.toString().substring(0, buffer_2st.toString().length() - 2);
    }
    /**
     * 获取商品销售统计（柱状图）
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getCarGoodsSale", method = RequestMethod.POST)
    public Map<String,Object> getCarGoodsSale(HttpServletRequest request, HttpServletResponse response){
        return carVGoodsService.statisticCarOrderDetails();
    }

    /**
     * 获取商品销售统计（饼状图）
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getCarGoodsSaleByPie", method = RequestMethod.POST)
    public Map<String,Object> getCarGoodsSaleByPie(HttpServletRequest request, HttpServletResponse response){
        return carVGoodsService.statisticCarOrderDetailsByPie();
    }

}
