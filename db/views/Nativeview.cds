namespace my.nativeview;


@cds.persistence.exists
entity SessionContext {
    key user   : String;
        locale : String;
}
