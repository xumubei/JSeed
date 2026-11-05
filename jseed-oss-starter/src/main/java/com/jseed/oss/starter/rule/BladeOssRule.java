package com.jseed.oss.starter.rule;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.jseed.oss.starter.utils.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Default bucket naming rule
 *
 * @author cyrus
 */
@Component
@AllArgsConstructor
public class BladeOssRule implements OssRule {

	@Override
	public String bucketName(String bucketName) {
		return bucketName;
	}

	@Override
	public String fileName(String originalFilename) {
		return "upload/" + DateUtil.today() + "/" + IdUtil.fastSimpleUUID() + "." + FileUtil.getFileExtension(originalFilename);
	}

}
