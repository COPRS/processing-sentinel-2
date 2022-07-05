package eu.csgroup.coprs.ps2.pw.l0u.model;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SessionMapper {

    Session toSession(SessionEntity sessionEntity);

    SessionEntity toSessionEntity(Session session);

}
