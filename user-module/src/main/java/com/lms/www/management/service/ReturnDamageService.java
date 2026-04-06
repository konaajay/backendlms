package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.ReturnDamage;

public interface ReturnDamageService {

    ReturnDamage processReturn(ReturnDamage returnDamage);

    List<ReturnDamage> getAllReturns();

    ReturnDamage getReturnById(Long id);

}