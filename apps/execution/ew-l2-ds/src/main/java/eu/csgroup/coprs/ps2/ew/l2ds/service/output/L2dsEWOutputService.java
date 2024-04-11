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

package eu.csgroup.coprs.ps2.ew.l2ds.service.output;

import eu.csgroup.coprs.ps2.core.ew.service.l2.L2EWOutputService;
import eu.csgroup.coprs.ps2.ew.l2ds.service.setup.L2dsEWCleanupService;
import org.springframework.stereotype.Service;

@Service
public class L2dsEWOutputService extends L2EWOutputService {

    protected L2dsEWOutputService(L2dsEWMessageService messageService, L2dsEWCleanupService cleanupService, L2dsEWUploadService uploadService) {
        super(messageService, cleanupService, uploadService);
    }

}
