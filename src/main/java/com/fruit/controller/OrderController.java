package com.fruit.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fruit.bean.*;
import com.fruit.dao.OrderConfirmCodeMapper;
import com.fruit.dao.OrderProductMapper;
import com.fruit.dao.OrdersMapper;
import com.fruit.dao.ProductMapper;
import com.fruit.util.StringFormatUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("order")
public class OrderController extends AbsController {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderProductMapper orderProductMapper;
    @Autowired
    private OrderConfirmCodeMapper orderConfirmCodeMapper;

    /**
     * 添加订单
     */
    @PostMapping("add")
    public void insertOrder(@RequestParam Map param) {
        int insert = 0;
        String order_id = null;
        int verification_code = -1;
        int total_count = 0;
        float total_price = 0;
        try {
            order_id = UUID.randomUUID().toString();
            String account_id = MapUtils.getString(param, "account_id", "");
            if (account_id == null || account_id.equals("")) {
                return;
            }
            String data = MapUtils.getString(param, "data", "");
            JSONArray array = JSON.parseArray(data);
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String product_id = object.getString("product_id");
                int count = object.getIntValue("product_count");
                Product product = productMapper.selectByPrimaryKey(product_id);
                if (product == null) {
                    return;
                }
                OrderProduct op = new OrderProduct();
                op.setOrderId(order_id);//.set("order_id", order_id);
                op.setProductId(product_id);//.set("product_id", product_id);
                op.setCount(count);//.set("count", count);
                orderProductMapper.insertSelective(op);
                total_count += count;
                total_price += product.getPrice() * count;
            }
            verification_code = (int) ((Math.random() * 9 + 1) * 100000);
            OrderConfirmCode occ = new OrderConfirmCode();
            occ.setOrderId(order_id);//.set("order_id", order_id);
            occ.setAccountId(account_id);//.set("account_id", account_id);
            occ.setVerificationCode("" + verification_code);//.set("verification_code", "" + verification_code);
            occ.setTime(System.currentTimeMillis());//.set("time", "" + System.currentTimeMillis());
            int occ_insert = orderConfirmCodeMapper.insertSelective(occ);
            if (occ_insert <= 0) {
                return;
            }
            Orders orders = new Orders();
            orders.setOrderId(order_id);
            orders.setAccountId(account_id);
            orders.setTotalPrice(total_price);
            orders.setTotalCount(Float.valueOf(total_count));
            orders.setStatus(1);
            orders.setProductIds(data);
            insert = ordersMapper.insertSelective(orders);
            Map<String, String> dataMap = new HashMap<>();
            String result = "订单添加失败";
            if (insert > 0) {
                result = "订单添加成功";
                dataMap.put("result", result);
                dataMap.put("order_id", order_id);
                dataMap.put("verification_code", "" + verification_code);
                dataMap.put("total_price", "" + total_price);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 确认订单
     */
    @PostMapping("confirm")
    public Object confirmOrder(@RequestParam Map param) {
        boolean confirm = false;
        try {
            String account_id = MapUtils.getString(param, "account_id", "");
            String verification_code = MapUtils.getString(param, "verification_code", "");
            String order_id = MapUtils.getString(param, "order_id", "");
            OrderConfirmCodeExample example = new OrderConfirmCodeExample();
            example.createCriteria().andOrderIdEqualTo(order_id);
            OrderConfirmCode occ = orderConfirmCodeMapper.selectByPrimaryKey(order_id);
            if (occ == null || StringFormatUtil.isEmpty(account_id) || StringFormatUtil.isEmpty(order_id)
                    || StringFormatUtil.isEmpty(verification_code)) {
                return ajax(confirm);
            }
            if (verification_code.equals(occ.getVerificationCode())
                    && account_id.equals(occ.getAccountId())) {
                confirm = true;
            }
            return ajax(confirm);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ajax(confirm);
        } catch (Exception e) {
            e.printStackTrace();
            return ajax(confirm);
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("cancle")
    public void cancleOrder(@RequestParam Map param) {
        boolean cancle = true;
        try {
            String account_id = MapUtils.getString(param, "account_id", "");
            String order_id = MapUtils.getString(param, "order_id", "");
            int delOrder = ordersMapper.deleteByPrimaryKey(order_id);
            if (delOrder <= 0)
                throw new RuntimeException("取消订单失败");
            int delOcc = orderConfirmCodeMapper.deleteByPrimaryKey(order_id);
            if (delOcc <= 0)
                throw new RuntimeException("取消订单失败");
            OrderProductExample example = new OrderProductExample();
            example.createCriteria().andOrderIdEqualTo(order_id);
            List<OrderProduct> list = orderProductMapper.selectByExample(example);
            for (OrderProduct orderProduct : list) {
                int delOp = orderProductMapper.deleteByPrimaryKey(orderProduct.getId());
                if (delOp <= 0)
                    throw new RuntimeException("取消订单失败");
            }
            ajax();
        } catch (RuntimeException e) {
            e.printStackTrace();
            ajax(e);
        } catch (Exception e) {
            e.printStackTrace();
            ajax(e);
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("my_orders")
    public Object myAllOrder(@RequestParam Map param) {
        List<Orders> list = null;
        try {
//            String account_id = MapUtils.getString(param, "account_id", "");
            String account_id = "weikong555@163.com";
            OrdersExample example = new OrdersExample();
            example.createCriteria().andAccountIdEqualTo(account_id);
            list = ordersMapper.selectByExample(example);
            return ajax(list);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ajax(e);
        } catch (Exception e) {
            e.printStackTrace();
            return ajax(e);
        }
    }

}
