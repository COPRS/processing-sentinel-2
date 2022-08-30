package eu.csgroup.coprs.ps2.pw.l0u.repository;

import eu.csgroup.coprs.ps2.pw.l0u.model.SessionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public interface SessionEntityRepository extends MongoRepository<SessionEntity, String> {

    List<SessionEntity> findAllByJobOrderCreated(boolean jobOrderCreated);

    List<SessionEntity> findAllByReadyAndJobOrderCreated(boolean ready, boolean jobOrderCreated);

    List<SessionEntity> findAllByRawCompleteAndReadyAndJobOrderCreated(boolean rawComplete, boolean ready, boolean jobOrderCreated);

    void deleteAllByNameIn(Set<String> nameSet);

}
