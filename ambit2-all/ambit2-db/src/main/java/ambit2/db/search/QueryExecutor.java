package ambit2.db.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.ProcessorException;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.exceptions.DbAmbitException;

public class QueryExecutor<Q extends IQueryObject> extends AbstractDBProcessor<Q,ResultSet> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5821244671560506456L;
	protected PreparedStatement sresults=null;
	protected Statement statement=null;
	public void open() throws DbAmbitException {
	}

	public ResultSet process(Q target) throws AmbitException {
		Connection c = getConnection();		
		if (c == null) throw new AmbitException("no connection");
		try {
				List<QueryParam> params = target.getParameters();
				if (params == null) {
					statement = c.createStatement();
					ResultSet rs = statement.executeQuery(target.getSQL());
					return rs;
				} else {
					sresults = c.prepareStatement(target.getSQL());					
					QueryExecutor.setParameters(sresults, params);
					System.out.println(sresults);
					ResultSet rs = sresults.executeQuery();
					return rs;
				}
		} catch (Exception x) {
			x.printStackTrace();
			throw new ProcessorException(this,x);
		}
	}
	public void closeResults(ResultSet rs) throws SQLException {
		if (rs != null) rs.close();
		if (sresults != null) sresults.close(); sresults = null;
		if (statement != null) statement.close();		statement = null;
	}
	@Override
	public void close() throws SQLException {
		closeResults(null);		
		super.close();
	}
	public static void setParameters(PreparedStatement ps, List<QueryParam> params) throws SQLException {
		if (params != null)
			for (int i=0; i < params.size(); i++) {
				if (params.get(i).getValue()== null) throw new SQLException("Null parameter found at "+(i+1));
				Class clazz = params.get(i).getType();
				if (Integer.class.equals(clazz))
					ps.setInt(i+1, ((Integer)params.get(i).getValue()).intValue());
				else
				if (Long.class.equals(clazz))
					ps.setLong(i+1, ((Long)params.get(i).getValue()).longValue());
				else
				if (Double.class.equals(clazz))
					ps.setDouble(i+1, ((Double)params.get(i).getValue()).doubleValue());
				else
				if (String.class.equals(clazz))
					ps.setString(i+1, params.get(i).getValue().toString());
				else
					throw new SQLException("Unsupported type "+clazz);
			}		
	}
}
