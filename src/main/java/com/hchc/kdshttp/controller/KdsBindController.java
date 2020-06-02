package com.hchc.kdshttp.controller;

import com.hchc.kdshttp.mode.request.KdsInfo;
import com.hchc.kdshttp.pack.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangrong
 * @date 2020-06-02
 */
@RestController
@RequestMapping("/kds")
@Slf4j
public class KdsBindController {

    @PostMapping("/bind")
    public Result bind(@RequestBody KdsInfo kdsInfo) {
        return null;
    }
}
