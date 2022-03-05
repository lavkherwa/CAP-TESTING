using my.template as myTemplate from '../db/templates';
using my.masterdata as myMasterdata from '../db/masterdata';
using my.templateview as myTemplateView from '../db/views';
using my.nativeview as myNativeView from '../db/views';

@cds.query.limit : {
    default : 20,
    max     : 100
}
service TestService {

    // Metadata entities
    @readonly
    entity Products                  as projection on myMasterdata.Products;

    // Unified template V1...................................................
    @Capabilities : {
        Insertable : true,
        Updatable  : true,
        Deletable  : false
    }
    entity UnifiedTemplate           as projection on myTemplate.UnifiedTemplate;

    @readonly
    entity EnrichedUnifiedTemplate   as projection on myTemplateView.EnrichedUnifiedTemplate actions {
        action deleteMapping();
        action updateRanking(targetRank : Integer) returns Integer;
    };


    // Unified template V2...................................................
    entity UnifiedTemplateV2         as projection on myTemplate.UnifiedTemplateV2 {
        ID, plant, product.productID as productID, product.name as productname, product.desc as productdesc
    } excluding {
        createdAt,
        createdBy,
        // modifiedAt,
        modifiedBy
    };

    // enable conflict detection
    annotate UnifiedTemplateV2 with {
        modifiedAt @odata.etag
    };

    @cds.query.limit.max : 100
    @readonly
    entity EnrichedUnifiedTemplateV2 as projection on myTemplateView.EnrichedUnifiedTemplateV2;

    // Native HANA proxy view
    entity SessionContext            as projection on myNativeView.SessionContext;

}
