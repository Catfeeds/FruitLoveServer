package com.fruit.controller;

import com.alibaba.druid.util.StringUtils;
import com.fruit.bean.Address;
import com.fruit.bean.AddressExample;
import com.fruit.dao.AddressMapper;
import com.fruit.util.StringFormatUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("address")
public class AddressController extends AbsController {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 添加地址
     */
    @PostMapping("add")
    public void insertAddress(@RequestParam Map param) {
        Address address = new Address();
        try {
            String account_id = MapUtils.getString(param, "account_id", "");
            String name = MapUtils.getString(param, "name", "");
            String phone = MapUtils.getString(param, "phone", "");
            String area = MapUtils.getString(param, "area", "");
            String detail_address = MapUtils.getString(param, "detail_address", "");
            if (StringFormatUtil.isEmpty(name) || StringFormatUtil.isEmpty(phone) || StringFormatUtil.isEmpty(area)
                    || StringFormatUtil.isEmpty(detail_address) || StringFormatUtil.isEmpty(account_id))
                ajax(-1, "数据不完整");
            address.setAccountId(account_id);
            address.setName(name);
            address.setPhone(phone);
            address.setArea(area);
            address.setAddress(detail_address);
            long create_time = System.currentTimeMillis();
            address.setCreateTime(create_time);
            address.setUpdateTime(create_time);
            addressMapper.insertSelective(address);
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
     * 更新地址
     */
    @PostMapping("/update")
    public void updateAddress(@RequestParam Map param) {
        Address address = new Address();
        try {
            String id = MapUtils.getString(param, "id", "");
            String account_id = MapUtils.getString(param, "account_id", "");
            String name = MapUtils.getString(param, "name", "");
            String phone = MapUtils.getString(param, "phone", "");
            String area = MapUtils.getString(param, "area", "");
            String detail_address = MapUtils.getString(param, "detail_address", "");
            if (StringFormatUtil.isEmpty(name) || StringFormatUtil.isEmpty(phone) || StringFormatUtil.isEmpty(area)
                    || StringFormatUtil.isEmpty(detail_address) || StringFormatUtil.isEmpty(account_id))
                ajax(-1, "数据不完整");
            address.setAccountId(account_id);
            address.setName(name);
            address.setPhone(phone);
            address.setArea(area);
            address.setAddress(detail_address);
            address.setUpdateTime(System.currentTimeMillis());
            addressMapper.updateByPrimaryKeySelective(address);
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
     * 获取地址
     */
    @GetMapping("/get")
    public Object getAddresses(@RequestParam Map param) {
        List<Address> list = null;
        try {
            String account_id = MapUtils.getString(param, "account_id", "");
            if (StringUtils.isEmpty(account_id))
                return "";
            AddressExample example = new AddressExample();
            example.createCriteria().andAccountIdEqualTo(account_id);
            list = addressMapper.selectByExample(example);
            return ajax(list);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ajax(-1, e != null ? e.getMessage() : "error");
        } catch (Exception e) {
            e.printStackTrace();
            return ajax(-1, e != null ? e.getMessage() : "error");
        }
    }
}
