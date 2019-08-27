package com.devpeer.calories.settings;

import com.devpeer.calories.settings.model.UserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/settings")
public class UserSettingsController {

    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    public UserSettingsController(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    @GetMapping
    public ResponseEntity getSettings(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(userSettingsRepository.findById(userDetails.getUsername()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @PutMapping
    public ResponseEntity upsertSettings(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestBody UserSettings userSettings) {
        try {
            userSettings.setUserId(userDetails.getUsername());
            return ResponseEntity.ok(userSettingsRepository.save(userSettings));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }
}
