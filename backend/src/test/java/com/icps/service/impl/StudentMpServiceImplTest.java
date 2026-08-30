package com.icps.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.icps.entity.StudentMp;
import com.icps.mapper.StudentMpMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 学生服务单元测试。
 *
 * <p>含一条关键回归用例：{@link #convertStudentToMapMapsColumnsThatDoNotMatchFieldNames()} ——
 * 原生 {@code @Select("SELECT * ...")} 走 MyBatis 自动映射，不认 {@code @TableField}，
 * 因此 {@code sblood} / {@code start_sign} / {@code evaluated_type} 三列的实体字段名
 * 必须与列名驼峰完全一致，否则会出现「写能进、读出来是 null」。</p>
 */
@ExtendWith(MockitoExtension.class)
class StudentMpServiceImplTest {

    @Mock
    private StudentMpMapper studentMpMapper;

    @InjectMocks
    private StudentMpServiceImpl studentService;

    @Test
    void resolveStudentByUserId() {
        StudentMp student = student("CARD001");
        when(studentMpMapper.selectByUserId(1011L)).thenReturn(student);

        assertSame(student, studentService.resolveStudent("1011"));
    }

    @Test
    void resolveStudentFallsBackToSno() {
        StudentMp student = student("CARD002");
        when(studentMpMapper.selectByUserId(2023001001L)).thenReturn(null);
        when(studentMpMapper.selectBySno("2023001001")).thenReturn(student);

        assertSame(student, studentService.resolveStudent("2023001001"));
    }

    @Test
    void resolveStudentFallsBackToCardNo() {
        StudentMp student = student("CARD003");
        when(studentMpMapper.selectBySno("CARD003")).thenReturn(null);
        when(studentMpMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(student);

        assertSame(student, studentService.resolveStudent("CARD003"));
    }

    @Test
    void resolveStudentReturnsNullForBlankOrUnknown() {
        assertNull(studentService.resolveStudent(null));
        assertNull(studentService.resolveStudent("   "));
        assertNull(studentService.getByUserId(null));
        assertNull(studentService.getBySno(null));
        assertNull(studentService.getByCardNo(null));
    }

    @Test
    void convertStudentToMapMapsColumnsThatDoNotMatchFieldNames() {
        StudentMp student = new StudentMp();
        student.setUserId(1011L);
        student.setSno("2023001001");
        student.setSname("张三");
        student.setSsex(1);
        student.setSage(20);
        student.setShbt("篮球");
        student.setSblood("A型");
        student.setStartSign("白羊座");
        student.setEvaluatedType("积极型");
        student.setStuDept("计算机学院");
        student.setStuMajor("计算机科学与技术");
        student.setStuClazz("计科2101");
        student.setRegion("北京");
        student.setStuAddress("北京市海淀区");

        Map<String, Object> map = studentService.convertStudentToMap(student);

        // 回归：这三列列名与字段名不一致，映射错位时会读出 null
        assertEquals("A型", map.get("bloodType"));
        assertEquals("白羊座", map.get("zodiac"));
        assertEquals("积极型", map.get("evaluation"));

        assertEquals("篮球", map.get("hobby"));
        assertEquals("男", map.get("gender"));
        assertEquals("张三", map.get("name"));
        assertEquals("2023001001", map.get("studentId"));
        assertEquals("计算机学院", map.get("department"));
        assertEquals("计算机科学与技术", map.get("major"));
        assertEquals("计科2101", map.get("clazz"));
        assertEquals("北京市海淀区", map.get("address"));
        assertEquals(1011L, map.get("userId"));
    }

    @Test
    void convertStudentToMapHandlesNullGender() {
        StudentMp student = new StudentMp();
        student.setUserId(1L);
        student.setSsex(null);

        // 回归：早期实现 student.getSsex() == 1 会在 ssex 为空时抛出 NPE
        assertEquals("女", studentService.convertStudentToMap(student).get("gender"));
    }

    @Test
    void updateStudentAcceptsCamelCasePayloadKeys() {
        StudentMp student = student("CARD001");
        student.setUserId(1011L);
        when(studentMpMapper.selectByUserId(1011L)).thenReturn(student);
        when(studentMpMapper.updateById(student)).thenReturn(1);

        Map<String, Object> payload = new HashMap<>();
        payload.put("sname", "张三");
        payload.put("ssex", 1);
        payload.put("sage", 20);
        payload.put("stuDept", "计算机学院");
        payload.put("stuMajor", "计算机科学与技术");
        payload.put("stuClazz", "计科2101");
        payload.put("region", "北京");
        payload.put("shbt", "篮球");
        payload.put("bloodType", "A型");
        payload.put("zodiac", "白羊座");
        payload.put("evaluation", "积极型");
        payload.put("stuAddress", "北京市海淀区");

        Map<String, Object> result = studentService.updateStudent("1011", payload);

        assertTrue((Boolean) result.get("success"));
        assertEquals("A型", student.getSblood());
        assertEquals("白羊座", student.getStartSign());
        assertEquals("积极型", student.getEvaluatedType());
        assertEquals("篮球", student.getShbt());
        assertEquals("北京市海淀区", student.getStuAddress());
        verify(studentMpMapper).updateById(student);
    }

    @Test
    void updateStudentReturnsFailureWhenStudentMissing() {
        when(studentMpMapper.selectByUserId(999L)).thenReturn(null);
        when(studentMpMapper.selectBySno("999")).thenReturn(null);
        when(studentMpMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        Map<String, Object> result = studentService.updateStudent("999", new HashMap<>());

        assertFalse((Boolean) result.get("success"));
    }

    @Test
    void searchStudentsByKeywordUsesOrCondition() {
        when(studentMpMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(new ArrayList<>());

        List<Map<String, Object>> result = studentService.searchStudentsByKeyword("张三");

        assertTrue(result.isEmpty());
        ArgumentCaptor<LambdaQueryWrapper<StudentMp>> captor =
                ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(studentMpMapper).selectList(captor.capture());

        String sql = captor.getValue().getTargetSql().toLowerCase();
        assertTrue(sql.contains(" or "), "关键词搜索应在学号/姓名/班级/专业之间使用 OR，实际 SQL：" + sql);
    }

    @Test
    void searchStudentsByKeywordReturnsEmptyForBlankKeyword() {
        assertTrue(studentService.searchStudentsByKeyword(null).isEmpty());
        assertTrue(studentService.searchStudentsByKeyword("   ").isEmpty());
        verify(studentMpMapper, org.mockito.Mockito.never()).selectList(any(LambdaQueryWrapper.class));
    }

    private StudentMp student(String cardNo) {
        StudentMp student = new StudentMp();
        student.setStuCardNo(cardNo);
        return student;
    }
}
