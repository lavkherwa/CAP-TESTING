package com.sap.testing.cap.testing.cap.handlers;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.testing.cap.testing.cap.handlers.cqnmodifiers.EnrichedUnifiedTemplateReadCqnModifier;

import cds.gen.my.template.UnifiedTemplate;
import cds.gen.my.template.UnifiedTemplate_;
import cds.gen.testservice.DeleteMappingContext;
import cds.gen.testservice.EnrichedUnifiedTemplate;
import cds.gen.testservice.EnrichedUnifiedTemplate_;
import cds.gen.testservice.TestService_;
import cds.gen.testservice.UpdateRankingContext;

@Component
@ServiceName(TestService_.CDS_NAME)
public class TemplatesHandler implements EventHandler {

	private static final String UPDATE_RANK = "UPDATE_RANK";

	private final PersistenceService dataStore;
	private final CqnAnalyzer analyzer;
	private final JdbcTemplate jdbcTemplate;

	public TemplatesHandler(PersistenceService dataStore, final CdsModel model, JdbcTemplate jdbcTemplate) {
		this.analyzer = CqnAnalyzer.create(model);
		this.dataStore = dataStore;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Before(entity = EnrichedUnifiedTemplate_.CDS_NAME, event = CdsService.EVENT_READ)
	public void beforeReadEnrichedUnifiedTemplate(final CdsReadEventContext context) {
		CqnSelect modifiedQuery = CQL.copy(context.getCqn(), new EnrichedUnifiedTemplateReadCqnModifier());
		context.setCqn(modifiedQuery);
	}

	@After(entity = EnrichedUnifiedTemplate_.CDS_NAME, event = CdsService.EVENT_READ)
	public void afterReadEnrichedUnifiedTemplate(List<EnrichedUnifiedTemplate> items) {
		for (EnrichedUnifiedTemplate item : items) {
			item.setName(item.getName() + "-enriched desc");
		}
	}

	// URL would look like -->
	// /odata/v4/TestService/EnrichedUnifiedTemplate(1)/TestService.deleteMapping
	@On(entity = EnrichedUnifiedTemplate_.CDS_NAME)
	public void onDeleteMapping(final DeleteMappingContext context) {

		Integer mappingId = (Integer) analyzer.analyze(context.getCqn()).targetKeys().get(EnrichedUnifiedTemplate.ID);

		try {
			CqnDelete delete = Delete.from(UnifiedTemplate_.CDS_NAME)
					.where(b -> b.get(UnifiedTemplate.ID).eq(mappingId));

			dataStore.run(delete);
			context.setCompleted();

		} catch (Exception e) {
			throw new ServiceException(ErrorStatuses.SERVER_ERROR, "something went wrong", e);
		}
	}

	@On(entity = EnrichedUnifiedTemplate_.CDS_NAME)
	public void onUpdateRanking(final UpdateRankingContext context) {

		Integer mappingId = (Integer) analyzer.analyze(context.getCqn()).targetKeys().get(EnrichedUnifiedTemplate.ID);
		Integer targetRank = context.getTargetRank();

		// Update the rank and re-adjust the mappings
		try {

			// use JDBC template to invoke the procedure and update the rankings
			jdbcTemplate.update(String.format("call %s(?,?)", UPDATE_RANK), mappingId, targetRank);

			context.setResult(targetRank);
		} catch (final Exception e) {
			throw new ServiceException(ErrorStatuses.SERVER_ERROR, "something went wrong", e);
		}
	}

}
