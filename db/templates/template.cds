namespace my.template;

using {
    my.masterdata.Products,
    my.masterdata.ProductsView
} from '../masterdata';
using {managed} from '@sap/cds/common';


@assert.unique : {unifiedtemplate : [
    product,
    plant
]}
entity UnifiedTemplate {
    key ID      : Integer;
        product : String;
        plant   : String;
}


// To analyse SQL generated for this use => cds compile template.cds --tosql
@assert.unique : {unifiedtemplatev2 : [
    product,
    plant
]}
entity UnifiedTemplateV2 : managed {
    key ID      : Integer;
        product : Association to ProductsView;
        plant   : String;
}
