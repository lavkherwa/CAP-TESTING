namespace my.nativeview;

using {my.gtt.Sample} from './NativeTable';
using {my.masterdata.Products} from '../masterdata';

@cds.persistence.exists
entity SessionContext {
    key user   : String;
        locale : String;
}

entity ProductExtension as
    select from Products
    join Sample
        on Products.productID = Sample.field1
    {
        key Products.productID as productId,
            Products.name      as productName,
            Products.desc      as productDesc,
            Sample.field1      as productExt1,
            Sample.field2      as productExt2
    };
