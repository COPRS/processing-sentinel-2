# coding=utf-8
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# author : Esquis Benjamin for CSGroup
#

# coding=utf-8
import copy
import re

from lxml import etree as et


class TileListFileReader(object):

    def __init__(self, filename):
        self.tree_hdr = et.parse(filename)
        self.root_hdr = self.tree_hdr.getroot()
        self.filename = filename

    def get_tile_ids(self, tiles):
        list_tile_xml = self.root_hdr.xpath(
            '//List_Tile_Id')
        tiles_number = [int(j) for j in tiles.split("-")]
        tiles_name = []
        for tile in list_tile_xml:
            if int(tile.attrib["Tile_Number"]) in tiles_number:
                tiles_name.append(tile.findtext("Tile_Id"))
        return tiles_name

    def get_all_tile_ids(self):
        list_tile_xml = self.root_hdr.xpath(
            '//List_Tile_Id')
        tiles_name = []
        for tile in list_tile_xml:
            tiles_name.append(tile.findtext("Tile_Id"))
        return tiles_name

    def write_to_file(self, filename):
        self.tree_hdr.write(filename, encoding="UTF-8")
