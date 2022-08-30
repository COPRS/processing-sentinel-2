package eu.csgroup.coprs.ps2.pw.l0c.repository;

import eu.csgroup.coprs.ps2.pw.l0c.model.DatastripEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public interface DatastripEntityRepository extends MongoRepository<DatastripEntity, String> {

    List<DatastripEntity> findAllByJobOrderCreated(boolean jobOrderCreated);

    List<DatastripEntity> findAllByReadyAndJobOrderCreated(boolean ready, boolean jobOrderCreated);

    void deleteAllByNameIn(Set<String> nameSet);

}
