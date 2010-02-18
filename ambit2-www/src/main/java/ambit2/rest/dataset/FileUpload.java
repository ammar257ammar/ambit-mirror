package ambit2.rest.dataset;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StoredQuery;
import ambit2.rest.AmbitApplication;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DBConnection;
import ambit2.rest.task.CallableFileImport;
import ambit2.rest.task.CallableQueryResultsCreator;

public class FileUpload {
	protected Request request;
	protected Application application;
	protected SourceDataset dataset;
	
	public SourceDataset getDataset() {
		return dataset;
	}

	public void setDataset(SourceDataset dataset) {
		this.dataset = dataset;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	protected Response response;
	protected Context context;
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	/**
	 * Creates new entry in query table and adds structures into query_results
	 */
	protected Representation copyDatasetToQueryResultsTable(Form form)
			throws ResourceException {
		CallableQueryResultsCreator callable = new CallableQueryResultsCreator(
				form,
				getRequest().getRootRef(),
				getContext(),
				null);
		try {
			getResponse().setLocationRef(callable.call());
			getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
			return new StringRepresentation(getResponse().getLocationRef().toString(),MediaType.TEXT_URI_LIST);
		} catch  (Exception x) {
			throw new ResourceException(x);
		}

	}	
	
	
	
	protected Representation upload(Representation entity, Variant variant,boolean newEntry)
				throws ResourceException {	

		if ((entity == null) || !entity.isAvailable()) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty content");
		
		if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
			return copyDatasetToQueryResultsTable(new Form(entity));
		} else if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),true)) {
			  DiskFileItemFactory factory = new DiskFileItemFactory();
              //factory.setSizeThreshold(100);
	          RestletFileUpload upload = new RestletFileUpload(factory);
	          Connection connection = null;
	          try {
					DBConnection dbc = new DBConnection(getApplication().getContext());
					connection = dbc.getConnection(getRequest());	
	              List<FileItem> items = upload.parseRequest(getRequest());
	              DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter = 
	            	  	new DatasetURIReporter<IQueryRetrieval<SourceDataset>> (getRequest());
				  Reference ref =  ((AmbitApplication)getApplication()).addTask(
						 "File import",
						new CallableFileImport(getRequest().getClientInfo(),dataset,items,DatasetsHTMLReporter.fileUploadField,connection,reporter),
						getRequest().getRootRef());		
				  getResponse().setLocationRef(ref);
				  getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
				  getResponse().setEntity(null);
				  
	          } catch (Exception x) {
	        	  try { connection.close(); } catch (Exception xx) {xx.printStackTrace();}
	        	  throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
	          }
		} else if (isAllowedMediaType(entity.getMediaType())) {
					Connection connection = null;
					try {
						DBConnection dbc = new DBConnection(getApplication().getContext());
						connection = dbc.getConnection(getRequest());						
			          DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter = 
			            	  	new DatasetURIReporter<IQueryRetrieval<SourceDataset>> (getRequest());					
					  Reference ref =  ((AmbitApplication)getApplication()).addTask(
							  
							  	 String.format("File import %s [%d]", entity.getDownloadName()==null?entity.getMediaType():entity.getDownloadName(),entity.getSize()),
								new CallableFileImport(getRequest().getClientInfo(),dataset,(InputRepresentation)entity,connection,reporter),
								getRequest().getRootRef());		
						  getResponse().setLocationRef(ref);
						  getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
						  getResponse().setEntity(null);
					} catch (Exception x) {
						try { connection.close(); } catch (Exception xx) {xx.printStackTrace();}
 		        	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);

					}
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
				String.format("Unsupported Content-type=%s",entity.getMediaType()));
	
		return getResponse().getEntity();
            
	}
	
	protected boolean isAllowedMediaType(MediaType mediaType) {
		return 
		ChemicalMediaType.CHEMICAL_MDLSDF.equals(mediaType) ||
		MediaType.APPLICATION_RDF_XML.equals(mediaType) ||
		MediaType.APPLICATION_RDF_TURTLE.equals(mediaType) ||
		MediaType.TEXT_RDF_N3.equals(mediaType) ||
		//MediaType.TEXT_CSV.equals(mediaType) ||
		ChemicalMediaType.CHEMICAL_SMILES.equals(mediaType) ||
		ChemicalMediaType.CHEMICAL_MDLMOL.equals(mediaType);
		//ChemicalMediaType.CHEMICAL_CML.equals(mediaType) ||
	}
	
}
