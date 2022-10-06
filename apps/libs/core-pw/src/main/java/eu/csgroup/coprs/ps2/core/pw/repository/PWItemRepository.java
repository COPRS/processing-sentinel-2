package eu.csgroup.coprs.ps2.core.pw.repository;

import eu.csgroup.coprs.ps2.core.pw.model.PWItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface PWItemRepository<I extends PWItemEntity> extends MongoRepository<I, String> {

    List<I> findAllByJobOrderCreated(boolean jobOrderCreated);

    List<I> findAllByReadyAndJobOrderCreated(boolean ready, boolean jobOrderCreated);

    void deleteAllByNameIn(Set<String> nameSet);

}
