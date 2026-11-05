// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import cn.dev33.satoken.stp.StpUtil;

import com.jseed.core.starter.model.R;
import com.jseed.satoken.starter.annotation.Anonymous;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Local demonstration only.
 *
 * @author cyrus
 */
@Profile("satoken")
@RestController
@RequestMapping("/demo/auth")
public class SaTokenDemo {
    @Anonymous
    @PostMapping("/login")
    public R<Map<String, String>> login() {
        StpUtil.login("example-user");
        return R.ok(
                Map.of("tokenName", StpUtil.getTokenName(), "tokenValue", StpUtil.getTokenValue()));
    }

    @GetMapping("/me")
    public R<String> me() {
        StpUtil.checkLogin();
        return R.ok(StpUtil.getLoginIdAsString());
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        StpUtil.logout();
        return R.ok();
    }
}
