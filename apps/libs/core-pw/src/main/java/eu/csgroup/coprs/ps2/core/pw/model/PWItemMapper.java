package eu.csgroup.coprs.ps2.core.pw.model;

public interface PWItemMapper<S extends PWItem, I extends PWItemEntity> {

    S toItem(I itemEntity);
    I toItemEntity(S item);

}
