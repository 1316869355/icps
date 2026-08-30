package com.icps.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.icps.entity.UserMp;
import com.icps.mapper.UserMpMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 用户服务单元测试。
 *
 * <p>含一条关键回归用例：{@link #updateLastLoginOnlyTouchesLastLoginColumn()} ——
 * 早期实现使用 {@code new UserMp()} + {@code updateById}，会把实体字段默认值
 * （{@code role} 默认为 "student"）写回数据库，导致管理员角色被改成 student。</p>
 */
@ExtendWith(MockitoExtension.class)
class UserMpServiceImplTest {

    @Mock
    private UserMpMapper userMpMapper;

    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private UserMpServiceImpl userService;

    @Test
    void matchesPasswordUsesBcrypt() {
        String encoded = passwordEncoder.encode("123456");

        assertTrue(userService.matchesPassword("123456", encoded));
        assertFalse(userService.matchesPassword("wrong-password", encoded));
        assertFalse(userService.matchesPassword(null, encoded));
        assertFalse(userService.matchesPassword("123456", null));
    }

    @Test
    void getByUsernameDelegatesToMapper() {
        UserMp user = new UserMp();
        user.setUserId(7L);
        user.setUsername("admin");
        when(userMpMapper.selectByUsername("admin")).thenReturn(user);

        assertEquals(user, userService.getByUsername("admin"));
    }

    @Test
    void updateLastLoginOnlyTouchesLastLoginColumn() {
        userService.updateLastLogin(7L);

        // 回归：绝不能退回 updateById —— 实体默认值会被整行写回
        verify(userMpMapper, never()).updateById(any(UserMp.class));

        ArgumentCaptor<LambdaUpdateWrapper<UserMp>> captor = ArgumentCaptor.forClass(LambdaUpdateWrapper.class);
        verify(userMpMapper).update(isNull(), captor.capture());

        // getSqlSet() 为 SET 子句，getTargetSql() 为 WHERE 子句
        String setSql = captor.getValue().getSqlSet();
        String whereSql = captor.getValue().getTargetSql();

        assertTrue(setSql.contains("last_login"), "必须更新 last_login 列，实际 SET：" + setSql);
        assertFalse(setSql.contains("role"), "角色列不允许被更新，实际 SET：" + setSql);
        assertFalse(setSql.contains("password"), "密码列不允许被更新，实际 SET：" + setSql);
        assertTrue(whereSql.contains("user_id"), "必须按 user_id 定位，实际 WHERE：" + whereSql);
    }

    @Test
    void updateLastLoginIgnoresNullUserId() {
        userService.updateLastLogin(null);
        verify(userMpMapper, never()).update(any(), any(LambdaUpdateWrapper.class));
    }

    @Test
    void getUserByUsernameAndPasswordUsesBcrypt() {
        String encoded = passwordEncoder.encode("123456");
        UserMp user = new UserMp();
        user.setUserId(7L);
        user.setPassword(encoded);
        when(userMpMapper.selectByUsername("admin")).thenReturn(user);

        assertTrue(userService.getUserByUsernameAndPassword("admin", "123456"));
        assertFalse(userService.getUserByUsernameAndPassword("admin", "nope"));
    }

    @Test
    void getUserByUsernameAndPasswordReturnsFalseForUnknownUser() {
        when(userMpMapper.selectByUsername("ghost")).thenReturn(null);
        assertFalse(userService.getUserByUsernameAndPassword("ghost", "123456"));
    }

    /**
     * 关键回归：非学生用户被分配到学生 user_id 保留段（2023001011..2023001099）时，
     * Service 必须回滚插入并返回失败，否则会与 icps_stu.user_id 撞车，经
     * {@code SecurityUtils.canAccessStudent} 接管真实学生档案——水平越权。
     *
     * <p>背景：本会话测试中曾实际触发——新建 admin 被分配 user_id=2023001014，
     * 撞上学生"赵六111"。</p>
     */
    @Test
    void addUserRejectsNonStudentUserFallingIntoStudentReservedRange() {
        UserMp candidate = new UserMp();
        candidate.setUsername("hijacker");
        candidate.setPassword("123456");
        candidate.setRole("admin");

        // 用户名不冲突
        when(userMpMapper.selectByUsername("hijacker")).thenReturn(null);
        // 模拟 AUTO_INCREMENT 把候选 user_id 分配到学生段（2023001014）
        when(userMpMapper.insert(any(UserMp.class))).thenAnswer(invocation -> {
            UserMp u = invocation.getArgument(0);
            u.setUserId(2023001014L);
            return 1;
        });
        // 学生段内 icps_stu.user_id=2023001014 已被占用
        when(userMpMapper.countStudentsByUserIdIncludingDeleted(2023001014L)).thenReturn(1L);

        Map<String, Object> result = userService.addUser(candidate);

        assertFalse(Boolean.TRUE.equals(result.get("success")),
                "非学生用户落入学生段必须被拒绝");
        // 必须逻辑删除刚插入的用户，避免被越权利用
        verify(userMpMapper).deleteById(2023001014L);
    }

    /**
     * 学生用户被分配到学生段是允许的（学生创建流程），不应被拦截。
     */
    @Test
    void addUserAllowsStudentUserFallingIntoStudentReservedRange() {
        UserMp candidate = new UserMp();
        candidate.setUsername("newstudent");
        candidate.setPassword("123456");
        candidate.setRole("student");

        when(userMpMapper.selectByUsername("newstudent")).thenReturn(null);
        when(userMpMapper.insert(any(UserMp.class))).thenAnswer(invocation -> {
            UserMp u = invocation.getArgument(0);
            u.setUserId(2023001015L);
            return 1;
        });

        Map<String, Object> result = userService.addUser(candidate);

        assertTrue(Boolean.TRUE.equals(result.get("success")),
                "学生用户落入学生段是正常流程，不应被拦截");
        // 不应回滚
        verify(userMpMapper, never()).deleteById(any(Long.class));
    }

    /**
     * 非学生用户被分配到学生段之外的值（且不撞任何 icps_stu.user_id）应正常通过。
     */
    @Test
    void addUserAllowsNonStudentUserOutsideStudentRange() {
        UserMp candidate = new UserMp();
        candidate.setUsername("safeadmin");
        candidate.setPassword("123456");
        candidate.setRole("admin");

        when(userMpMapper.selectByUsername("safeadmin")).thenReturn(null);
        when(userMpMapper.insert(any(UserMp.class))).thenAnswer(invocation -> {
            UserMp u = invocation.getArgument(0);
            u.setUserId(2023002001L); // V99 迁移后的安全值
            return 1;
        });
        when(userMpMapper.countStudentsByUserIdIncludingDeleted(2023002001L)).thenReturn(0L);

        Map<String, Object> result = userService.addUser(candidate);

        assertTrue(Boolean.TRUE.equals(result.get("success")),
                "非学生用户落在安全段应正常创建");
        verify(userMpMapper, never()).deleteById(any(Long.class));
    }
}
