package com.lms.www.fee.service.impl;

import org.springframework.stereotype.Service;

import com.lms.www.fee.dto.FeeMapper;
import com.lms.www.fee.dto.UserDto;
import com.lms.www.fee.service.UserService;
import com.lms.www.service.AdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AdminService adminService;

    @Override
    public UserDto getUser(Long id) {
        return FeeMapper.toUserDto(adminService.getUserByUserId(id));
    }
}
