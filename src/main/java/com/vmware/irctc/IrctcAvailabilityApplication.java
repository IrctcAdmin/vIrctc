package com.vmware.irctc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;


@EnableSpringDataWebSupport
@SpringBootApplication(scanBasePackages= "com")
@EntityScan(basePackages = {"com.vmware.irctc.model", "com.vmware.irctc.service"})
public class IrctcAvailabilityApplication {

	public static void main(String[] args) {
		SpringApplication.run(IrctcAvailabilityApplication.class, args);
	}
}
