package ambit2.rest.query;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.db.search.structure.QueryStructureByQuality;
import ambit2.rest.OpenTox;

/**
 * Retrieves Dataset ofcompounds, given a quality label. 
 * Resource wrapper for {@link QueryStructureByQuality}
 * @author nina
 *
 */
public class QLabelQueryResource   extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {
	public static String resource = "/qlabel";
	protected Integer datasetID;
	protected Integer queryResultsID;
	@Override
	public String getCompoundInDatasetPrefix() {
		if (dataset_prefixed_compound_uri)
		return
			datasetID!=null?String.format("%s/%d", OpenTox.URI.dataset.getURI(),datasetID):
				queryResultsID!=null?String.format("%s/R%d", OpenTox.URI.dataset.getURI(),queryResultsID):"";
		else return "";
	}	
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		QueryStructureByQuality q = new QueryStructureByQuality();
		Form form = request.getResourceRef().getQueryAsForm();
		Object key = form.getFirstValue(QueryResource.search_param);
		if (key != null) {
	        try {
	        	q.setValue(new QLabel(QUALITY.valueOf(Reference.decode(key.toString()))));
	        } catch (Exception x) {
	        	StringBuilder b = new StringBuilder();
	        	b.append("Valid values are ");
	        	for(QUALITY v : QUALITY.values()) { b.append(v); b.append('\t');}
				throw new ResourceException(
						Status.CLIENT_ERROR_BAD_REQUEST,
						b.toString(),
						x
						);
	        }			
		} else {
			q.setValue(new QLabel(QUALITY.OK));
		}
		
		Object id = request.getAttributes().get(OpenTox.URI.dataset.getKey());
		
		
		if (id != null) try {

			datasetID = Integer.parseInt(id.toString());
			QueryDatasetByID scope = new QueryDatasetByID();
			scope.setValue(new Integer(Reference.decode(id.toString())));
			
			QueryCombinedStructure combined = new QueryCombinedStructure();
			combined.add(q);
			combined.setScope(scope);
			return combined;
		} catch (Exception x) {
			
		} 
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No dataset !");
	}

}
