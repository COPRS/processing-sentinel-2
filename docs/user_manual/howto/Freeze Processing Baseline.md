# Freeze Processing Baseline

## Processing Baseline

> The processing baseline [GIPP](https://sentinels.copernicus.eu/web/sentinel/user-guides/sentinel-2-msi/definitions) (PROBAS- L1C, PROBA2 - L2A) are configuration files that evolve over the Mission lifetime. This evolution is done to manage the introduction of new product features or processing steps (e.g. introduction of the geometric refinement), and the correction of errors in the processing chain.

More informations about the Processing Baseline : <https://sentinels.copernicus.eu/web/sentinel/technical-guides/sentinel-2-msi/processing-baseline>

## Freeze the Processing Baseline for L1

You are able to freeze the Processing Baseline through the file `IDPSC_EXE_export.json` from the [S2IPF_Orchestrator_Launcher](https://github.com/CS-SI/S2IPF_Orchestrator_Launcher/blob/main/config/IDPSC_EXE_export.json) dependency.

For e.g. the Level 1 execution worker is using the file above so the Processing Baseline is set to `04.00`.

## Freeze the Processing Baseline for L2

The Processing Baseline cannot be frozen as easily for the L2 as one processor (Sen2Cor) will use the GIPP PROBA2 if it's found, thus ignoring the Processing Baseline previously set from the L1 processing.

If you want to have the same Processing Baseline for L1 and L2 (for e.g. `04.00`), you have to :

1. Remove the ingestion of the GIPP PROBA2 from the system (in production-common) by editing the matchRegex from :

   `app.ingestion-auxip-trigger.ingestion-trigger.polling.inbox3.matchRegex=^(S2)(A|B|_)_(OPER|TEST)_((AUX|GIP)_[0-9A-Z_]{7})(.*).TGZ$`  
   to :  
   `app.ingestion-auxip-trigger.ingestion-trigger.polling.inbox3.matchRegex=^(S2)(A|B|_)_(OPER|TEST)_((AUX_|GIP_)(?!PROBA2_)[0-9A-Z_]{7})(.*).TGZ$`

2. Keep the last PROBA2 with the same Processing Baseline of the L1 and delete the others from the metadata catalogue :

   |                                   GIP_PROBA2                                    |          File_Version           |Action|
   |:--------------------------------------------------------------------------------|:--------------------------------|:-----|
   |S2__OPER_GIP_PROBA2_MPC__20220314T000400_V20220316T030000_21000101T000000_B00.HDR|<File_Version>0400</File_Version>|Keep  |
   |S2__OPER_GIP_PROBA2_MPC__20221206T000509_V20150622T000000_21000101T000000_B00.HDR|<File_Version>0509</File_Version>|Delete|
   |S2__OPER_GIP_PROBA2_MPC__20221206T000509_V20221206T064000_21000101T000000_B00.HDR|<File_Version>0509</File_Version>|Delete|
   |S2__OPER_GIP_PROBA2_MPC__20221206T000509_V20221206T073000_21000101T000000_B00.HDR|<File_Version>0509</File_Version>|Delete|

   Note 1 : You may retrieve the File_Version directly in the .HDR file from the bucket of the S2 aux files.  
   Note 2 : Elasticsearch Delete API : <https://www.elastic.co/guide/en/elasticsearch/reference/7.15/docs-delete.html>
