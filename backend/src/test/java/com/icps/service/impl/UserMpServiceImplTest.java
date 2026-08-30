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
}
