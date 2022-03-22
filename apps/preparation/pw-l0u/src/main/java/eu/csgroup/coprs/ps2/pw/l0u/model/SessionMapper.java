package eu.csgroup.coprs.ps2.pw.l0u.model;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    Session toSession(SessionEntity sessionEntity);

    SessionEntity toSessionEntity(Session session);

}
