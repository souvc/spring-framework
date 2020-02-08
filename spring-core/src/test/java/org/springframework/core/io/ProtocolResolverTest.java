package org.springframework.core.io;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 使用自定义协议解析器加载
 *
 * 主要定义一些特定的协议，比如一个特定的文件上传
 *
 */
public class ProtocolResolverTest {

	@Test
	public void testProtocolResolver() {
		// 使用自定义协议解析器加载
		DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
		resourceLoader.addProtocolResolver(new MyProtocolResolver());
		Resource resource = resourceLoader.getResource("cloud:Resource.class");
		assertEquals(resource, new ClassPathResource("Resource.class"));
	}

	/**
	 * 自定义资源解析协议
	 */
	public class MyProtocolResolver implements ProtocolResolver {
		@Override
		public Resource resolve(String location, ResourceLoader resourceLoader) {
			if (location.startsWith("cloud")) {
				//此处可以拓展读取云的数据，或者比较私密的数据存放地方
				//Resource resource = new ClassPathResource("Resource.class", getClass());
				return resourceLoader.getResource(location.replace("cloud", "classpath"));
			}
			return null;
		}
	}
}
