package ambit2.rest.query;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.db.search.structure.QueryStructureByQuality;

/**
 * Resource wrapper for {@link QueryStructureByQuality}
 * @author nina
 *
 */
public class QLabelQueryResource   extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {
	public static String resource = "/qlabel";
	public QLabelQueryResource(Context context, Request request,
			Response response) {
		super(context, request, response);
	}

	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context,
			Request request, Response response) throws AmbitException {
		QueryStructureByQuality q = new QueryStructureByQuality();
		Form form = request.getResourceRef().getQueryAsForm();
		Object key = form.getFirstValue("search");
		if (key != null) {
	        try {
	        	q.setValue(new QLabel(QUALITY.valueOf(Reference.decode(key.toString()))));
	        } catch (Exception x) {
	        	StringBuilder b = new StringBuilder();
	        	b.append("Valid values are ");
	        	for(QUALITY v : QUALITY.values()) { b.append(v); b.append('\t');}
	        	throw new AmbitException(b.toString(),x);
	        }			
		} else {
			q.setValue(new QLabel(QUALITY.OK));
		}
		
		Object id = request.getAttributes().get("dataset_id");
		if (id != null) try {
			QueryDatasetByID scope = new QueryDatasetByID();
			scope.setValue(new Integer(Reference.decode(id.toString())));
			
			QueryCombinedStructure combined = new QueryCombinedStructure();
			combined.add(q);
			combined.setScope(scope);
			return combined;
		} catch (Exception x) {
			return q;
		} else return q;
	}

}
