package com.send.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.StringUtils;

/**
 * 处理枚举的转换，如果无法转换时，返回null，不抛出异常
 * @author handong
 *
 */
@Slf4j
public class CustomEnumConverterFactory implements ConverterFactory<String, Enum> {

	@Override
	public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
		return new CustomEnumConverter<>(targetType);
	}
	
	class CustomEnumConverter<T extends Enum> implements Converter<String, T> {
		private final Class<T> enumType;
        CustomEnumConverter(Class<T> targetType) {
            this.enumType = targetType;
        }
		@Override
		public T convert(String source) {
			for(T t:enumType.getEnumConstants()){
				   if(t.name().equals(source)){
					    return t;
				   }
			}
			if (StringUtils.hasText(source)) {
                log.warn("Can't convert to Enum, source:{}, type:{}", source, enumType.getName());
            }
            return null;
		}
		
	}

}
