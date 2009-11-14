package ambit2.db.update.assessment;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.AmbitUser;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.SessionID;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

/**
 * TODO Session class to be renamed to Assessment
 * @author nina
 *
 */
public class CreateAssessment extends AbstractUpdate<AmbitUser,SessionID> {
	public static final String sql = "insert into sessions (user_name,title) values (?,?)";
	public static final String sql_current_user = "insert into sessions (user_name,title) values (SUBSTRING_INDEX(user(),'@',1),?)";
	
	public CreateAssessment(AmbitUser user) {
		super();
		setGroup(user);
	}
	public CreateAssessment() {
		this(null);
	}	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> param = new ArrayList<QueryParam>();
		if (getGroup()!=null)
			param.add(new QueryParam<String>(String.class,getGroup().getName()));
		param.add(new QueryParam<String>(String.class,getObject()==null?"Default":getObject().getName()));
		return param;
	}

	public String[] getSQL() throws AmbitException {
		return new String[] {getGroup()==null?sql_current_user:sql};
	}

	public void setID(int index, int id) {
		getObject().setId(id);
	}
	


}
