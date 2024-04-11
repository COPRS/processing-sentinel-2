# Copyright 2023 CS Group
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#safescale  gw-ivv-cluster  ~  curl -X GET http://10.98.60.209:9200/s2_aux/_search?pretty -H 'Content-type:application/json' -d
# '{"query":{"match":{"productType":"AUX_UT1UTC"}}}'

{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : 4.6249723,
    "hits" : [
      {
        "_index" : "s2_aux",
        "_type" : "_doc",
        "_id" : "S2__OPER_AUX_UT1UTC_PDMC_20220407T000000_V20220408T000000_20230407T000000.TGZ",
        "_score" : 4.6249723,
        "_source" : {
          "productFamily" : "S2_AUX",
          "validityStopTime" : "2023-04-07T00:00:00.000000Z",
          "missionId" : "S2",
          "productClass" : "OPER",
          "creationTime" : "2022-04-07T00:00:00.000000Z",
          "insertionTime" : "2022-05-13T09:37:43.326886Z",
          "satelliteId" : "_",
          "validityStartTime" : "2022-04-08T00:00:00.000000Z",
          "productName" : "S2__OPER_AUX_UT1UTC_PDMC_20220407T000000_V20220408T000000_20230407T000000.TGZ",
          "productType" : "AUX_UT1UTC"
        }
      }
    ]
  }
}

# safescale  gw-ivv-cluster  ~  curl -X GET http://10.98.60.209:9200/s2_aux/_search?pretty

{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 152,
      "relation" : "eq"
    },
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "s2_aux",
        "_type" : "_doc",
        "_id" : "S2B_OPER_GIP_R2DENT_MPC__20170206T103039_V20170101T000000_21000101T000000_B09.TGZ",
        "_score" : 1.0,
        "_source" : {
          "productFamily" : "S2_AUX",
          "validityStopTime" : "2100-01-01T00:00:00.000000Z",
          "bandIndexId" : "09",
          "missionId" : "S2",
          "productClass" : "OPER",
          "creationTime" : "2017-02-06T10:30:39.000000Z",
          "insertionTime" : "2022-05-13T09:37:38.713642Z",
          "satelliteId" : "B",
          "validityStartTime" : "2017-01-01T00:00:00.000000Z",
          "productName" : "S2B_OPER_GIP_R2DENT_MPC__20170206T103039_V20170101T000000_21000101T000000_B09.TGZ",
          "productType" : "GIP_R2DENT"
        }
      },
      {
        "_index" : "s2_aux",
        "_type" : "_doc",
        "_id" : "S2B_OPER_GIP_R2WAFI_MPC__20170206T103047_V20170101T000000_21000101T000000_B09.TGZ",
        "_score" : 1.0,
        "_source" : {
          "productFamily" : "S2_AUX",
          "validityStopTime" : "2100-01-01T00:00:00.000000Z",
          "bandIndexId" : "09",
          "missionId" : "S2",
          "productClass" : "OPER",
          "creationTime" : "2017-02-06T10:30:47.000000Z",
          "insertionTime" : "2022-05-13T09:37:40.972313Z",
          "satelliteId" : "B",
          "validityStartTime" : "2017-01-01T00:00:00.000000Z",
          "productName" : "S2B_OPER_GIP_R2WAFI_MPC__20170206T103047_V20170101T000000_21000101T000000_B09.TGZ",
          "productType" : "GIP_R2WAFI"
        }
      },
      {
        "_index" : "s2_aux",
        "_type" : "_doc",
        "_id" : "S2B_OPER_GIP_R2MACO_MPC__20170206T103040_V20170101T000000_21000101T000000_B09.TGZ",
        "_score" : 1.0,
        "_source" : {
          "productFamily" : "S2_AUX",
          "validityStopTime" : "2100-01-01T00:00:00.000000Z",
          "bandIndexId" : "09",
          "missionId" : "S2",
          "productClass" : "OPER",
          "creationTime" : "2017-02-06T10:30:40.000000Z",
          "insertionTime" : "2022-05-13T09:37:40.998560Z",
          "satelliteId" : "B",
          "validityStartTime" : "2017-01-01T00:00:00.000000Z",
          "productName" : "S2B_OPER_GIP_R2MACO_MPC__20170206T103040_V20170101T000000_21000101T000000_B09.TGZ",
          "productType" : "GIP_R2MACO"
        }
      },
      {
        "_index" : "s2_aux",
        "_type" : "_doc",
        "_id" : "S2B_OPER_GIP_R2DENT_MPC__20170206T103039_V20170101T000000_21000101T000000_B08.TGZ",
        "_score" : 1.0,
        "_source" : {
          "productFamily" : "S2_AUX",
          "validityStopTime" : "2100-01-01T00:00:00.000000Z",
          "bandIndexId" : "08",
          "missionId" : "S2",
          "productClass" : "OPER",
          "creationTime" : "2017-02-06T10:30:39.000000Z",
          "insertionTime" : "2022-05-13T09:37:41.032179Z",
          "satelliteId" : "B",
          "validityStartTime" : "2017-01-01T00:00:00.000000Z",
            "productName" : "S2B_OPER_GIP_R2DENT_MPC__20170206T103039_V20170101T000000_21000101T000000_B08.TGZ",
          "productType" : "GIP_R2DENT"
        }
      },
      {
        "_index" : "s2_aux",
        "_type" : "_doc",
        "_id" : "S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B03.TGZ",
        "_score" : 1.0,
        "_source" : {
          "productFamily" : "S2_AUX",
          "validityStopTime" : "2100-01-01T00:00:00.000000Z",
          "bandIndexId" : "03",
          "missionId" : "S2",
          "productClass" : "OPER",
          "creationTime" : "2017-05-12T11:47:36.000000Z",
          "insertionTime" : "2022-05-13T09:37:41.063324Z",
          "satelliteId" : "B",
          "validityStartTime" : "2017-03-22T00:00:00.000000Z",
          "productName" : "S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B03.TGZ",
          "productType" : "GIP_VIEDIR"
        }
      },
      {
        "_index" : "s2_aux",
        "_type" : "_doc",
        "_id" : "S2B_OPER_GIP_R2L2NC_MPC__20170206T103040_V20170101T000000_21000101T000000_B04.TGZ",
        "_score" : 1.0,
        "_source" : {
          "productFamily" : "S2_AUX",
          "validityStopTime" : "2100-01-01T00:00:00.000000Z",
          "bandIndexId" : "04",
          "missionId" : "S2",
          "productClass" : "OPER",
          "creationTime" : "2017-02-06T10:30:40.000000Z",
          "insertionTime" : "2022-05-13T09:37:41.107169Z",
          "satelliteId" : "B",
          "validityStartTime" : "2017-01-01T00:00:00.000000Z",
          "productName" : "S2B_OPER_GIP_R2L2NC_MPC__20170206T103040_V20170101T000000_21000101T000000_B04.TGZ",
          "productType" : "GIP_R2L2NC"
        }
      },
      {
        "_index" : "s2_aux",
        "_type" : "_doc",
        "_id" : "S2B_OPER_GIP_R2L2NC_MPC__20170206T103040_V20170101T000000_21000101T000000_B12.TGZ",
        "_score" : 1.0,
        "_source" : {
          "productFamily" : "S2_AUX",
          "validityStopTime" : "2100-01-01T00:00:00.000000Z",
          "bandIndexId" : "12",
          "missionId" : "S2",
          "productClass" : "OPER",
          "creationTime" : "2017-02-06T10:30:40.000000Z",
          "insertionTime" : "2022-05-13T09:37:41.137357Z",
          "satelliteId" : "B",
          "validityStartTime" : "2017-01-01T00:00:00.000000Z",
          "productName" : "S2B_OPER_GIP_R2L2NC_MPC__20170206T103040_V20170101T000000_21000101T000000_B12.TGZ",
          "productType" : "GIP_R2L2NC"
        }
      },
      {
        "_index" : "s2_aux",
        "_type" : "_doc",
        "_id" : "S2B_OPER_GIP_R2DENT_MPC__20170206T103039_V20170101T000000_21000101T000000_B01.TGZ",
        "_score" : 1.0,
        "_source" : {
          "productFamily" : "S2_AUX",
          "validityStopTime" : "2100-01-01T00:00:00.000000Z",
          "bandIndexId" : "01",
          "missionId" : "S2",
          "productClass" : "OPER",
          "creationTime" : "2017-02-06T10:30:39.000000Z",
          "insertionTime" : "2022-05-13T09:37:41.221261Z",
          "satelliteId" : "B",
          "validityStartTime" : "2017-01-01T00:00:00.000000Z",
          "productName" : "S2B_OPER_GIP_R2DENT_MPC__20170206T103039_V20170101T000000_21000101T000000_B01.TGZ",
          "productType" : "GIP_R2DENT"
        }
      },
      {
        "_index" : "s2_aux",
        "_type" : "_doc",
        "_id" : "S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B02.TGZ",
        "_score" : 1.0,
        "_source" : {
          "productFamily" : "S2_AUX",
          "validityStopTime" : "2100-01-01T00:00:00.000000Z",
          "bandIndexId" : "02",
          "missionId" : "S2",
          "productClass" : "OPER",
          "creationTime" : "2017-05-12T11:47:36.000000Z",
          "insertionTime" : "2022-05-13T09:37:41.249269Z",
          "satelliteId" : "B",
          "validityStartTime" : "2017-03-22T00:00:00.000000Z",
          "productName" : "S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_B02.TGZ",
          "productType" : "GIP_VIEDIR"
        }
      },
      {
        "_index" : "s2_aux",
        "_type" : "_doc",
        "_id" : "S2B_OPER_GIP_TILPAR_MPC__20170206T103032_V20170101T000000_21000101T000000_B00.TGZ",
        "_score" : 1.0,
        "_source" : {
          "productFamily" : "S2_AUX",
          "validityStopTime" : "2100-01-01T00:00:00.000000Z",
          "bandIndexId" : "00",
          "missionId" : "S2",
          "productClass" : "OPER",
          "creationTime" : "2017-02-06T10:30:32.000000Z",
          "insertionTime" : "2022-05-13T09:37:41.274952Z",
          "satelliteId" : "B",
          "validityStartTime" : "2017-01-01T00:00:00.000000Z",
          "productName" : "S2B_OPER_GIP_TILPAR_MPC__20170206T103032_V20170101T000000_21000101T000000_B00.TGZ",
          "productType" : "GIP_TILPAR"
        }
      }
    ]
  }
}