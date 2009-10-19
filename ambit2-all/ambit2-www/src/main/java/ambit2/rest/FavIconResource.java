package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;

import ambit2.base.io.DownloadTool;

/**
 * favicon.ico support
 * @author nina
 *
 */
public class FavIconResource extends Resource {
	@Override
	public Representation represent(Variant variant) throws ResourceException {
		return new OutputRepresentation(MediaType.IMAGE_PNG) {
			@Override
			public void write(OutputStream outputStream)
					throws IOException {
				try {
				DownloadTool.download(getClass().getClassLoader().getResourceAsStream("16x16.png"), outputStream);
				outputStream.close();				
				} catch (Exception x) {
					x.printStackTrace();
				}
				
			}
		};
	}
}
