package data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapping class to pass ResultSet to TableView.
 * 
 * @author Cedric Ferrand
 *
 */
public class DataResults {

	private final List<String> columnNames;
	private final List<List<Object>> data;

	/**
	 * construstor to use when the resultset is not empty.
	 * 
	 * @param columnNames
	 * @param data
	 */
	public DataResults(List<String> columnNames, List<List<Object>> data) {
		this.columnNames = columnNames;
		this.data = data;
	}

	/**
	 * constructor to use when the resultset is empty
	 */
	public DataResults() {
		List<Object> row = new ArrayList<Object>();
		data = new ArrayList<>();

		columnNames = new ArrayList<String>();
		columnNames.add("NIL");
		row.add("NIL");
		data.add(row);
	}

	public int getNumColumns() {
		return columnNames.size();
	}

	public String getColumnName(int index) {
		return columnNames.get(index);
	}

	public int getNumRows() {
		return data.size();
	}

	public Object getData(int column, int row) throws SQLException {
		return data.get(row).get(column);
	}

	public List<List<Object>> getData() {
		return data;
	}
}
