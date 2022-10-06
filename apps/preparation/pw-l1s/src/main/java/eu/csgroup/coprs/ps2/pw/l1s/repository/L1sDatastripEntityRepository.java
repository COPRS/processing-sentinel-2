package eu.csgroup.coprs.ps2.pw.l1s.repository;

import eu.csgroup.coprs.ps2.core.pw.repository.PWItemRepository;
import eu.csgroup.coprs.ps2.pw.l1s.model.L1sDatastripEntity;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface L1sDatastripEntityRepository extends PWItemRepository<L1sDatastripEntity> {

    List<L1sDatastripEntity> findAllByGrCompleteAndReadyAndJobOrderCreated(boolean grComplete, boolean ready, boolean jobOrderCreated);

}
