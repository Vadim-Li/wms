package com.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Goods;
import com.wms.entity.Goodstype;
import com.wms.entity.Storage;
import com.wms.entity.User;
import com.wms.service.GoodsService;
import com.wms.service.GoodstypeService;
import com.wms.service.StorageService;
import com.wms.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/echarts")
public class EchartsController {
    @Resource
    private GoodsService goodsService;
    @Resource
    private UserService userService;
    @Resource
    private StorageService storageService;
    @Resource
    private GoodstypeService goodstypeService;


    private String getRoleName(int roleId) {
        switch (roleId) {
            case 0:
                return "超级管理员";
            case 1:
                return "管理员";
            case 2:
                return "普通账号";
            default:
                return "未知";
        }
    }


    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String name = (String)param.get("name");
        String goodstype = (String)param.get("goodstype");
        String storage = (String)param.get("storage");

        Page<Goods> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Goods> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(StringUtils.isNotBlank(name) && !"null".equals(name)){
            lambdaQueryWrapper.like(Goods::getName,name);
        }
        if(StringUtils.isNotBlank(goodstype) && !"null".equals(goodstype)){
            lambdaQueryWrapper.eq(Goods::getGoodstype,goodstype);
        }
        if(StringUtils.isNotBlank(storage) && !"null".equals(storage)){
            lambdaQueryWrapper.eq(Goods::getStorage,storage);
        }

        IPage result = goodsService.pageCC(page,lambdaQueryWrapper);
        return Result.suc(result.getRecords(),result.getTotal());
    }

    @GetMapping("/list")
    public Map<String, Integer> list() {
        // 定义 wrapper 条件构造器
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // 添加查询条件，根据 sex 字段分组，并统计数量
        wrapper.select("sex", "count(*) as count").groupBy("sex");
        // 执行查询，返回结果集
        List<Map<String, Object>> result = userService.listMaps(wrapper);

        // 定义存储结果的 Map 对象
        Map<String, Integer> data = new HashMap<>();
        for (Map<String, Object> map : result) {
            // 将 sex 值转换成文字形式
            String sex = (Integer)map.get("sex") == 1 ? "男" : "女";
            // 获取数量值，并存入 Map 对象中
            int count = ((Long) map.get("count")).intValue();
            data.put(sex, count);
        }
        return data;
    }


    @GetMapping("/roleCount")
    public Map<String, Integer> getRoleCount() {
        List<Map<String, Object>> listMap = userService.getBaseMapper().selectMaps(new QueryWrapper<User>()
                .select("count(*) as count", "role_id")
                .groupBy("role_id")
        );
        Map<String, Integer> resultMap = new HashMap<>();
        for (Map<String, Object> map : listMap) {
            int roleId = ((Integer) map.get("role_id")).intValue();
            String roleName = getRoleName(roleId);
            int count = ((Long) map.get("count")).intValue();
            resultMap.put(roleName, count);
        }
        return resultMap;
    }

    @GetMapping("/countByStorage")
    public List<Map<String, Object>> countByStorage() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Goods> goodsList = goodsService.list();
        Map<Integer, Integer> storageCountMap = new HashMap<>();
        for (Goods goods : goodsList) {
            Integer storage = goods.getStorage();
            if (!storageCountMap.containsKey(storage)) {
                storageCountMap.put(storage, 0);
            }
            storageCountMap.put(storage, storageCountMap.get(storage) + goods.getCount());
        }
        List<Storage> storageList = storageService.list();
        for (Storage storage : storageList) {
            Integer storageId = storage.getId();
            String storageName = storage.getName();
            Integer totalCount = storageCountMap.getOrDefault(storageId, 0);
            Map<String, Object> map = new HashMap<>();
            map.put("name", storageName);
            map.put("totalCount", totalCount);
            result.add(map);
        }
        return result;
    }

    @GetMapping("/countByGoodsType")
    public List<Map<String, Object>> countByGoodsType() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Goods> goodsList = goodsService.list();
        Map<Integer, Integer> goodsTypeCountMap = new HashMap<>();
        for (Goods goods : goodsList) {
            Integer goodsType = goods.getGoodstype();
            if (!goodsTypeCountMap.containsKey(goodsType)) {
                goodsTypeCountMap.put(goodsType, 0);
            }
            goodsTypeCountMap.put(goodsType, goodsTypeCountMap.get(goodsType) + goods.getCount());
        }
        List<Goodstype> goodsTypeList = goodstypeService.list();
        for (Goodstype goodsType : goodsTypeList) {
            Integer goodsTypeId = goodsType.getId();
            String goodsTypeName = goodsType.getName();
            Integer totalCount = goodsTypeCountMap.getOrDefault(goodsTypeId, 0);
            Map<String, Object> map = new HashMap<>();
            map.put("name", goodsTypeName);
            map.put("totalCount", totalCount);
            result.add(map);
        }
        return result;
    }


}
