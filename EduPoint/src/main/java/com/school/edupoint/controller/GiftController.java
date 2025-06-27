package com.school.edupoint.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.edupoint.mapper.Gift1Mapper;
import com.school.edupoint.model.Gift;
import com.school.edupoint.model.StudentGift;
import com.school.edupoint.response.Result;
import com.school.edupoint.service.GiftService;
import com.school.edupoint.service.StudentGiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/gift")
public class GiftController {

    @Autowired
    private GiftService giftService;

    @Autowired
    private Gift1Mapper giftMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StudentGiftService studentGiftService;

    @GetMapping
    public Result<List<Gift>> getAll() {
        return Result.success(giftMapper.selectList(null));
    }


    /**
     * 分页查询礼物列表
     */
    @PostMapping("/page")
    public Result<IPage<Gift>> getPage(@RequestBody Map<String, Object> param) {
        Integer pageNum = (Integer) param.getOrDefault("pageNum", 1);
        Integer pageSize = (Integer) param.getOrDefault("pageSize", 10);
        String name = (String) param.get("name");

        Page<Gift> page = new Page<>(pageNum, pageSize);
        return Result.success(giftService.page(page, new LambdaQueryWrapper<Gift>()
                .like(name != null && !name.isEmpty(), Gift::getName, name)));
    }

    /**
     * 获取礼物详情
     */
    @GetMapping("/{id}")
    public Result<Gift> getById(@PathVariable Integer id) {
        return Result.success(giftMapper.getById(id));
    }


    /**
     * 获取礼物详情
     */
    @PostMapping("/get")
    public Result<Gift> getById(@RequestBody Map<String, Object> param) {
        Integer id = (Integer) param.get("id");
        return Result.success(giftService.getById(id));
    }

    /**
     * 新增或更新礼物信息
     */
    @PostMapping("/save")
    public Result<Gift> save(@RequestBody Map<String, Object> rawRequest) {
        System.out.println("原始请求数据：" + rawRequest);

        Gift gift = new Gift();
        gift.setId((Integer) rawRequest.get("id"));
        gift.setName((String) rawRequest.get("name"));
        gift.setPoint((Integer) rawRequest.get("point"));
        gift.setDescription((String) rawRequest.get("description"));
        gift.setStock((Integer) rawRequest.get("stock"));


        // 检查是否传递了新的 imageUrl
        Object imageUrlObj = rawRequest.get("imageUrl");

        if (imageUrlObj != null && !"".equals(imageUrlObj.toString().trim())) {
            gift.setImageUrl(imageUrlObj.toString());
        } else {
            gift.setImageUrl(null); // 明确设为 null
        }

        boolean success = giftService.saveOrUpdate(gift);
        if (success) {
            return Result.success(gift);
        } else {
            return Result.error(500, "保存失败");
        }
    }


    /**
     * 删除礼物
     */
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody Map<String, Integer> param) {
        Integer id = param.get("id");
        giftService.removeById(id);
        return Result.success(null);
    }

    /**
     * 删除礼物
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        giftMapper.removeById(id);
        return Result.success(null);
    }

    /**
     * 检查库存并调用兑换接口
     */
    @PostMapping("/check-stock-and-exchange")
    public Result<?> checkStockAndExchange(@RequestBody Map<String, Object> param) {
        System.out.println("收到的参数: " + param);

        // 尝试从不同参数中获取giftId
        Integer giftId = null;

        // 尝试多种可能的参数名获取giftId
        if (param.containsKey("giftId")) {
            giftId = extractIntegerParam(param, "giftId");
        } else if (param.containsKey("gift_id")) {
            giftId = extractIntegerParam(param, "gift_id");
        } else if (param.containsKey("productId")) {
            giftId = extractIntegerParam(param, "productId");
        } else if (param.containsKey("product_id")) {
            giftId = extractIntegerParam(param, "product_id");
        } else if (param.containsKey("id")) {
            // 也尝试直接从id参数获取，可能是礼品ID
            giftId = extractIntegerParam(param, "id");
        }

        // 打印所有参数，帮助调试
        System.out.println("所有请求参数:");
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        System.out.println("提取到的giftId: " + giftId);

        if (giftId == null) {
            return Result.error(400, "无法获取礼品ID，请在请求中提供giftId参数");
        }

        // 获取礼品信息
        Gift gift = giftService.getById(giftId);
        if (gift == null) {
            return Result.error(404, "礼品不存在，ID: " + giftId);
        }

        // 检查库存
        if (gift.getStock() != null && gift.getStock() > 0) {
            // 先调用兑换接口，成功后再减库存
            try {
                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                String exchangeUrl = baseUrl + "/api/admin/gift/exchange";
                System.out.println("开始调用兑换接口: " + exchangeUrl);

                // 创建带有更长超时时间的RestTemplate
                SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                factory.setConnectTimeout(10000); // 连接超时10秒
                factory.setReadTimeout(30000);    // 读取超时30秒
                RestTemplate extendedTimeoutRestTemplate = new RestTemplate(factory);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // 从请求中获取Authorization头，如果存在的话
                String authorization = (String) param.get("authorization");
                if (authorization != null) {
                    headers.set("Authorization", authorization);
                } else {
                    // 尝试从请求头信息中获取
                    String tokenType = (String) param.get("tokenType");
                    String accessToken = (String) param.get("accessToken");
                    if (tokenType != null && accessToken != null) {
                        headers.set("Authorization", tokenType + " " + accessToken);
                    }
                }

                // 构建兑换接口所需的请求参数
                Map<String, Object> exchangeParams = new HashMap<>();

                // 必填字段：id
                exchangeParams.put("id", giftId);

                // 设置状态为3
                exchangeParams.put("status", 3);

                // 从原始参数中提取其他必要字段
                String receiveName = (String) param.get("receiveName");
                String receivePhone = (String) param.get("receivePhone");
                String address = (String) param.get("address");

                // 添加必要字段到请求体
                if (receiveName != null) exchangeParams.put("receiveName", receiveName);
                if (receivePhone != null) exchangeParams.put("receivePhone", receivePhone);
                if (address != null) exchangeParams.put("address", address);

                // 打印请求参数
                System.out.println("兑换接口请求参数: " + exchangeParams);
                System.out.println("兑换接口请求头: " + headers);

                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(exchangeParams, headers);

                Map<String, Object> response = null;
                try {
                    response = extendedTimeoutRestTemplate.postForObject(exchangeUrl, requestEntity, HashMap.class);
                    System.out.println("兑换接口调用成功，响应: " + response);
                } catch (org.springframework.web.client.ResourceAccessException e) {
                    System.err.println("无法连接到兑换接口: " + e.getMessage());
                    // 尝试降低超时时间再试一次
                    try {
                        System.out.println("正在使用原始RestTemplate重试...");
                        response = restTemplate.postForObject(exchangeUrl, requestEntity, HashMap.class);
                        System.out.println("使用原始RestTemplate调用成功，响应: " + response);
                    } catch (Exception retryEx) {
                        System.err.println("重试失败: " + retryEx.getMessage());
                        retryEx.printStackTrace();
                        return Result.error(500, "无法连接到兑换接口: " + e.getMessage());
                    }
                } catch (org.springframework.web.client.HttpClientErrorException e) {
                    System.err.println("兑换接口客户端错误: " + e.getStatusCode() + ", " + e.getResponseBodyAsString());
                    return Result.error(e.getRawStatusCode(), "兑换接口错误: " + e.getResponseBodyAsString());
                } catch (org.springframework.web.client.HttpServerErrorException e) {
                    System.err.println("兑换接口服务器错误: " + e.getStatusCode() + ", " + e.getResponseBodyAsString());
                    return Result.error(e.getRawStatusCode(), "兑换接口服务器错误: " + e.getResponseBodyAsString());
                } catch (Exception e) {
                    System.err.println("调用兑换接口时发生未知错误: " + e.getClass().getName() + ": " + e.getMessage());
                    e.printStackTrace();
                    return Result.error(500, "调用兑换接口失败: " + e.getMessage());
                }
                // 检查响应中是否包含错误码，只有code为0才算成功
                if (response != null && response.containsKey("code") && Integer.valueOf(0).equals(response.get("code"))) {
                    // 兑换成功，才减库存
                    gift.setStock(gift.getStock() - 1);
                    boolean updated = giftService.updateById(gift);
                    if (!updated) {
                        return Result.error(500, "库存更新失败");
                    }
                    // 获取studentName和giftName
                    StudentGift studentGift = studentGiftService.getById(giftId);
                    com.school.edupoint.model.MessageV0 msg = new com.school.edupoint.model.MessageV0();
                    msg.setTitle("礼物兑换提醒：");
                    msg.setMessageType("gift_exchange");
                    msg.setContent("有人兑换礼物请及时发货");
                    com.school.edupoint.websocket.MyTextWebSocketHandler.sendToAllAdmins(msg);
                    return Result.success(response);
                } else {
                    // 兑换失败，直接返回
                    Integer errorCode = response != null && response.get("code") instanceof Number ? ((Number)response.get("code")).intValue() : 500;
                    String errorMsg = response != null && response.containsKey("msg") ? String.valueOf(response.get("msg")) : "兑换失败";
                    System.out.println("兑换接口返回错误: 代码=" + errorCode + ", 消息=" + errorMsg);
                    return Result.error(errorCode, errorMsg);
                }
            } catch (Exception e) {
                System.err.println("准备调用兑换接口时发生错误: " + e.getMessage());
                e.printStackTrace();
                return Result.error(500, "准备调用兑换接口时发生错误: " + e.getMessage());
            }
        } else {
            return Result.error(460, "当前库存不足无法兑换");
        }
    }

    /**
     * 从参数中提取整数值
     */
    private Integer extractIntegerParam(Map<String, Object> param, String key) {
        if (!param.containsKey(key)) {
            return null;
        }

        Object value = param.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                System.out.println("无法将参数 " + key + " 的值 '" + value + "' 转换为整数");
                return null;
            }
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return null;
    }
}
