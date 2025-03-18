package com.bookstore.admin.service;

import java.util.ArrayList;
import java.util.List;

import com.bookstore.admin.repository.SettingRepository;
import com.bookstore.admin.model.GeneralSettingBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.entity.setting.Setting;
import com.bookstore.entity.setting.SettingCategory;

@Service
public class SettingService {

    @Autowired private SettingRepository repo;

    public List<Setting> listAllSettings() {
        return (List<Setting>) repo.findAll();
    }

    public GeneralSettingBag getGeneralSettings() {
        List<Setting> settings = new ArrayList<>();

        List<Setting> generalSettings = repo.findByCategory(SettingCategory.GENERAL); 
        List<Setting> currencySettings = repo.findByCategory (SettingCategory.CURRENCY);

        settings.addAll(generalSettings); 
        settings.addAll(currencySettings);

        return new GeneralSettingBag(settings);
    }

    public void saveAll(Iterable<Setting> settings) { 
        repo.saveAll(settings);
    }

    public List<Setting> getMailServerSettings() {
		return repo.findByCategory(SettingCategory.MAIL_SERVER);
	}

    public List<Setting> getMailTemplateSettings() {
		return repo.findByCategory(SettingCategory.MAIL_TEMPLATES);
	}	

    public List<Setting> getCurrencySettings() {
		return repo.findByCategory(SettingCategory.CURRENCY);
	}


}