package com.dongtech.dao.impl;


import com.dongtech.dao.CarGoodsDao;
import com.dongtech.util.JDBCUtil;
import com.dongtech.vo.*;
import org.springframework.util.StringUtils;
import com.mysql.jdbc.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<CarOrderDetails> queryOrdersDetails(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CarOrderDetails> carOrderDetailsList = new ArrayList<CarOrderDetails>();
        try {
            //1 加载数据库驱动  2 获取数据库连接
            conn = JDBCUtil.getMysqlConn();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM car_orders_details where 1=1");
            if(!StringUtils.isEmpty(id)){
                sql.append(" and order_id =").append(id);
            }
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
                        rs.getInt("order_id")

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
    public int saveOrders(CarOrders carOrders) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //建立连接
            conn = JDBCUtil.getMysqlConn();
            //创建语句
            String sql = "insert into car_orders(number,price) values (?, ?) ";
            //通过传入第二个参数,就会产生主键返回给我们
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, carOrders.getNumber());
            ps.setBigDecimal(2, carOrders.getPrice());
            ps.executeUpdate();

            //返回的结果集中包含主键,注意：主键还可以是UUID,
            //复合主键等,所以这里不是直接返回一个整型
            rs = ps.getGeneratedKeys();
            int id = 0;
            if (rs.next()) {
                id = rs.getInt(1);
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //5 关闭连接
            JDBCUtil.close(rs, ps, conn);
        }
        return -1;
    }

    @Override
    public void batchSaveOrderDetails(List<CarOrderDetails> carOrderDetails) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //建立连接
            conn = JDBCUtil.getMysqlConn();
            // 关闭自动提交，即开启事务
            conn.setAutoCommit(false);
            String sql = "insert into car_orders_details(goods_name, num,produce,price,order_id) values (?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            for (CarOrderDetails carOrderDetail :carOrderDetails) {
                ps.setString(1, carOrderDetail.getGoodsname());
                ps.setInt(2, carOrderDetail.getNum());
                ps.setString(3, carOrderDetail.getProduce());
                ps.setBigDecimal(4, carOrderDetail.getPrice());
                ps.setInt(5, carOrderDetail.getOrderId());
                // 添加批处理SQL
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //5 关闭连接
            JDBCUtil.close(rs, ps, conn);
        }
    }

    @Override
    public List<TearDownDetails> queryOrdersTearDownDetails(CarOrderDetails carOrderDetail) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TearDownDetails> tearDownDetailsList = new ArrayList<>();
        try {
            //1 加载数据库驱动  2 获取数据库连接
            conn = JDBCUtil.getMysqlConn();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM tear_down_details where 1=1");
            if (!StringUtils.isEmpty(carOrderDetail.getProduce())) {
                sql.append(" and produce ='").append(carOrderDetail.getProduce()).append("'");
            }
            if (!StringUtils.isEmpty(carOrderDetail.getOrderId())) {
                sql.append("  and order_id = ").append(carOrderDetail.getOrderId());
            }
            if (!StringUtils.isEmpty(carOrderDetail.getGoodsname())) {
                sql.append("  and cargoods_name='").append(carOrderDetail.getGoodsname()).append("'");
            }
            //3 操作数据库——查询一条数据记录
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            //4 处理返回数据——将返回的一条记录封装到一个JavaBean对象
            while (rs.next()) {
                TearDownDetails vo = new TearDownDetails(rs.getLong("id"),
                        rs.getInt("order_id"),
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

    @Override
    public void saveTearDownDetails(TearDownDetails tearDownDetail) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //建立连接
            conn = JDBCUtil.getMysqlConn();
            //创建语句
            String sql = "insert into tear_down_details(order_id,produce,cargoods_name,num) values (?, ?,?,?) ";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tearDownDetail.getOrderId());
            ps.setString(2, tearDownDetail.getProduce());
            ps.setString(3, tearDownDetail.getCargoods_name());
            ps.setInt(4, tearDownDetail.getNum());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //5 关闭连接
            JDBCUtil.close(rs, ps, conn);
        }
    }

    @Override
    public Map<String, Integer> statisticCarOrderDetails() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Integer> saleMap = new HashMap<>();
        try {
            //1 加载数据库驱动  2 获取数据库连接
            conn = JDBCUtil.getMysqlConn();
            StringBuffer sql = new StringBuffer();
            sql.append("select c.goods_name, SUM(c.num) as num from car_orders_details c GROUP by c.goods_name");
            //3 操作数据库——查询一条数据记录
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            //4 处理返回数据——将返回的一条记录封装到一个JavaBean对象
            while (rs.next()) {
                saleMap.put(rs.getString("goods_name"),rs.getInt("num"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5 关闭连接
            JDBCUtil.close(rs, ps, conn);
        }
        return saleMap;
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
