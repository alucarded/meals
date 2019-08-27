package com.devpeer.calories.settings;

import com.devpeer.calories.settings.model.UserSettings;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserSettingsRepository extends PagingAndSortingRepository<UserSettings, String> {
}
