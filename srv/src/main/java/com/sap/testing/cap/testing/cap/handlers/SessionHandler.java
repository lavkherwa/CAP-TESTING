package com.sap.testing.cap.testing.cap.handlers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.testservice.SessionContext_;
import cds.gen.testservice.TestService_;

@Component
@ServiceName(TestService_.CDS_NAME)
public class SessionHandler implements EventHandler {

	private static final String UPDATE_SESSION_CONTEXT = "UPDATE_SESSION_CONTEXT";
	private final JdbcTemplate jdbcTemplate;

	public SessionHandler(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;

	}

	@Before(entity = SessionContext_.CDS_NAME, event = CdsService.EVENT_READ)
	public void beforeReadEnrichedUnifiedTemplate(final CdsReadEventContext context) {
		try {

			// use JDBC template to invoke the procedure and update the session context
			jdbcTemplate.update(String.format("call %s(?,?)", UPDATE_SESSION_CONTEXT), "test-user", "en");

		} catch (final Exception e) {
			throw new ServiceException(ErrorStatuses.SERVER_ERROR, "something went wrong", e);
		}
	}
}
