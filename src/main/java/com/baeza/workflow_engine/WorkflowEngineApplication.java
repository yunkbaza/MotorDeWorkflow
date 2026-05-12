package com.baeza.workflow_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync; // Adiciona isto

@SpringBootApplication
@EnableAsync // 🌟 Ativa o processamento em background do Spring!
public class WorkflowEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkflowEngineApplication.class, args);
	}
}