// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import com.jseed.core.starter.model.R;
import com.jseed.oss.starter.OssTemplate;
import com.jseed.oss.starter.model.BladeFile;

import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author cyrus
 */
@Profile("oss")
@RestController
@RequestMapping("/demo/oss")
public class OssDemo {
    private final OssTemplate oss;

    public OssDemo(OssTemplate oss) {
        this.oss = oss;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<BladeFile> upload(@RequestParam MultipartFile file) {
        return R.ok(oss.putFile(file));
    }

    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] download(@RequestParam String name) {
        return oss.downloadFileAsBytes(name);
    }
}
