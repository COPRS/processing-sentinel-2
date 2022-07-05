package eu.csgroup.coprs.ps2.pw.l0u.repository;

import eu.csgroup.coprs.ps2.pw.l0u.model.SessionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public interface SessionEntityRepository extends MongoRepository<SessionEntity, String> {

    List<SessionEntity> findAllByFailedAndJobOrderCreated(boolean failed, boolean jobOrderCreated);

    List<SessionEntity> findAllByFailedOrJobOrderCreated(boolean failed, boolean jobOrderCreated);

    List<SessionEntity> findAllByReadyAndFailedAndJobOrderCreated(boolean ready, boolean failed, boolean jobOrderCreated);

    List<SessionEntity> findAllByRawCompleteAndReadyAndFailedAndJobOrderCreated(boolean rawComplete, boolean ready, boolean failed, boolean jobOrderCreated);

    void deleteAllByNameIn(Set<String> nameSet);

}
