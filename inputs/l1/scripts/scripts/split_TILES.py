import argparse
import math


def get_ponderation(idx,total):
    return total * (-0.005*idx+1.005) / 100


def main():
    #INPUTS ARGUMENTS
    parser = argparse.ArgumentParser(description='This script create an inventory metadata file for each DS and TILE')
    parser.add_argument('-t', '--total', dest='total', type=int, required=True, help='Total number of tile')
    parser.add_argument('-p', '--parts', dest='parts', type=int, required=True, help='Total number of parts')
    parser.add_argument('-n', '--number', dest='number', type=int, required=True, help='Part number')
    args = parser.parse_args()
    all_tiles = list(range(1, args.total))
    total_complexity = 0
    for t in all_tiles:
        total_complexity = total_complexity + get_ponderation(t,args.total)
    print("Total complexity = "+str(total_complexity))
    number = args.number
    nb_per_part = total_complexity *1.0 / args.parts
    print("Complexity per group: "+str(nb_per_part))
    print(get_ponderation(all_tiles[1], args.total))
    groups = {}
    for g in range(1, args.parts+1):
        print("Group "+str(g))
        parallel_tile_ident = ""
        job = 0
        keep=True
        idx = 0
        elem = all_tiles[0]
        while job < nb_per_part and keep:
            if job + get_ponderation(elem, args.total) <= nb_per_part:
                job = job + get_ponderation(elem, args.total)
                if parallel_tile_ident == "":
                    parallel_tile_ident = '%03d' % (elem)
                else:
                    parallel_tile_ident = parallel_tile_ident + '-%03d' % (elem)
                all_tiles.pop(idx)
                if len(all_tiles) > idx:
                    elem = all_tiles[idx]
                else:
                    keep = False
                    break
            else:
                idx = idx+1
                if idx >= len(all_tiles):
                    keep = False
                    break
                elem = all_tiles[idx]
        print("Tile Ident: "+parallel_tile_ident)
        print("Group complexity: " + str(job))
        groups[g] = parallel_tile_ident
    idx = 1
    while len(all_tiles) > 0:
        groups[idx] = groups[idx] + '-%03d' % (all_tiles[0])
        all_tiles.pop(0)
        idx = (idx + 1)%(args.parts+1)

    for f in groups.items():
        print(f)

    #nb_per_part = int(math.ceil(args.total * 1.0 / args.parts))
    #parallel_tile_ident = ""
    #if number <= args.total:
    #    parallel_tile_ident = '%03d' % (number)
    #for i in range(number+1, args.total+1):
    #    if (i-number)% args.parts == 0:
    #        parallel_tile_ident = parallel_tile_ident + '-%03d' % (i)
    #if len(parallel_tile_ident) != 0:
    #    print(parallel_tile_ident)

if __name__ == "__main__":
    main()