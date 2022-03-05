namespace my.templateview;

using {
    my.template.UnifiedTemplate,
    my.template.UnifiedTemplateV2
} from '../templates';

using my.masterdata.Products as products from '../masterdata';

// Enriched entity for unified template with additional information
entity EnrichedUnifiedTemplate   as
    select from UnifiedTemplate
    left join products
        on UnifiedTemplate.product = products.productID
    {
        key UnifiedTemplate.ID,
            UnifiedTemplate.plant,
            UnifiedTemplate.product,
            products.name, // name is a localized entity
            products.desc
    };


entity EnrichedUnifiedTemplateV2 as
    select from UnifiedTemplateV2 {
        key UnifiedTemplateV2.ID,
            UnifiedTemplateV2.plant,
            UnifiedTemplateV2.product.productID,
            UnifiedTemplateV2.product.name, // name is a localized entity
            UnifiedTemplateV2.product.desc,
    };
