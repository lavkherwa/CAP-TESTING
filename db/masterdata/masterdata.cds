namespace my.masterdata;

entity Products {
    key productID : String;
        name      : localized String;
        desc      : String;
}


entity ProductsView as
    select from Products {
        productID,
        name,
        desc
    };
