package data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataResults {

	private final List<String> columnNames;
	private final List<List<Object>> data;

	public DataResults(List<String> columnNames, List<List<Object>> data) {
		this.columnNames = columnNames;
		this.data = data;
	}

	public DataResults() {
		ArrayList<Object> row = new ArrayList<Object>();

		columnNames = new ArrayList<String>();
		columnNames.add("NIL");
		data = new ArrayList<>();
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
