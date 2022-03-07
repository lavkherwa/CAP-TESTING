package com.sap.testing.cap.testing.cap.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Insert;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.my.gtt.Sample;
import cds.gen.my.gtt.Sample_;
import cds.gen.testservice.ProductExtension_;
import cds.gen.testservice.SessionContext_;
import cds.gen.testservice.TestService_;

@Component
@ServiceName(TestService_.CDS_NAME)
public class SessionHandler implements EventHandler {

	private static final String UPDATE_SESSION_CONTEXT = "UPDATE_SESSION_CONTEXT";
	private static final String CLEANUP_GTT = "MY_GTT_SAMPLE";

	private final JdbcTemplate jdbcTemplate;
	private final PersistenceService dataStore;

	public SessionHandler(JdbcTemplate jdbcTemplate, PersistenceService dataStore) {
		this.jdbcTemplate = jdbcTemplate;
		this.dataStore = dataStore;

	}

	@Before(entity = SessionContext_.CDS_NAME, event = CdsService.EVENT_READ)
	public void beforeReadSessionContext(final CdsReadEventContext context) {
		try {

			// use JDBC template to invoke the procedure and update the session context
			jdbcTemplate.update(String.format("call %s(?,?)", UPDATE_SESSION_CONTEXT), "test-user", "en");

		} catch (final Exception e) {
			throw new ServiceException(ErrorStatuses.SERVER_ERROR, "something went wrong", e);
		}
	}

	@Before(entity = ProductExtension_.CDS_NAME, event = CdsService.EVENT_READ)
	public void beforeReadProductExtension(final CdsReadEventContext context) {
		try {

			List<Map<String, String>> data = getDataFromExternalService();

			dataStore.run(Insert.into(Sample_.CDS_NAME).entries(data));

		} catch (final Exception e) {
			throw new ServiceException(ErrorStatuses.SERVER_ERROR, "something went wrong", e);
		}

	}

	@After(entity = ProductExtension_.CDS_NAME, event = CdsService.EVENT_READ)
	public void afterReadProductExtension(final CdsReadEventContext context) {
		try {

			/*
			 * IDEAL: create a procedure for cleanup of all session variables and run it for
			 * all services AFTER
			 */

			// Cleanup session context
			jdbcTemplate.update(String.format("truncate table %s", CLEANUP_GTT));
		} catch (final Exception e) {
			throw new ServiceException(ErrorStatuses.SERVER_ERROR, "something went wrong", e);
		}

	}

	/* Simulating the data coming in from external agent */
	private List<Map<String, String>> getDataFromExternalService() {

		Map<String, String> row1 = new HashMap<String, String>();
		Map<String, String> row2 = new HashMap<String, String>();

		row1.put(Sample.FIELD1, "product1");
		row1.put(Sample.FIELD2, "product1 extension");

		row2.put(Sample.FIELD1, "product2");
		row2.put(Sample.FIELD2, "product2 extension");

		List<Map<String, String>> data = new ArrayList<>();
		data.add(row1);
		data.add(row2);
		return data;
	}

}
