package id.co.bippo.camelasyncredo;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.ProxyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

	private static Logger logger = LoggerFactory.getLogger(App.class);
	private static LoggerInvoiceListener loggerInvoiceListener = new LoggerInvoiceListener();
	private static InvoiceListener invoiceListener;
	
//	public static void main(String[] args) throws Exception {
//	invoiceListener = loggerInvoiceListener;
//	invoiceListener.invoiceCreated(243, "Sumba Enterprise");
//	logger.info("first invoice sent");
//	invoiceListener.invoiceCreated(938, "Mina Co.");
//	logger.info("second invoice sent");
//}

	public static void main(String[] args) throws Exception {
		CamelContext camelContext = new DefaultCamelContext();
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
//				from("direct:invoice").bean(loggerInvoiceListener);
				from("direct:invoice").inOnly().to("seda:invoice.queue");
				from("seda:invoice.queue").threads().bean(loggerInvoiceListener);
			}
		});
		camelContext.start();

		invoiceListener = new ProxyBuilder(camelContext).endpoint("direct:invoice")
			.build(InvoiceListener.class);
		invoiceListener.invoiceCreated(243, "Sumba Enterprise");
		logger.info("first invoice sent");
		invoiceListener.invoiceCreated(938, "Mina Co.");
		logger.info("second invoice sent");

		camelContext.stop();
	}
	
}
