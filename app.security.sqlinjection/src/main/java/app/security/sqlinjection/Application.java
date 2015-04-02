package app.security.sqlinjection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Application for demo the SQL injection
 * 
 * @author malalanayake
 *
 */
public class Application {

	public static void main(String[] args) {
		Application app = new Application();

		System.out.println("****Unsecured : Try to Login Bob with wrong password****");
		boolean status1 = app.unsecuredValidateLogin("bob", "wrong");
		System.out.println("Result:" + status1 + "\n");

		System.out
			.println("****Unsecured : Try to Login Bob with wrong password by using SQL injection****");
		boolean status2 = app.unsecuredValidateLogin("bob' or '1'='1", "wrong");
		System.out.println("Result:" + status2 + "\n");

		System.out.println("****Secured : Try to Login Bob with wrong password****");
		boolean status3 = app.securedValidateLogin("bob", "wrong");
		System.out.println("Result:" + status3 + "\n");

		System.out
			.println("****Secured : Try to Login Bob with wrong password by using SQL injection****");
		boolean status4 = app.securedValidateLogin("bob' or '1'='1", "wrong");
		System.out.println("Result:" + status4 + "\n");

		System.out.println("****Information Leakage : Try to Login Bob with wrong password****");
		StringBuilder stringBuilder1 = new StringBuilder();
		boolean status5 = app.informationLeakageLogin("bob", "wrong", stringBuilder1);
		System.out.println("Result:" + status5);
		System.out.println("User message:" + stringBuilder1.toString() + "\n");

		System.out.println("****Information Leakage : Try to Login with wrong username****");
		StringBuilder stringBuilder2 = new StringBuilder();
		boolean status6 = app.informationLeakageLogin("test", "wrong", stringBuilder2);
		System.out.println("Result:" + status6);
		System.out.println("User message:" + stringBuilder2.toString() + "\n");

		System.out
			.println("****Corrected Information Leakage : Try to Login Bob with wrong password****");
		StringBuilder stringBuilder3 = new StringBuilder();
		boolean status7 = app.correctedInformationLeakageLogin("bob", "wrong", stringBuilder3);
		System.out.println("Result:" + status7);
		System.out.println("User message:" + stringBuilder3.toString() + "\n");

		System.out.println("****Corrected Information Leakage : Try to Login with wrong username****");
		StringBuilder stringBuilder4 = new StringBuilder();
		boolean status8 = app.correctedInformationLeakageLogin("test", "wrong", stringBuilder4);
		System.out.println("Result:" + status8);
		System.out.println("User message:" + stringBuilder4.toString() + "\n");
	}

	/**
	 * SQL injection is possible
	 * 
	 * @param name
	 * @param password
	 * @return
	 */
	public boolean unsecuredValidateLogin(String name, String password) {

		System.out.println("User Name :" + name);
		System.out.println("Passward :" + password);

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/cs466?user=root&password=root123");
			stmt = conn.createStatement();
			// String concatenation
			String sql = "select * from users where name='" + name + "' and password='" + password + "'";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return true; // login succeeded
			} else {
				return false; // login failed
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				stmt.close();
				conn.close();
			} catch (SQLException e) {

			}
		}

	}

	/**
	 * SQL injection is not possible because we used the PreparedStatements
	 * 
	 * @param name
	 * @param password
	 * @return
	 */
	public boolean securedValidateLogin(String name, String password) {

		System.out.println("User Name :" + name);
		System.out.println("Passward :" + password);

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/cs466?user=root&password=root123");
			stmt = conn.createStatement();
			String sql = "select * from users where name=? and password=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, password);
			rs = ps.executeQuery();

			if (rs.next()) {
				return true; // login pass
			} else {
				return false; // login fail
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				stmt.close();
				conn.close();
			} catch (SQLException e) {

			}
		}

	}

	/**
	 * Detail information present to user
	 * 
	 * @param name
	 * @param password
	 * @param msg
	 * @return
	 */
	public boolean informationLeakageLogin(String name, String password, StringBuilder msg) {

		System.out.println("User Name :" + name);
		System.out.println("Passward :" + password);

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/cs466?user=root&password=root123");
			stmt = conn.createStatement();
			String sql = "select password from users where name=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			rs = ps.executeQuery();
			if (rs.next()) {
				String actualPassword = rs.getString("password"); // get password stored in
																																																						// database
				if (!actualPassword.equals(password)) {
					System.out.println();
					msg.append("login failed because of bad password");
					return false;
				} else
					return true; // login succeeded
			} else {
				msg.append("login failed because of bad username");
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				stmt.close();
				conn.close();
			} catch (SQLException e) {

			}
		}
	}

	/**
	 * Hide the details from user
	 * 
	 * @param name
	 * @param password
	 * @param msg
	 * @return
	 */
	public boolean correctedInformationLeakageLogin(String name, String password, StringBuilder msg) {

		System.out.println("User Name :" + name);
		System.out.println("Passward :" + password);

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/cs466?user=root&password=root123");
			stmt = conn.createStatement();
			String sql = "select password from users where name=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			rs = ps.executeQuery();

			if (rs.next()) {
				String actualPassword = rs.getString("password"); // get password stored in
																																																						// database
				if (!actualPassword.equals(password)) {
					System.out.println();
					throw new Exception("login failed because of bad password");
				} else
					return true; // login succeeded
			} else {
				throw new Exception("login failed because of bad username");
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg.append("Unauthorized access wrong credentials");
			return false;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				stmt.close();
				conn.close();
			} catch (SQLException e) {

			}
		}
	}
}
