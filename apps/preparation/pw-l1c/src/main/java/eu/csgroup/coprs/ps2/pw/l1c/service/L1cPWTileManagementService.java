package eu.csgroup.coprs.ps2.pw.l1c.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class L1cPWTileManagementService {

    private static final String TILE_FILE = "L1B/GET_TILE_LIST/output/TILE_LIST_FILE/tile_list_file.xml";
    private static final String TILE_TAG = "Tile_Id";

    public Set<String> listTiles(L1ExecutionInput executionInput) {

        log.info("Extracting tile List");

        final List<String> tileList = FileContentUtils.extractXmlTagValues(Paths.get(executionInput.getOutputFolder(), TILE_FILE), TILE_TAG);

        log.info("Found {} tiles to process", tileList.size());

        return new HashSet<>(tileList);
    }

}
