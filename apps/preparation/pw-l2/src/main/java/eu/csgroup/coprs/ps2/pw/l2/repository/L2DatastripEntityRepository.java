/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.csgroup.coprs.ps2.pw.l2.repository;

import eu.csgroup.coprs.ps2.core.pw.repository.PWItemRepository;
import eu.csgroup.coprs.ps2.pw.l2.model.L2DatastripEntity;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface L2DatastripEntityRepository extends PWItemRepository<L2DatastripEntity> {

    List<L2DatastripEntity> findAllByTlCompleteAndReadyAndJobOrderCreated(boolean tlComplete, boolean ready, boolean jobOrderCreated);

}
