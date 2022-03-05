package com.sap.testing.cap.testing.cap.handlers.cqnmodifiers;

import java.util.List;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.RefSegment;
import com.sap.cds.ql.StructuredTypeRef;
import com.sap.cds.ql.cqn.CqnModifier;
import com.sap.cds.ql.cqn.CqnSelectListItem;
import com.sap.cds.ql.cqn.CqnSortSpecification;
import com.sap.cds.ql.cqn.CqnStructuredTypeRef;

import cds.gen.my.templateview.EnrichedUnifiedTemplate;

public class EnrichedUnifiedTemplateReadCqnModifier implements CqnModifier {

	// Default - PREDICATE FILTER EXAMPLE
	@Override
	public CqnStructuredTypeRef ref(final StructuredTypeRef ref) {
		final RefSegment root = ref.rootSegment();
		final Predicate filterProduct = CQL.get(EnrichedUnifiedTemplate.PRODUCT).eq("product1");
		/*
		 * check if there is already any filters then add with AND or else just add
		 * product filter
		 */
		final Predicate filterFinal = root.filter().map(f -> CQL.and(f, filterProduct)).orElse(filterProduct);
		root.filter(filterFinal);
		return ref;
	}

	// Default - SORT EXAMPLE
	@Override
	public List<CqnSortSpecification> orderBy(final List<CqnSortSpecification> orderBy) {
		orderBy.clear();
		orderBy.add(CQL.get(EnrichedUnifiedTemplate.PRODUCT).asc());
		return orderBy;
	}

	// Default - ADD SELECT ITEMS
	@Override
	public List<CqnSelectListItem> items(List<CqnSelectListItem> items) {

		// Useful when we want to get some info in the after handler
		items.add(CQL.get(EnrichedUnifiedTemplate.PRODUCT));
		items.add(CQL.get(EnrichedUnifiedTemplate.NAME));

		return items;
	}

}
