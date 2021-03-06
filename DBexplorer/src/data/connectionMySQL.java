package data;

import static data.interfaceMySQL.db;
import static data.interfaceMySQL.driver;

import applicationDB.MyException;

/**
 * Connection class specialized for MySQL.
 * 
 * @author Cedric Ferrand
 *
 */
public class connectionMySQL extends connectionDB implements interfaceMySQL {

	public connectionMySQL(String address, String port, String login, String password) throws MyException {
		super.setDb(db);
		super.setDriver(driver);
		super.connect(address, port, login, password);
	}

}
