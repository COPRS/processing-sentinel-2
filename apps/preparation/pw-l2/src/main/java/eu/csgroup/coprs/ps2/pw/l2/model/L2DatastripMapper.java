package eu.csgroup.coprs.ps2.pw.l2.model;

import eu.csgroup.coprs.ps2.core.pw.model.PWItemMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface L2DatastripMapper extends PWItemMapper<L2Datastrip, L2DatastripEntity> {

}
