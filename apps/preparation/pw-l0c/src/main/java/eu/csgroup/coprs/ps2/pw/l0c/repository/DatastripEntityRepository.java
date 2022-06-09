package eu.csgroup.coprs.ps2.pw.l0c.repository;

import eu.csgroup.coprs.ps2.pw.l0c.model.DatastripEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DatastripEntityRepository extends MongoRepository<DatastripEntity, String> {

    List<DatastripEntity> findAllByFailedAndJobOrderCreated(boolean failed, boolean jobOrderCreated);

    List<DatastripEntity> findAllByReadyAndFailedAndJobOrderCreated(boolean ready, boolean failed, boolean jobOrderCreated);

}
