package com.bookstore.admin.repository;

import org.springframework.data.repository.CrudRepository;

import com.bookstore.entity.setting.Setting;
import com.bookstore.entity.setting.SettingCategory;

import java.util.List;


public interface SettingRepository extends CrudRepository<Setting, String>{
    public List<Setting> findByCategory(SettingCategory category);
    
}
