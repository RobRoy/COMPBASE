package uzuzjmd.database.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * 
 * @author Julian Dehne
 */
public class MysqlConnect {

	static final Logger logger = LogManager.getLogger(MysqlConnect.class.getName());

	public Connection conn = null;
	public static Boolean isLokal = true;

	/**
	 * Mit dieser Methode stellt man die Verbindung zu der Datenbank her.
	 */
	public void connect(String connectionString) {
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException ex) {
				logger.error(ex);
			}
			conn = DriverManager.getConnection(connectionString);
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw new Error("could not connect to mysql");
		}
	}

	/**
	 * Mit dieser Methode wird die Verbindung zu der Datenbank geschlossen
	 */
	public void close() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (final SQLException e) {
			throw new Error("could not close mysql");
		}
	}

	/**
	 * Hilfsmethode 2 - fügt der einem PreparedStatement die entsprechenden
	 * Parameter hinzu
	 * 
	 * @param statement
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	private PreparedStatement addParameters(final String statement, final Object[] args) {
		try {
			final PreparedStatement ps = conn.prepareStatement(statement);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					final Object arg = args[i];
					setParam(ps, arg, i + 1);
				}
			}
			return ps;
		} catch (SQLException ex) {
			logger.error(ex);
		}
		return null;
	}

	/**
	 * Mit dieser Methode können select-Statements abgesetzt werden.
	 * 
	 * @param statement
	 * @param args
	 * @return
	 */
	public VereinfachtesResultSet issueSelectStatement(final String statement, final Object... args) {
		try {
			PreparedStatement ps = addParameters(statement, args);
			ResultSet queryResult = ps.executeQuery();
			return new VereinfachtesResultSet(queryResult);
		} catch (SQLException ex) {
			logger.error(ex);
		}
		return null;
	}

	/**
	 * Mit dieser Methode können Statements ohne Variablen und ohne Rückgaben
	 * ausgeführt werden.
	 * 
	 * @param statement
	 */
	public void otherStatements(final String statement) {
		try {
			this.conn.createStatement().execute(statement);
		} catch (SQLException ex) {
			logger.error(ex);
		}
	}

	/**
	 * Mit dieser Methode können updateStatements abgesetzt werden.
	 * 
	 * @param statement
	 * @param args
	 * @return
	 */
	public Integer issueUpdateStatement(final String statement, final Object... args) {
		PreparedStatement ps = addParameters(statement, args);
		try {
			return ps.executeUpdate();
		} catch (SQLException ex) {
			logger.error(ex);
		}
		return null;
	}

	/**
	 * Mit dieser Methode können insert- oder delete-Statements abgesetzt
	 * werden.
	 * 
	 * @param statement
	 * @param args
	 */
	public void issueInsertOrDeleteStatement(final String statement, final Object... args) {
		PreparedStatement ps = addParameters(statement, args);
		try {
			ps.execute();
		} catch (SQLException ex) {
			logger.error(ex);
		}
	}

	private void setParam(final PreparedStatement ps, final Object arg, final int i) throws SQLException {
		if (arg instanceof String) {
			ps.setString(i, (String) arg);
		} else if (arg instanceof Integer) {
			ps.setInt(i, (Integer) arg);
		} else if (arg instanceof Double) {
			ps.setDouble(i, (Double) arg);
		} else if (arg instanceof Boolean) {
			ps.setBoolean(i, (Boolean) arg);
		} else if (arg instanceof Float) {
			ps.setFloat(i, (Float) arg);
		} else if (arg instanceof Short) {
			ps.setShort(i, (Short) arg);
		} else if (arg instanceof Long) {
			ps.setLong(i, (Long) arg);
		} else if (arg instanceof Byte) {
			ps.setByte(i, (Byte) arg);
		} else if (arg instanceof Character) {
			ps.setString(i, ((Character) arg).toString());
		} else if (arg instanceof Date) {
			final java.sql.Date d = new java.sql.Date(((Date) arg).getTime());
			ps.setDate(i, d);
		} else if (arg == null) {
			ps.setNull(i, java.sql.Types.NULL);
		} else {
			ps.setString(i, arg.toString());
		}
	}

	public Connection getConnection() {
		return conn;
	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}
}
