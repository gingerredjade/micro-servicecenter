package com.nci.gis.conf;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "config-attributes")
@ToString
public class ConfigTest {

	private String value;
	private String[] valueArray;
	private List<String> valueList;
	private HashMap<String, String> valueMap;
	private List<Map<String, String>> valueMapList;
}
