package com.dongtech.dao.impl;


import com.dongtech.dao.CarGoodsDao;
import com.dongtech.util.JDBCUtil;
import com.dongtech.vo.*;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据层，只负责与数据库的数据交互，将数据进行存储读取操作
 */
public class CarGoodsDaoImpl implements CarGoodsDao {


    @Override
    public List<CarGoods> queryList(CarGoods carGoods) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CarGoods> bookList = new ArrayList<CarGoods>();
        try {
            //1 加载数据库驱动  2 获取数据库连接
            conn = JDBCUtil.getMysqlConn();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM cargoods where 1=1");
            if(!StringUtils.isEmpty(carGoods.getId())){
                sql.append(" and id =").append(carGoods.getId());
            }
            if(!StringUtils.isEmpty(carGoods.getName())){
                sql.append("  and name like '%").append(carGoods.getName()).append("%'");
            }
            if(!StringUtils.isEmpty(carGoods.getType())){
                sql.append("  and type='").append(carGoods.getType()).append("'");
            }
            //3 操作数据库——查询一条数据记录
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            //4 处理返回数据——将返回的一条记录封装到一个JavaBean对象
            while (rs.next()) {
                CarGoods vo = new CarGoods(rs.getLong("id"),
                        rs.getString("number"),
                        rs.getString("name"),
                        rs.getString("produce"),
                        rs.getBigDecimal("price"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getInt("num")

                );
                bookList.add(vo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5 关闭连接
            JDBCUtil.close(rs, ps, conn);
        }
        return bookList;
    }

    /**
     * @Author gzl
     * @Description：查询订单信息
     * @Exception
     * @Date： 2020/4/20 12:04 AM
     */
    @Override
    public List<CarOrders> queryOrders() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CarOrders> carOrdersList = new ArrayList<CarOrders>();
        try {
            //1 加载数据库驱动  2 获取数据库连接
            conn = JDBCUtil.getMysqlConn();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM car_orders where 1=1");
            //3 操作数据库——查询一条数据记录
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            //4 处理返回数据——将返回的一条记录封装到一个JavaBean对象
            while (rs.next()) {
                CarOrders vo = new CarOrders(rs.getLong("id"),
                        rs.getString("number"),
                        rs.getBigDecimal("price")

                );
                carOrdersList.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5 关闭连接
            JDBCUtil.close(rs, ps, conn);
        }
        return carOrdersList;
    }

    /**
     * @Author gzl
     * @Description：查询订单详情
     * @Exception
     * @Date： 2020/4/20 12:17 AM
     */
    @Override
    public List<CarOrderDetails> queryOrdersDetails(String id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        System.out.println(id);

        List<CarOrderDetails> carOrderDetailsList = new ArrayList<CarOrderDetails>();
        try {
            //1 加载数据库驱动  2 获取数据库连接
            conn = JDBCUtil.getMysqlConn();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM car_orders_details where 1=1");
            if(!StringUtils.isEmpty(id)){
                sql.append(" and order_id =").append("'" + id + "'");
            }
            System.out.println(sql);
            //3 操作数据库——查询一条数据记录
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            //4 处理返回数据——将返回的一条记录封装到一个JavaBean对象
            while (rs.next()) {
                CarOrderDetails vo = new CarOrderDetails(rs.getLong("id"),
                        rs.getString("goods_name"),
                        rs.getInt("num"),
                        rs.getString("produce"),
                        rs.getBigDecimal("price"),
                        rs.getString("order_id")

                );
                carOrderDetailsList.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5 关闭连接
            JDBCUtil.close(rs, ps, conn);
        }
        return carOrderDetailsList;
    }

    @Override
    public void saveOrders(List<Cart> cartInCookie) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int price =0;
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String number= dateFormat.format(date);

        for (Cart cartIn:cartInCookie) {

            price = price + cartIn.getPrice();

            try {
                //插入到工单详情表
                //1 加载数据库驱动  2 获取数据库连接
                conn = JDBCUtil.getMysqlConn();
                String sql = "INSERT INTO jk_pro_db.car_orders_details(goods_name,num,produce,price,order_id) values (?,?,?,?,?)";
                ps = conn.prepareStatement(sql);
                //ps.setInt(1,id0.intValue());
                ps.setString(1, cartIn.getName());
                ps.setInt(2, cartIn.getNum());
                ps.setString(3, cartIn.getProduce());
                ps.setBigDecimal(4, BigDecimal.valueOf(cartIn.getPrice()));
                ps.setString(5, number);

                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                //5 关闭连接
                JDBCUtil.close(rs, ps, conn);
            }
        }
        insertOrders(number, price);

    }

    @Override
    public TearDownDetails queryOrderTearsDetails(CarOrderDetails c) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<TearDownDetails> tearDownDetailsList = new ArrayList<TearDownDetails>();
        try {
            //1 加载数据库驱动  2 获取数据库连接
            conn = JDBCUtil.getMysqlConn();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM tear_down_details where 1=1");
            if(!StringUtils.isEmpty(c.getOrderId())){
                sql.append(" and order_id =").append("'" + c.getOrderId() + "'");
            }

            //3 操作数据库——查询一条数据记录
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            //4 处理返回数据——将返回的一条记录封装到一个JavaBean对象
            while (rs.next()) {
                TearDownDetails vo = new TearDownDetails(rs.getLong("id"),
                        rs.getString("order_id"),
                        rs.getString("produce"),
                        rs.getString("cargoods_name"),
                        rs.getInt("num")
                );
                tearDownDetailsList.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5 关闭连接
            JDBCUtil.close(rs, ps, conn);
        }

        if (tearDownDetailsList.size() == 0 || tearDownDetailsList.isEmpty()) {
            return null;
        } else {
            return tearDownDetailsList.get(0);
        }

    }

    @Override
    public void saveTearDownDetails(TearDownDetails t) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //插入到工单表
            //1 加载数据库驱动  2 获取数据库连接
            conn = JDBCUtil.getMysqlConn();
            String sql = "INSERT INTO jk_pro_db.tear_down_details(order_id,produce,cargoods_name,num) values (?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getOrderId());
            ps.setString(2, t.getProduce());
            ps.setString(3, t.getCargoods_name());
            ps.setInt(4, t.getNum());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //5 关闭连接
            JDBCUtil.close(rs, ps, conn);
        }
    }

    @Override
    public List<TearDownDetails> queryAllOrderTearsDetails(String order_id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<TearDownDetails> tearDownDetailsList = new ArrayList<TearDownDetails>();
        try {
            //1 加载数据库驱动  2 获取数据库连接
            conn = JDBCUtil.getMysqlConn();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM tear_down_details where 1=1");
            if(StringUtils.isEmpty(order_id) ){
                sql.append(" and order_id =").append("'" + order_id + "'");
            }

            //3 操作数据库——查询一条数据记录
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            //4 处理返回数据——将返回的一条记录封装到一个JavaBean对象
            while (rs.next()) {
                TearDownDetails vo = new TearDownDetails(rs.getLong("id"),
                        rs.getString("order_id"),
                        rs.getString("produce"),
                        rs.getString("cargoods_name"),
                        rs.getInt("num")
                );
                tearDownDetailsList.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5 关闭连接
            JDBCUtil.close(rs, ps, conn);
        }

        return tearDownDetailsList;
    }

    public void insertOrders(String number, int price) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //插入到工单表
            //1 加载数据库驱动  2 获取数据库连接
            conn = JDBCUtil.getMysqlConn();
            String sql = "INSERT INTO jk_pro_db.car_orders(number,price) values (?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, number);
            ps.setBigDecimal(2, BigDecimal.valueOf(price));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //5 关闭连接
            JDBCUtil.close(rs, ps, conn);
        }

    }

    public void saveOrdersDetails(String goods_name,int num,String produce ,int order_id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //1 加载数据库驱动  2 获取数据库连接
            conn = JDBCUtil.getMysqlConn();
            final int[] totalprice = {0};
                String sql = "INSERT INTO jk_pro_db.car_orders_details(goods_name, num,produce,order_id) values (?,?,?,?)";
                ps = conn.prepareStatement(sql);
                long randomNum = System.currentTimeMillis();
                ps.setString(1, goods_name);
                ps.setInt(2,num);
                ps.setString(3, produce);
                ps.setInt(4,order_id);
                ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //5 关闭连接
            JDBCUtil.close(rs, ps, conn);
        }
    }

}
