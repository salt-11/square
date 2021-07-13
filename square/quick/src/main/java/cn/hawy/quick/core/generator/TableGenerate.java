package cn.hawy.quick.core.generator;

import cn.stylefeng.roses.kernel.generator.GenerateParams;
import cn.stylefeng.roses.kernel.generator.SimpleGenerator;

public class TableGenerate {

	//生成代码里，注释的作者
	private static String author = "hawy";
	//代码生成输出的目录，可为项目路径的相对路径
	private static String outputDirectory = "E:\\temp";
	//jdbc驱动
	private static String jdbcDriver = "com.mysql.cj.jdbc.Driver";
	//数据库连接地址
	private static String jdbcUrl = "jdbc:mysql://47.110.50.211:3306/quick_pay?autoReconnect=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false&serverTimezone=CTT";
	//数据库账号
	private static String jdbcUserName = "root";
	//数据库密码
	private static String jdbcPassword = "648c064a886c800f";
	//service是否生成接口，这个根据自己项目情况决定
	private static Boolean generatorInterface = false;
	//代码生成包含的表，可为空，为空默认生成所有
	private static String[] includeTables = new String[] {"t_agent_rate_channel"};
	//代码生成的类的父包名称
	private static String parentPackage = "cn.hawy.quick.modular.api";

	public static void main(String[] args) {
		//初始化参数
		GenerateParams generateParams = new GenerateParams();
		generateParams.setAuthor(author);
		generateParams.setOutputDirectory(outputDirectory);
		generateParams.setJdbcDriver(jdbcDriver);
		generateParams.setJdbcUrl(jdbcUrl);
		generateParams.setJdbcUserName(jdbcUserName);
		generateParams.setJdbcPassword(jdbcPassword);
		generateParams.setIncludeTables(includeTables);
		generateParams.setGeneratorInterface(generatorInterface);
		generateParams.setParentPackage(parentPackage);

		//执行代码生成
		SimpleGenerator.doGeneration(generateParams);
	}

}
