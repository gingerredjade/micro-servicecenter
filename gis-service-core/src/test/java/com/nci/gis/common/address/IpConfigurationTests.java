package com.nci.gis.common.address;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IpConfigurationTests {

	@Autowired
	IpConfiguration ipConfiguration;

	@Test
	public void testAddress() {
		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
			address.getAddress();
			String hostAddress = address.getHostAddress();
			int port = ipConfiguration.getPort();
			System.out.println(address.getAddress()+"===hostAddress==="+hostAddress+"==="+port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
