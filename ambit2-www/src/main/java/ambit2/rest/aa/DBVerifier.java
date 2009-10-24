package ambit2.rest.aa;

import java.sql.Connection;
import java.sql.ResultSet;

import org.restlet.security.SecretVerifier;

import ambit2.db.search.QueryExecutor;
import ambit2.db.update.user.VerifyUser;
import ambit2.rest.AmbitApplication;



public class DBVerifier extends SecretVerifier {
	protected AmbitApplication application;
	protected QueryExecutor<VerifyUser> executor = new QueryExecutor<VerifyUser>();
	protected VerifyUser query = new VerifyUser();

	public DBVerifier(AmbitApplication application) {
		super();
		this.application = application;
	}

	@Override
	public boolean verify(String identifier, char[] inputSecret) {
		//TODO make use of same connection for performance reasons
		Connection c = null;
		ResultSet rs = null;
		try {
			query.setFieldname(identifier);
			query.setValue(new String(inputSecret));
			c = application.getConnection();
			executor.setConnection(c);
			rs = executor.process(query);
			boolean ok = false;
			while (rs.next()) {
				ok = query.getObject(rs);
				break;
			}
			return ok;
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {rs.close();} catch (Exception x) {};
			try {c.close();} catch (Exception x) {};
		}
		return false;
	}

}
