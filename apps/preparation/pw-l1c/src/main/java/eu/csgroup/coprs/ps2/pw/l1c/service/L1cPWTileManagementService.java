package eu.csgroup.coprs.ps2.pw.l1c.service;

import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.l01.OrchestratorMode;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class L1cPWTileManagementService {

    private static final String TILE_FILE = "GET_TILE_LIST/output/TILE_LIST_FILE/tile_list_file.xml";
    private static final String TILE_TAG = "<Tile_Id>";

    public Set<String> listTiles(L1ExecutionInput executionInput) {

        log.info("Extracting tile List");

        final Path outputFolderPath = Paths.get(executionInput.getOutputFolder());
        String l1bFolder = Files.exists(outputFolderPath.resolve(OrchestratorMode.L1B.getMode())) ? OrchestratorMode.L1B.getMode() : OrchestratorMode.L1B_NO_GRI.getMode();

        final int tileCount = FileContentUtils.grepAll(outputFolderPath.resolve(l1bFolder).resolve(TILE_FILE), TILE_TAG).size();
        Set<String> tileList = IntStream.range(1, tileCount + 1).mapToObj(index -> String.format("%03d", index)).collect(Collectors.toSet());

        log.info("Found {} tiles to process", tileList.size());

        return tileList;
    }

}
