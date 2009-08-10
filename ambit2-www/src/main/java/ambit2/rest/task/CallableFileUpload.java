package ambit2.rest.task;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.fileupload.FileItem;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Asynchronous file upload
 * @author nina
 *
 */
public abstract class CallableFileUpload implements Callable<Reference> {
	protected List<FileItem> items;
	protected String fileUploadField;
	public CallableFileUpload(List<FileItem> items, String fileUploadField) {
		this.items = items;
		this.fileUploadField = fileUploadField;
	}
	public Reference call() throws Exception {
				try {
                    // Process only the uploaded item called "fileToUpload" and
                    // save it on disk
                    boolean found = false;
                    for (final Iterator<FileItem> it = items.iterator(); 
                    		it.hasNext()
                            && !found;) {
                        FileItem fi = it.next();
                        if (fi.getFieldName().equals(fileUploadField)) {
                        	fi.getContentType();
                            found = true;
                            File file = new File(System.getProperty("java.io.tmpdir")+fi.getName());
                            fi.write(file);
                        }
                    }    

                    return createReference();
                 } catch (Exception e) {
                	 throw new ResourceException(new Status(Status.SERVER_ERROR_INTERNAL,e.getMessage()));
                 } finally {
                	 
                 }

	}
	
	public abstract Reference createReference() ;
	

	/*
FileRepresentation rep = new FileRepresentation(
        "d:\\temp\\test.txt", MediaType.TEXT_ALL, 0);
EncodeRepresentation encodedRep = new EncodeRepresentation(Encoding.GZIP,
        rep);
Client client = new Client(Protocol.HTTP);
Response response = client.put("http://localhost:8182/essai/";, encodedRep);
System.out.println("******" + response.getStatus());


	 */
}
