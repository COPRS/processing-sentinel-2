package eu.csgroup.coprs.ps2.pw.l0c.model;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DatastripMapper {

    Datastrip toDatastrip(DatastripEntity datastripEntity);

    DatastripEntity toDatastripEntity(Datastrip datastrip);

}
