package eu.csgroup.coprs.ps2.pw.l0c.model;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DatastripMapper {

    Datastrip toDatastrip(DatastripEntity datastripEntity);

    DatastripEntity toDatastripEntity(Datastrip datastrip);

}
