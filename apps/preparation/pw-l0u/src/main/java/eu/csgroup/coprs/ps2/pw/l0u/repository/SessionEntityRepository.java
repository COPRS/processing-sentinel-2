package eu.csgroup.coprs.ps2.pw.l0u.repository;

import eu.csgroup.coprs.ps2.core.pw.repository.PWItemRepository;
import eu.csgroup.coprs.ps2.pw.l0u.model.SessionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SessionEntityRepository extends PWItemRepository<SessionEntity> {

    List<SessionEntity> findAllByRawCompleteAndReadyAndJobOrderCreated(boolean rawComplete, boolean ready, boolean jobOrderCreated);

}
