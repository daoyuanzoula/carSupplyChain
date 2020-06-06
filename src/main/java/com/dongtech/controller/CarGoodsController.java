package com.dongtech.controller;

import com.dongtech.service.CarVGoodsService;
import com.dongtech.vo.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gzl
 * @date 2020-04-15
 * @program: springboot-jsp
 * @description: ${description}
 */
@Controller
@RequestMapping("cargoods")
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
//        System.out.println(list.size());
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
     * @Author gzl
     * @Description：查询下单详情列表
     * @Exception
     * @Date： 2020/4/19 11:59 PM
     */
    @RequestMapping("/queryordersdetails")
    public ModelAndView QueryOrdersDetails(String id)  {
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
     * 添加商品到购物车列表
     * @param goodsId 商品ID
     * @param request
     * @param response
     * @throws UnsupportedEncodingException 异常抛出
     */
    @RequestMapping("/addGoodsToCart")
    @ResponseBody
    public String addGoodsToCart(Integer goodsId,HttpServletRequest request,
            HttpServletResponse response) throws UnsupportedEncodingException {
        //从cookie中获取购物车列表

        List<Cart> cartVos = getCartInCookie(response, request);
        Cookie cookie_2st;
        CarGoods carGoods = new CarGoods();
        try {
            CarGoods carGoods1 = new CarGoods();
            carGoods1.setId(Long.parseLong(goodsId + ""));
            List<CarGoods> cList = carVGoodsService.queryList(carGoods1);
            carGoods = cList.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //如果购物车列表为空
        if (cartVos.size() <= 0) {
            //TODO 根据商品ID获取商品信息
            //测试用，实际应当根据id获取
            Cart cartVo = new Cart();
            cartVo.setNum(1);
            cartVo.setPrice(carGoods.getPrice().intValue());
            cartVo.setId(carGoods.getId());
            cartVo.setType(carGoods.getType());
            cartVo.setName(carGoods.getName());
            cartVo.setProduce(carGoods.getProduce());
            cartVo.setDescription(carGoods.getDescription());

            //将当前传来的商品添加到购物车列表
            cartVos.add(cartVo);
            if (getCookie(request) == null) {
                //制作购物车cookie数据
                cookie_2st = new Cookie("cart", URLEncoder.encode(makeCookieValue(cartVos)));
                //设置在该项目下都可以访问该cookie
                cookie_2st.setPath("/");
                //设置cookie有效时间为30分钟
                cookie_2st.setMaxAge(60 * 30);
                //添加coolie
                response.addCookie(cookie_2st);
            } else {
                cookie_2st = getCookie(request);
                //设置在该项目下都可以访问该cookie
                cookie_2st.setPath("/");
                //设置cookie有效时间为30分钟
                cookie_2st.setMaxAge(60 * 30);
                cookie_2st.setValue(URLEncoder.encode(makeCookieValue(cartVos)));
                //添加cookie
                response.addCookie(cookie_2st);
            }
        }
        //当获取的购物车列表不为空时
        else {
            int bj = 0;
            for (Cart cart : cartVos) {
                //如果购物车中该商品则数量+1
                if (cart.getId().equals(goodsId)) {
                    cart.setNum(cart.getNum() + 1);
                    bj = 1;
                    break;
                }
            }
            if (bj == 0) {
                //TODO 根据商品ID获取商品信息
                //测试用。实际应当根据id获取
                Cart cartVo = new Cart();
                cartVo.setNum(carGoods.getNum());
                cartVo.setPrice(carGoods.getPrice().intValue());
                cartVo.setId(carGoods.getId());
                cartVo.setType(carGoods.getType());
                cartVo.setName(carGoods.getName());
                cartVo.setProduce(carGoods.getProduce());
                cartVo.setDescription(carGoods.getDescription());

                //将当前传来的商品添加到购物车列表
                cartVos.add(cartVo);
            }
            //获取名为“cart”的cookie
            cookie_2st = getCookie(request);
            //设置在该项目下都可以访问该cookie
            cookie_2st.setPath("/");
            //设置cookie有效时间为30分钟
            cookie_2st.setMaxAge(60 * 30);
            //设置value
            cookie_2st.setValue(URLEncoder.encode(makeCookieValue(cartVos)));
            //添加cookie
            response.addCookie(cookie_2st);
        }

        return  cartVos.toString();

    }

    /**
     * 获取购物车列表
     * @param request
     * @param response
     * @return 购物车列表
     * @throws UnsupportedEncodingException 异常抛出
     */
    @RequestMapping("/getCart")
    public ModelAndView getCart(HttpServletRequest request,HttpServletResponse response) throws
            UnsupportedEncodingException {
        List<Cart> cartInCookie = getCartInCookie(response,request);

        ModelAndView modelAndView = new ModelAndView();
        //将返回页面的数据放入模型和视图对象中
        modelAndView.addObject("list",cartInCookie);
        //指定返回的页面位置
        modelAndView.setViewName("carGoods/carlist");
        return modelAndView;
    }

    /**
     * 获取购物车列表
     * @Author gZJ
     * @Description 下单
     * @Exception: 购物车列表
     * @Date 2020/6/4
     */
    @RequestMapping("/addorders")
    public ModelAndView addOrders(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
        List<Cart> cartInCookie = getCartInCookie(response,request);
        carVGoodsService.saveOrders(cartInCookie);
        /**
         * 模型和视图
         * mode模型：模型对象中存放了返回给页面的数据
         * view视图：视图对象中指定了返回的页面的位置
         */
        ModelAndView modelAndView = new ModelAndView();
        //将放回给页面的数据放入模型和视图对象中
        List<CarGoods> list = new ArrayList<>();
        try {
            CarGoods carGoods = new CarGoods();
            list = carVGoodsService.queryList(carGoods);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        modelAndView.addObject("list",list);
        //指定返回的页面位置
        modelAndView.setViewName("carGoods/list");
        return modelAndView;
    }

    /**
     * @Author gZJ
     * @Description 拆单
     * @Exception
     * @Date 2020/6/4 23：23 PM
     */
    @RequestMapping("/teardowndetails")
    public ModelAndView tearDownDetails(HttpServletRequest request, HttpServletResponse response, String orderId) throws UnsupportedEncodingException {
        List<Cart> cartInCookie = getCartInCookie(response,request);
        List<CarOrderDetails> list = carVGoodsService.queryOrdersDetails(orderId);
        //筛选出不同的
        for (CarOrderDetails c:list) {
            TearDownDetails tdd = carVGoodsService.queryOrderTearsDetails(c);
            if (tdd !=null) {
                tdd.setNum(tdd.getNum() + c.getNum());
            } else {
                TearDownDetails t = new TearDownDetails();
                t.setNum(c.getNum());
                t.setCargoods_name(c.getGoodsname());
                t.setOrderId(c.getOrderId());
                t.setProduce(c.getProduce());
                carVGoodsService.saveTearDownDetails(t);
            }
        }
        List<TearDownDetails> list1 = carVGoodsService.queryAllOrderTearsDetails(orderId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("list",list1);
        //指定返回的页面位置
        modelAndView.setViewName("carGoods/teardowndetailslist");
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
                    item.setProduce(arr_2st[6]);//加入购物车数量
                    items.add(item);
                }
            }
        }
        return items;

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
                    + item.getPrice() + "=" + item.getDescription() + "=" + item.getNum() + "=" + item.getProduce() + "==");
        }
        return buffer_2st.toString().substring(0, buffer_2st.toString().length() - 2);
    }

    /**
     * 清空购物车,删除所有cookie
     * Author:REE
     */
    @RequestMapping("/deleteAllCookie")
    public  void deleteAllCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();//获取cookie数组

        for (Cookie c : cookies   //遍历cookie
        ) {
            if (c.getName().equals("cart")) {
                Cookie cookie = new Cookie(c.getName(), null);//删除前必须要new 一个value为空；
                cookie.setPath("/");//路径要相同
                cookie.setMaxAge(0);//生命周期设置为0
                response.addCookie(cookie);
                break;
            }
        }
    }

    /**
     * 更新cookie，删除购物车一条数据
     * Author:REE
     */
    @RequestMapping("/deleteOneCookie")
    public  void deleteAllCookie(HttpServletRequest request, HttpServletResponse response,Long Id) throws UnsupportedEncodingException {
        List<Cart> cartInCookie = getCartInCookie(response,request);
        Cookie cookie_2st ;
        for (Cart cartIn:cartInCookie
             ) {
            if (cartIn.getId() == Id) {
                int i = cartInCookie.indexOf(cartIn);
                cartInCookie.remove(i);
            }
        }
        //获取购物车cookie
        cookie_2st = getCookie(request);
        //设置在该项目下都可以访问该cookie
        cookie_2st.setPath("/");
        //设置cookie有效时间为30分钟
        cookie_2st.setMaxAge(60 * 30);
        cookie_2st.setValue(URLEncoder.encode(makeCookieValue(cartInCookie)));
        //添加cookie
        response.addCookie(cookie_2st);

    }

}

