package eu.csgroup.coprs.ps2.pw.l0u.model;

import eu.csgroup.coprs.ps2.core.pw.model.PWItemMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SessionMapper extends PWItemMapper<Session, SessionEntity> {

}
