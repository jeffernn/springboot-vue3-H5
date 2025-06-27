package com.school.edupoint.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.school.edupoint.config.EduContext;
import com.school.edupoint.mapper.Gift1Mapper;

import com.school.edupoint.model.*;
import com.school.edupoint.response.Result;
import com.school.edupoint.service.StudentGiftService;
import com.school.edupoint.service.StudentService;
import com.school.edupoint.service.impl.StudentScore1Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/gift")
public class StudentGiftController {

    @Autowired
    private StudentGiftService studentGiftService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private Gift1Mapper giftMapper;

    @Autowired
    private EduContext eduContext;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StudentScore1Impl studentScoreService;

    /**
     * 分页查询礼物兑换记录
     */
    @PostMapping("/exchange/page")
    public Result<IPage<StudentGift>> getPage(@RequestBody Map<String, Object> param) {
        Integer pageNum = (Integer) param.getOrDefault("pageNum", 1);
        Integer pageSize = (Integer) param.getOrDefault("pageSize", 10);
        Integer studentId = (Integer) param.get("studentId");
        Integer giftId = (Integer) param.get("giftId");
        Integer status = (Integer) param.get("status");

        return Result.success(studentGiftService.getPage(pageNum, pageSize, studentId, giftId, status));
    }

    /**
     * 获取礼物兑换详情
     */
    @GetMapping("/exchange/{id}")
    public Result<StudentGift> getById(@PathVariable Integer id) {
        StudentGift gift = studentGiftService.getById(id);
        if (gift == null) {
            return Result.error(404, "找不到ID为" + id + "的兑换记录");
        }
        return Result.success(gift);
    }

    /**
     * 新增或更新礼物兑换信息
     */
    @PostMapping("/exchange/save")
    public Result<StudentGift> save(@RequestBody StudentGift studentGift) {
        return Result.success(studentGiftService.save(studentGift));
    }

    /**
     * 修改兑换状态
     */
    @PostMapping("/exchange")
    public Result<Boolean> updateStatus(@RequestBody Map<String, Object> param) {
        System.out.println("收到的参数: " + param);

        Integer id = null;
        Integer status = null;

        // 尝试从不同类型的参数中提取id
        if (param.containsKey("id")) {
            Object idObj = param.get("id");
            if (idObj instanceof Integer) {
                id = (Integer) idObj;
            } else if (idObj instanceof String) {
                try {
                    id = Integer.parseInt((String) idObj);
                } catch (NumberFormatException e) {
                    return Result.error(400, "id参数格式不正确");
                }
            } else if (idObj instanceof Number) {
                id = ((Number) idObj).intValue();
            }
        }

        // 尝试从不同类型的参数中提取status
        if (param.containsKey("status")) {
            Object statusObj = param.get("status");
            if (statusObj instanceof Integer) {
                status = (Integer) statusObj;
            } else if (statusObj instanceof String) {
                try {
                    status = Integer.parseInt((String) statusObj);
                } catch (NumberFormatException e) {
                    return Result.error(400, "status参数格式不正确");
                }
            } else if (statusObj instanceof Number) {
                status = ((Number) statusObj).intValue();
            }
        }

        if (id == null || status == null) {
            return Result.error(400, "参数不能为空，请确保提供了id和status参数");
        }

        // 获取当前用户信息，如果为空则使用系统用户
        String updateBy = "system";
        try {
            User currentUser = eduContext.getUser();
            if (currentUser != null) {
                updateBy = String.valueOf(currentUser.getId());
            }
        } catch (Exception e) {
            // 捕获任何可能的异常，继续使用默认的系统用户
            System.out.println("获取当前用户信息失败: " + e.getMessage());
        }

        // 检查记录是否存在
        if (!studentGiftService.checkRecordExists(id)) {
            // 数据库中没有找到记录，尝试创建新记录
            System.out.println("未找到ID为" + id + "的记录，尝试创建新记录");

            // 从参数中提取礼物详细信息
            Integer studentId = null;
            Integer giftId = null;

            // 尝试多种可能的参数名获取studentId
            if (param.containsKey("studentId")) {
                studentId = extractIntegerParam(param, "studentId");
            } else if (param.containsKey("student_id")) {
                studentId = extractIntegerParam(param, "student_id");
            } else if (param.containsKey("userId")) {
                studentId = extractIntegerParam(param, "userId");
            } else if (param.containsKey("user_id")) {
                studentId = extractIntegerParam(param, "user_id");
            }

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
                System.out.println("从id参数获取礼品ID: " + giftId);
            }

            // 打印所有参数，帮助调试
            System.out.println("所有请求参数:");
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }

            System.out.println("提取到的studentId: " + studentId);
            System.out.println("提取到的giftId: " + giftId);

            // 从参数中提取其他礼品信息
            String receiveName = (String) param.get("receiveName");
            String receivePhone = (String) param.get("receivePhone");
            String address = (String) param.get("address");

            // 如果参数中没有提供studentId，尝试从数据库中查询
            if (studentId == null) {
                // 从数据库中获取一个有效的学生ID
                Student defaultStudent = studentService.getDefaultStudent();
                if (defaultStudent != null) {
                    studentId = defaultStudent.getId();
                    System.out.println("从数据库获取默认学生ID: " + studentId);
                } else {
                    return Result.error(400, "创建记录失败：无法获取有效的学生ID，请在请求中提供studentId参数");
                }
            }

            // 获取学生信息
            Student student = studentService.getInfo(studentId);
            if (student == null) {
                return Result.error(400, "创建记录失败：无法找到ID为" + studentId + "的学生信息");
            }

            Gift gift = null;

            // 如果提供了giftId，直接通过API获取礼品详情
            if (giftId != null) {
                // 从API获取礼品详情
                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                String giftApiUrl = baseUrl + "/api/admin/gift/" + giftId;
                try {
                    Result<Gift> giftResult = restTemplate.getForObject(giftApiUrl, Result.class);
                    if (giftResult != null && giftResult.getData() != null) {
                        // 将Result中的data转换为Gift对象
                        Map<String, Object> giftData = (Map<String, Object>) giftResult.getData();
                        gift = mapToGift(giftData);
                    }
                } catch (Exception e) {
                    System.out.println("从API获取礼品详情失败: " + e.getMessage());
                    // 如果API调用失败，尝试从数据库获取
                    gift = giftMapper.getById(giftId);
                }
            }

            // 如果通过API未获取到礼品详情，则尝试使用数据库
            if (gift == null && giftId != null) {
                gift = giftMapper.getById(giftId);
            }

            // 如果仍然没有礼品信息，则获取所有礼品列表并使用第一个礼品
            if (gift == null) {
                // 尝试从路径参数中获取礼品ID
                String pathParam = (String) param.get("path");
                if (pathParam != null && pathParam.contains("gift/detail")) {
                    // 解析路径中的id参数
                    int idIndex = pathParam.indexOf("id=");
                    if (idIndex != -1) {
                        String idPart = pathParam.substring(idIndex + 3);
                        // 如果有其他参数，截取到下一个&
                        int andIndex = idPart.indexOf("&");
                        if (andIndex != -1) {
                            idPart = idPart.substring(0, andIndex);
                        }
                        try {
                            giftId = Integer.parseInt(idPart);
                            System.out.println("从路径参数中提取礼品ID: " + giftId);
                            gift = giftMapper.getById(giftId);
                        } catch (NumberFormatException e) {
                            System.out.println("解析路径中的礼品ID失败: " + e.getMessage());
                        }
                    }
                }

                // 尝试检查所有参数，查找任何可能包含gift/detail?id=的参数值
                if (gift == null) {
                    for (Map.Entry<String, Object> entry : param.entrySet()) {
                        String value = String.valueOf(entry.getValue());
                        if (value != null && value.contains("gift/detail")) {
                            System.out.println("找到可能包含礼品ID的参数: " + entry.getKey() + " = " + value);
                            int idIndex = value.indexOf("id=");
                            if (idIndex != -1) {
                                String idPart = value.substring(idIndex + 3);
                                // 如果有其他参数，截取到下一个&
                                int andIndex = idPart.indexOf("&");
                                if (andIndex != -1) {
                                    idPart = idPart.substring(0, andIndex);
                                }
                                try {
                                    giftId = Integer.parseInt(idPart);
                                    System.out.println("从参数值中提取礼品ID: " + giftId);
                                    gift = giftMapper.getById(giftId);
                                    if (gift != null) {
                                        System.out.println("成功找到对应礼品: " + gift.getName());
                                        break; // 找到礼品就跳出循环
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("解析参数值中的礼品ID失败: " + e.getMessage());
                                }
                            }
                        }
                    }
                }

                // 如果仍未获取到礼品信息，才尝试获取所有礼品列表
                if (gift == null) {
                    try {
                        // 调用/api/admin/gift API获取所有礼品列表
                        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                        String giftListApiUrl = baseUrl + "/api/admin/gift";
                        Result<List<Map<String, Object>>> giftListResult = restTemplate.getForObject(giftListApiUrl, Result.class);
                        if (giftListResult != null && giftListResult.getData() != null) {
                            List<Map<String, Object>> gifts = (List<Map<String, Object>>) giftListResult.getData();
                            if (!gifts.isEmpty()) {
                                // 使用列表中的第一个礼品
                                Map<String, Object> firstGift = gifts.get(0);
                                gift = mapToGift(firstGift);
                                giftId = gift.getId();
                                System.out.println("使用默认礼品: ID=" + giftId + ", 名称=" + gift.getName());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("从API获取礼品列表失败: " + e.getMessage());
                        // 如果API调用失败，尝试从数据库获取默认礼品
                        Gift defaultGift = giftMapper.getDefaultGift();
                        if (defaultGift != null) {
                            gift = defaultGift;
                            giftId = gift.getId();
                        }
                    }
                }
            }

            // 如果仍然没有获取到礼品信息，返回错误
            if (gift == null) {
                return Result.error(400, "创建记录失败：无法获取礼品信息，请在请求中提供有效的giftId参数");
            }

            System.out.println("找到学生: " + student.getUsername());
            System.out.println("找到礼物: " + gift.getName());

            // 从参数或礼品信息中获取积分
            Integer point = extractIntegerParam(param, "point");
            if (point == null) {
                point = gift.getPoint(); // 使用礼品中定义的积分
            }

            // 检查学生积分是否足够
            StudentScore1 studentScore = studentScoreService.getStudentScoreByStudentId(studentId);
            if (studentScore == null || studentScore.getTotalPoint() < point) {
                System.out.println("学生积分不足，需要: " + point + ", 实际拥有: " +
                        (studentScore == null ? "0" : studentScore.getTotalPoint()));
                return Result.error(400, "积分不足，无法兑换该礼品");
            }

            // 创建新的礼物兑换记录
            StudentGift newGift = new StudentGift();
            newGift.setStudentId(studentId);
            newGift.setStudentName(student.getUsername());
            newGift.setGiftId(giftId);
            newGift.setGiftName(gift.getName());
            newGift.setImageUrl(gift.getImageUrl());
            newGift.setPoint(point);
            newGift.setStatus(status);
            newGift.setReceiveName(receiveName);
            newGift.setReceivePhone(receivePhone);
            newGift.setAddress(address);

            System.out.println("准备保存新记录: " + newGift);

            // 保存新记录
            try {
                StudentGift savedGift = studentGiftService.save(newGift);

                if (savedGift != null && savedGift.getId() != null) {
                    System.out.println("记录保存成功，ID: " + savedGift.getId());
                    return Result.success(true);
                } else {
                    System.out.println("记录保存失败，返回的对象为空或ID为空");
                    return Result.error(500, "创建记录失败，请检查服务器日志");
                }
            } catch (Exception e) {
                System.out.println("保存记录时发生异常: " + e.getMessage());
                e.printStackTrace();
                return Result.error(500, "创建记录失败: " + e.getMessage());
            }
        }

        // 记录存在，更新状态
        try {
            boolean success = studentGiftService.updateStatus(id, status, updateBy);
            if (success) {
                System.out.println("状态更新成功");
                return Result.success(true);
            } else {
                System.out.println("状态更新失败");
                return Result.error(500, "修改失败，请确认记录是否存在");
            }
        } catch (Exception e) {
            System.out.println("更新状态时发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "更新状态失败: " + e.getMessage());
        }
    }

    /**
     * 将Map对象转换为Gift对象
     */
    private Gift mapToGift(Map<String, Object> map) {
        Gift gift = new Gift();

        // 处理可能的类型转换问题
        if (map.containsKey("id")) {
            Object idObj = map.get("id");
            if (idObj instanceof Integer) {
                gift.setId((Integer) idObj);
            } else if (idObj instanceof Number) {
                gift.setId(((Number) idObj).intValue());
            } else if (idObj instanceof String) {
                try {
                    gift.setId(Integer.parseInt((String) idObj));
                } catch (NumberFormatException e) {
                    // 忽略解析错误
                }
            }
        }

        if (map.containsKey("name")) {
            gift.setName((String) map.get("name"));
        }

        if (map.containsKey("point")) {
            Object pointObj = map.get("point");
            if (pointObj instanceof Integer) {
                gift.setPoint((Integer) pointObj);
            } else if (pointObj instanceof Number) {
                gift.setPoint(((Number) pointObj).intValue());
            } else if (pointObj instanceof String) {
                try {
                    gift.setPoint(Integer.parseInt((String) pointObj));
                } catch (NumberFormatException e) {
                    // 忽略解析错误
                }
            }
        }

        if (map.containsKey("description")) {
            gift.setDescription((String) map.get("description"));
        }

        if (map.containsKey("imageUrl")) {
            gift.setImageUrl((String) map.get("imageUrl"));
        }

        return gift;
    }

    /**
     * 从参数中提取整数值
     */
    private Integer extractIntegerParam(Map<String, Object> param, String key) {
        if (!param.containsKey(key)) {
            return null;
        }

        Object obj = param.get(key);
        if (obj == null) {
            return null;
        }

        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof String) {
            String strValue = (String) obj;
            if (strValue.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(strValue);
            } catch (NumberFormatException e) {
                System.out.println("参数 " + key + " 值 '" + strValue + "' 不是有效的整数");
                return null;
            }
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }

        System.out.println("参数 " + key + " 的类型 " + obj.getClass().getName() + " 无法转换为整数");
        return null;
    }

    /**
     * 删除兑换记录
     */
    @DeleteMapping("/exchange/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        // 检查记录是否存在
        if (!studentGiftService.checkRecordExists(id)) {
            return Result.error(404, "找不到ID为" + id + "的兑换记录");
        }

        boolean success = studentGiftService.delete(id);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error(500, "删除失败");
        }
    }

    /**
     * 获取所有礼物兑换记录
     */
    @GetMapping("/exchange/list")
    public Result<List<StudentGift>> getAllExchanges() {
        System.out.println("获取所有礼物兑换记录");

        try {
            // 使用MyBatis-Plus的selectList方法获取所有记录
            LambdaQueryWrapper<StudentGift> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByDesc(StudentGift::getCreateTime); // 按创建时间降序排序

            // 直接使用mapper查询
            List<StudentGift> list = studentGiftService.getAllExchangeRecords();

            System.out.println("共获取到 " + list.size() + " 条记录");
            return Result.success(list);
        } catch (Exception e) {
            System.out.println("获取礼物兑换记录异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "获取数据失败: " + e.getMessage());
        }
    }
}