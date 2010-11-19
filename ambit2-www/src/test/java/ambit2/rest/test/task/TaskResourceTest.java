package ambit2.rest.test.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.config.Preferences;
import ambit2.core.smiles.SmilesParserWrapper.SMILES_PARSER;
import ambit2.rest.AmbitApplication;
import ambit2.rest.OpenTox;
import ambit2.rest.task.CallablePOST;
import ambit2.rest.task.RemoteTask;
import ambit2.rest.task.RemoteTaskPool;
import ambit2.rest.test.ResourceTest;

public class TaskResourceTest extends ResourceTest {
	protected AmbitApplication app;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		setUpDatabase("src/test/resources/src-datasets.xml");
/*
		Context context = new Context();
		context.getParameters().add(Preferences.DATABASE, getDatabase());
		context.getParameters().add(Preferences.USER, getUser());
		context.getParameters().add(Preferences.PASSWORD, getPWD());
		context.getParameters().add(Preferences.PORT, getPort());
		context.getParameters().add(Preferences.HOST, getHost());

		// Create a component
		component = new Component();

		component.getClients().add(Protocol.FILE);
		component.getClients().add(Protocol.HTTP);
		component.getClients().add(Protocol.HTTPS);

		app = new AmbitApplication();
		app.setContext(context);

		// Attach the application to the component and start it

		component.getDefaultHost().attach(app);
		component.getInternalRouter().attach("/", app);

		component.getServers().add(Protocol.HTTP, port);
		component.getServers().add(Protocol.HTTPS, port);

		component.start();
		*/
	}

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/task", port);
	}

	@After
	public void cleanup() {
		//((AmbitApplication) app).removeTasks();
	}

	@Test
	public void testRDF() throws Exception {
		Callable<Reference> c = new Callable<Reference>() {
			public Reference call() throws Exception {
				return new Reference("http://localhost/newResult");
			}
		};
		((AmbitApplication) app).addTask("Test task", c, new Reference(String
				.format("http://localhost:%d", port)));

		testGet(getTestURI(), MediaType.APPLICATION_RDF_XML);

	}

	@Test
	public void testURI() throws Exception {
		Callable<Reference> c = new Callable<Reference>() {
			public Reference call() throws Exception {
				return new Reference("quickTaskURI");
			}
		};
		((AmbitApplication) app).addTask("Test task", c, new Reference(String
				.format("http://localhost:%d", port)));

		testGet(getTestURI(), MediaType.TEXT_URI_LIST);

	}

	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			// Assert.assertEquals("http://localhost:8181/newURI",line);
			count++;
		}
		return count == 1;
	}

	@Test
	public void testCompletedTaskURI() throws Exception {
		// creating task that completes immediately
	
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"0");
		testAsyncTask(String.format("http://localhost:%d/algorithm/mockup", port), 
				form,Status.SUCCESS_OK,"dataseturi");

	}
	@Test
	public void testRunningTaskURI() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		testAsyncTask(String.format("http://localhost:%d/algorithm/mockup", port), 
				form,Status.SUCCESS_OK,"dataseturi");

	}
	
	@Test
	public void testMultipleRunningTasks() throws Exception {

		
		Reference url = new Reference(String.format("http://localhost:%d/algorithm/mockup", port));
		for (int i=0; i < 600; i++) {
			Form form = new Form();  
			form.add(OpenTox.params.dataset_uri.toString(),String.format("dataseturi-%d",i+1));
			form.add(OpenTox.params.delay.toString(),"1000");
			RemoteTask task = new RemoteTask(
					url,
					MediaType.APPLICATION_WWW_FORM,form.getWebRepresentation(),Method.POST,null);
		}
		

		Reference alltasks = new Reference(String.format("http://localhost:%d/task", port));
		RemoteTask tasks = new RemoteTask(
				alltasks,
				MediaType.TEXT_URI_LIST,null,Method.GET,null);
		while (!tasks.poll()) {
			System.out.println(tasks);
		}
		System.out.println(tasks);
		
	}	
	
	@Test
	public void testSuperService() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.algorithm_uri.toString(),String.format("http://localhost:%d/algorithm/mockup", port));
		
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);
		
		Reference ref = testAsyncTask(superservice, form, Status.SUCCESS_OK, "dataseturi");

		Assert.assertEquals("dataseturi",ref.toString());
				
		
	}
	
	@Test
	public void testSuperServiceWithError() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.error.toString(),"Mockup error");
		form.add(OpenTox.params.algorithm_uri.toString(),String.format("http://localhost:%d/algorithm/mockup", port));
		
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);

		try {
			Reference ref = testAsyncTask(superservice, form, Status.SERVER_ERROR_BAD_GATEWAY, "dataseturi");
			Assert.fail("Should throw an error");
		} catch (Exception x) {
			ResourceException xx = (ResourceException) x;
			Assert.assertEquals(Status.SERVER_ERROR_BAD_GATEWAY,xx.getStatus());
			Assert.assertTrue(xx.getMessage().indexOf("Mockup error")>0);
		}
		
		
		/*
		ResourceException xx = (ResourceException) x.getCause();
		Assert.assertEquals(Status.CLIENT_ERROR_BAD_REQUEST,xx.getStatus());
		Assert.assertEquals("Mockup error",xx.getMessage());
		*/
	}	
	
	//TODO - how to get it to test timeouts?
	@Test
	public void testSuperServiceWithTimeout() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.algorithm_uri.toString(),String.format("http://localhost:%d/algorithm/mockup", port));
		
		CallablePOST post = new CallablePOST(
				MediaType.TEXT_URI_LIST,
				form.getWebRepresentation(),
				null);
		try {
			Reference ref = post.call();
			Assert.fail("Should throw an error");
		} catch (ResourceException x) {
			Assert.assertEquals(Status.SERVER_ERROR_GATEWAY_TIMEOUT,x.getStatus());
		} 
	}	
	
	public void testSuperServiceRemote() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.algorithm_uri.toString(),"http://ambit.uni-plovdiv.bg:8080/ambit2/algorithm/toxtreecarc");
		//form.add("dataset_uri","http://ambit.uni-plovdiv.bg:8080/ambit2/algorithm/J48");
		
		CallablePOST post = new CallablePOST(
				MediaType.TEXT_URI_LIST,
				form.getWebRepresentation(),
				null);
		Reference ref = post.call();
		long now = System.currentTimeMillis();
		Assert.assertEquals("http://ambit.uni-plovdiv.bg:8080/ambit2/model/2",ref.toString());
		System.out.println(System.currentTimeMillis()-now);		
		
	}	
	@Test
	public void testMockup() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.algorithm_uri.toString(),String.format("http://localhost:%d/algorithm/mockup", port));
		
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);

		Reference ref = testAsyncTask(superservice, form, Status.SUCCESS_OK, "dataseturi");
	}	

	@Test
	public void testMockupError() throws Exception {
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.error.toString(),"Mockup error");
		form.add(OpenTox.params.algorithm_uri.toString(),String.format("http://localhost:%d/algorithm/mockup", port));
		
		
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);
		try {
			//OTModel.model().withParams(form).withUri(superservice).process(null);
			Reference ref = testAsyncTask(superservice, form, Status.SERVER_ERROR_BAD_GATEWAY, "dataseturi");
			Assert.fail("Should throw an error");
		} catch (ResourceException x) {
			ResourceException xx = (ResourceException) x;
			Assert.assertEquals(Status.SERVER_ERROR_BAD_GATEWAY,xx.getStatus());
			Assert.assertTrue(xx.getMessage().indexOf("Mockup error")>0);
		} 
	}
	@Override
	public void testGetJavaObject() throws Exception {
	}
	
	@Test
	public void testMultiplePOST() throws Exception {
		Preferences.setProperty(Preferences.SMILESPARSER.toString(),SMILES_PARSER.CDK.toString());
		//setUpDatabase("src/test/resources/src-datasets.xml");
		final Reference url = testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreeskinirritation", port),
				new Form(), Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));		
		
		final RemoteTaskPool pool = new RemoteTaskPool();
		ExecutorService xs= Executors.newCachedThreadPool();
		Runnable[] t = new Runnable[3];
		final int batch = 500;
		for (int j=0; j < t.length; j++) {

			t[j] = new Runnable() {
				@Override
				public void run() {
					for (int i=0; i < batch; i++) {
						Form form = new Form();  
						form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/compound/11", port));
						RemoteTask task = new RemoteTask(
								url,
								MediaType.TEXT_URI_LIST,form.getWebRepresentation(),Method.POST,null);
						pool.add(task);
						System.out.println(i);
					}
				}
				@Override
				public String toString() {
					return "Task creator";
				}
			};
		
		}
		for (Runnable r: t) xs.submit(r);
		
		
		/*
		Reference alltasks = new Reference(String.format("http://localhost:%d/task", port));
		RemoteTask tasks = new RemoteTask(
				alltasks,
				MediaType.TEXT_URI_LIST,null,Method.GET,null);
		
		System.out.println("Polling!");
		*/
		System.out.println(String.format("Poll %d",pool.size()));
		while (pool.size()<t.length*batch) {
			//System.out.println(String.format("Poll %d",pool.size()));
			Thread.yield();
		}
		int running = pool.poll();
		System.out.println(String.format("Poll %d running %d",pool.size(),running));
		while ((running = pool.poll())>0) {
			//System.out.println(String.format("Poll %d running %d",pool.size(),running));
			Thread.yield();

			/*
			tasks = new RemoteTask(
					alltasks,
					MediaType.TEXT_URI_LIST,null,Method.GET,null);
					*/
			
		}
		
		System.out.println("Done!!!!!!!");
		xs.awaitTermination(1,TimeUnit.SECONDS);
		xs.shutdown();
		System.out.println(pool.size());
		
	}	
	
	public static void main(String[] args) {
		TaskResourceTest test = new TaskResourceTest();
		try {
			
			test.setUp();
			test.testMultiplePOST();
			
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {test.tearDown(); } catch (Exception x) {x.printStackTrace();}
		}
		
	}
	
}
