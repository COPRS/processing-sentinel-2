package eu.csgroup.coprs.ps2.pw.l2.repository;

import eu.csgroup.coprs.ps2.core.pw.repository.PWItemRepository;
import eu.csgroup.coprs.ps2.pw.l2.model.L2DatastripEntity;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface L2DatastripEntityRepository extends PWItemRepository<L2DatastripEntity> {

    List<L2DatastripEntity> findAllByTlCompleteAndReadyAndJobOrderCreated(boolean tlComplete, boolean ready, boolean jobOrderCreated);

}
