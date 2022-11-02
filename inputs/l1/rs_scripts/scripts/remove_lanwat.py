import sys, os
from xml.etree import ElementTree as ET

def removeDataObjectSection(results):
    for result in results:
        print result
        tree = ET.parse(result)
        root = tree.getroot()
        dataObjectSection = root.find('dataObjectSection')

        for item in dataObjectSection:
            if 'LanWatMask_Band_0m' in item.attrib['ID']:
                root.find('dataObjectSection').remove(item)

        tree.write(result)

   
def main():
    results = []
    msk_lanwat = '_MSK_LANWAT_'
    for root, dirs, files in os.walk(sys.argv[1]):
        if 'manifest.safe' in files:
            print files
            results.append(os.path.join(root, 'manifest.safe'))
        for file in files:
            if msk_lanwat in file:
                os.remove('{root}/{file}'.format(root=root, file=file))

    try:
        removeDataObjectSection(results)
    except:
        pass

if __name__ == '__main__':
    main()
