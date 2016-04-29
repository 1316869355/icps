package com.icps.dao;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class MyDataSource {
	/*dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	dataSource.setUrl("jdbc:mysql://localhost:3306/icpsdb");
	dataSource.setUsername("root");
	dataSource.setPassword("123456");*/
	private String DriverClass = "com.mysql.jdbc.Driver";
	private static String URL = "jdbc:mysql://localhost:3306/icpsdb";
	private static String USERNAME = "root";
	private static String PASSWORD = "123456";
	
	protected static DriverManagerDataSource dateSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
}
