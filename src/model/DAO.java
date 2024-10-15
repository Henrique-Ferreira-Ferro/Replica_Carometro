package model;

import java.sql.Connection;
import java.sql.DriverManager;

public class DAO {

	//Classe responsavel por abrir e fechar a conecção com o banco de dados
	private Connection con;
	
	//Abaixo se encontram variaveis que iram configurar a conecção com o banco de dados
	//Caminho do driver do mysql
	private String driver = "com.mysql.cj.jdbc.Driver";
	//O caminho do seu banco de dados, basta entrar no workbench do mysql e ver, alem disso e preciso adicionar algumas coisas como é possivel ver abaixo
	private String url = "jdbc:mysql://localhost:3306/dbcarometro";
	//Dados do usuario que pode acessar esse banco de dados
	private String user = "root";
	private String password = "nan";
	
	//Metodo que ira realizar a conecção
	public Connection conectar() {
		try {
			//Se refere a essa classe Connection, usando a biblioteca do driver
			Class.forName(driver);
			//Realiza a conecção por meio dos parametros
			con = DriverManager.getConnection(url, user, password);
			return con;
			
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
}
